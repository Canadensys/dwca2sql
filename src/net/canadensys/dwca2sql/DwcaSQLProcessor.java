/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.canadensys.dwca2sql.config.AbstractDatabaseConfig;
import net.canadensys.dwca2sql.config.Dwca2SQLConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveFile;

/**
 * This class is used to process a DarwinCore archive into a SQL file.
 * This file can then be used to populate a database.
 * @author canadensys
 */
public class DwcaSQLProcessor {
	private static Logger LOGGER = Logger.getLogger(DwcaSQLProcessor.class.getName());
	
	private enum ColumnTypeEnum {STRING, INTEGER, DECIMAL};
	
	public static final int MAX_ERROR_LINE = 25;
	private static final int BUFFER_SIZE = 500; //number of lines to keep before flushing to the file
	private static final String OUTPUT_FILE_ENCODING = "utf-8";
	private static final char NULL_CHAR = '\0';
	
	//This Regex will match all the strings that ends with one or more backslashes
	//By default, the regular expressions ^ and $ ignore line terminators and
	//only match at the beginning and the end, respectively, of the entire input sequence.
	private static final Pattern ENDING_BACKSLASH_PATTERN = Pattern.compile("\\\\+$");
	
	private String ID_COLUMN_NAME = "id";
	
	private Dwca2SQLConfig appConfig = null;
	private AbstractDatabaseConfig dbConfig = null;
	private String createStatementQuotedColumn;
	private String insertStatementQuotedColumn;
	
	public DwcaSQLProcessor(Dwca2SQLConfig appConfig, AbstractDatabaseConfig dbConfig){
		if(appConfig==null || dbConfig == null){
			System.out.println("Please set proper configuration objects");
			return;
		}
		this.appConfig = appConfig;
		this.dbConfig = dbConfig;
		this.createStatementQuotedColumn = dbConfig.getColumnEscapeChar() + "%s" + dbConfig.getColumnEscapeChar() + " %s";
		this.insertStatementQuotedColumn = dbConfig.getColumnEscapeChar() + "%s" + dbConfig.getColumnEscapeChar();
	}

	public Dwca2SQLReport processDwcaCore(ArchiveFile dwcaCore) {
		ArrayList<String> toFile = new ArrayList<String>();
		Dwca2SQLReport report = new Dwca2SQLReport(appConfig.getDestinationFile());
		long start = System.currentTimeMillis();
		
		//use the last part of the rowType as table name
		String tableName = dwcaCore.getRowType().substring(dwcaCore.getRowType().lastIndexOf("/")+1).toLowerCase();
		if(appConfig.getDestinationTablePrefix()!= null){
			tableName = appConfig.getDestinationTablePrefix()+"_"+tableName;
		}
		
		//CREATE TABLE
		if(appConfig.isCreateTableStatementRequired()){
			toFile.add(generateCreateStatement(dwcaCore,tableName));
		}
		
		//INSERT INTO
		if(appConfig.isInsertStatementRequired()){
			//INSERT statement preparation
			InsertPreparationResult prepResult =  prepareInsertStatement(dwcaCore);
			String columnNames = StringUtils.join(prepResult.getColumnNamesList(),',');
			ColumnTypeEnum[] columnType = prepResult.getColumnType().toArray(new ColumnTypeEnum[0]);
			String columnsDefaultValuesStr = StringUtils.join(prepResult.getColumnDefaultValues(), ",");
			String insertIntoStatement = String.format(dbConfig.getInsertIntoStatement(), tableName, columnNames);
			
			//INSERT statement for each rows
			StringBuilder columnValues = new StringBuilder(500);
			String[] currLine;
			Iterator<String[]> rowsIt;
			
			try {
				org.apache.commons.io.FileUtils.writeLines(new File(appConfig.getDestinationFile()), OUTPUT_FILE_ENCODING, toFile, false);
				toFile.clear();
				
				rowsIt = dwcaCore.getCSVReader().iterator();
				int col = 0;
				int rowQty = 0;
				int insertRowQty = 0;
				int rowNumber = 0;
				List<String> parsingErrorList = new ArrayList<String>();
				List<String> parsingWarningList = new ArrayList<String>();
				
				while(rowsIt.hasNext()){
					//delete the previous values
					columnValues.delete(0, columnValues.length());
					currLine = rowsIt.next();
					//reset column counter
					col = 0;
					for(String currElement : currLine){
						if(col == 0){
							//check the number of rows for this INSERT statement
							if(insertRowQty == 0){
								columnValues.append(insertIntoStatement);
							}
							columnValues.append("(");
							//increment this at each line
							insertRowQty++;
						}
						else{
							columnValues.append(",");
						}
						
						//make sure that the string doesn't contain NULL character (\0).
						//NULL characters make the script invalid.
						if(currElement.indexOf(NULL_CHAR) >=0){
							currElement = StringUtils.remove(currElement, NULL_CHAR);
							handleNullCharacterWarning(currElement, rowNumber, prepResult.getColumnNamesList().get(col), parsingWarningList);
						}
						
						switch(columnType[col]){
							case STRING:
								columnValues.append(dbConfig.getStringSeparatorChar()+escapeSql(currElement) + dbConfig.getStringSeparatorChar());
								break;
							case INTEGER:
								if(StringUtils.isBlank(currElement)){
									columnValues.append(dbConfig.getNullValue());
								}
								else{
									try{
										Integer.parseInt(currElement);
									}
									catch (NumberFormatException e) {
										handleParsingException(currElement, rowNumber, prepResult.getColumnNamesList().get(col), "INTEGER", parsingErrorList);
									}
									columnValues.append(currElement);
								}
								break;
							case DECIMAL:
								if(StringUtils.isBlank(currElement)){
									columnValues.append(dbConfig.getNullValue());
								}
								else{
									try{
										Double.parseDouble(currElement);
									}
									catch (NumberFormatException e) {
										handleParsingException(currElement, rowNumber, prepResult.getColumnNamesList().get(col), "DECIMAL", parsingErrorList);
									}
									columnValues.append(currElement);
								}
								break;
						}
						col++;
					}
					rowNumber++;
					
					//check for fields with default values
					if(!StringUtils.isBlank(columnsDefaultValuesStr)){
						columnValues.append(",");
						columnValues.append(columnsDefaultValuesStr);
					}
					//if we reach the MaxRowsPerInsertStatement or we are at the last row
					if(insertRowQty == appConfig.getMaxRowPerInsertStatement() || !rowsIt.hasNext()){
						columnValues.append(");");
						insertRowQty = 0;
					}
					else{
						columnValues.append("),");
					}
					
					//we do not record lines once we hit a parsing error
					if(parsingErrorList.isEmpty()){
						//add values to INSERT INTO statement
						toFile.add(columnValues.toString());
						rowQty++;
						if(rowQty == BUFFER_SIZE){
							org.apache.commons.io.FileUtils.writeLines(new File(appConfig.getDestinationFile()), OUTPUT_FILE_ENCODING, toFile, true);
							toFile.clear();
							rowQty = 0;
						}
					}
				}//end rowsIt.hasNext()
				
				//set result
				report.setResult(parsingErrorList, System.currentTimeMillis()-start, rowNumber, parsingWarningList);
				
				if(parsingErrorList.isEmpty()){
					org.apache.commons.io.FileUtils.writeLines(new File(appConfig.getDestinationFile()), OUTPUT_FILE_ENCODING, toFile, true);
				}
			} catch (IOException e) {
				report.setResult(e);
			}
		}
		return report;
	}
	
	/**
	 * This function creates a CREATE TABLE sql statement.
	 * @param dwcaCore
	 * @param tableName
	 * @return
	 */
	private String generateCreateStatement(ArchiveFile dwcaCore, String tableName){
		ArchiveField indexField = dwcaCore.getId();
		ArrayList<String> indexedColumns = new ArrayList<String>();
		ArrayList<String> nonIndexedColumns = new ArrayList<String>();
		List<ArchiveField> sortedFieldList = dwcaCore.getFieldsSorted();
		
		if(indexField != null && indexField.getIndex() != null){
			indexedColumns.add(indexField.getIndex(), String.format(createStatementQuotedColumn, ID_COLUMN_NAME, dbConfig.getStringColumnType()));
		}
		String createStmtColumn;
		for(ArchiveField currArField : sortedFieldList){
			createStmtColumn = String.format(createStatementQuotedColumn, currArField.getTerm().simpleName(), getSQLColumnType(currArField));
			if(currArField.getIndex() != null){
				indexedColumns.add(createStmtColumn);
			}
			else{
				nonIndexedColumns.add(createStmtColumn);
			}
		}
		indexedColumns.addAll(nonIndexedColumns);
		String columnsDef = StringUtils.join(indexedColumns, ',');
		
		String createStatement = (String.format(dbConfig.getCreateTableStatement(), tableName, columnsDef));
		return createStatement;
	}
	
	/**
	 * The purpose of this method is to compute some data before looping each record.
	 * The data computed includes the first part of the INSERT SQL statement, the default values for each columns
	 * and the type of each columns.
	 * @param dwcaCore
	 * @return preparation result 
	 */
	private InsertPreparationResult prepareInsertStatement(ArchiveFile dwcaCore){
		InsertPreparationResult analysisResult = new InsertPreparationResult();
		ArrayList<String> indexedColumns = new ArrayList<String>();
		ArrayList<String> nonIndexedColumns = new ArrayList<String>();
		ArrayList<ColumnTypeEnum> indexedColumnsType = new ArrayList<ColumnTypeEnum>();
		ArrayList<ColumnTypeEnum> nonIndexedColumnsType = new ArrayList<ColumnTypeEnum>();
		List<String> columnDefaultValues = new ArrayList<String>();
		
		List<ArchiveField> sortedFieldList = dwcaCore.getFieldsSorted();
		
		//is this mandatory ?
		ArchiveField indexField = dwcaCore.getId();
		if(indexField != null && indexField.getIndex() != null){
			indexedColumns.add(indexField.getIndex(), String.format(insertStatementQuotedColumn,ID_COLUMN_NAME));
			indexedColumnsType.add(indexField.getIndex(), getColumnTypeEnum(indexField));
		}
		
		for(ArchiveField currArField : sortedFieldList){
			if(currArField.getIndex() != null){
				indexedColumns.add(currArField.getIndex(), String.format(insertStatementQuotedColumn,currArField.getTerm().simpleName()));
				indexedColumnsType.add(currArField.getIndex(), getColumnTypeEnum(currArField));
			}
			else{
				nonIndexedColumns.add(String.format(insertStatementQuotedColumn,currArField.getTerm().simpleName()));
				nonIndexedColumnsType.add(getColumnTypeEnum(currArField));
				columnDefaultValues.add(dbConfig.getStringSeparatorChar() + escapeSql(currArField.getDefaultValue()) + dbConfig.getStringSeparatorChar());
			}
		}
		
		//add all non-indexed columns to the end
		indexedColumns.addAll(nonIndexedColumns);
		
		//combine the 2 lists
		indexedColumnsType.addAll(nonIndexedColumnsType);
		
		analysisResult.setColumnNamesList(indexedColumns);
		analysisResult.setColumnType(indexedColumnsType);
		analysisResult.setColumnDefaultValues(columnDefaultValues);

		return analysisResult;
	}
	
	/**
	 * Escapes the characters in a String to be suitable for a SQL query.
	 * This is NOT a solution to SQL injection.
	 * The function only handles apostrophes and strings that end with a backslash.
	 * TODO use canadensys jar instead of custom method
	 * @param str
	 * @return
	 */
	public static String escapeSql(String str){
		if(str == null){
			return null;
		}
		str = StringUtils.replace(str,"'", "''");
		//avoid to be escaped by an ending backslash
		if(str.endsWith("\\")){
			Matcher m = ENDING_BACKSLASH_PATTERN.matcher(str);
			if(m.find()){
				String backslahes = m.group();
				int index = str.lastIndexOf(backslahes);
				//double the number of backslashes
				String replacementStr = backslahes+backslahes;
				//replace the backslashes in the string
				StringBuilder sb = new StringBuilder(str);
				sb.replace(index, str.length(), replacementStr);
				return sb.toString();
			}
			else{
				System.out.println("The value " + str + " seems to end with a backslash but the Regex fails to find it.");
				System.out.println("This is a bug!");
			}
		}
		return str;
	}
	
	/**
	 * This function is used to map the ArchiveField type with the inner type. This allows
	 * to switch faster on the type (enum faster than String).
	 * @param af
	 * @return ColumnTypeEnum representing the inner type or null if the type was not found
	 */
	private ColumnTypeEnum getColumnTypeEnum(ArchiveField af){
		switch(af.getType()){
			case string : return ColumnTypeEnum.STRING;
			case integer : return ColumnTypeEnum.INTEGER;
			case bool : return ColumnTypeEnum.INTEGER;
			case decimal : return ColumnTypeEnum.DECIMAL;
			case date : return ColumnTypeEnum.STRING;
			case uri : return ColumnTypeEnum.STRING;
			default : LOGGER.fatal("Unable to map " + af.getType() + " to ColumnTypeEnum");
		}
		return null;
	}
	
	/**
	 * This function is used to map the ArchiveField type with the matching SQL type.
	 * @param af
	 * @return String representing the SQL type or null if the type was not found
	 */
	private String getSQLColumnType(ArchiveField af){
		switch(af.getType()){
			case string : return dbConfig.getStringColumnType();
			case integer : return dbConfig.getIntegerColumnType();
			case bool : return dbConfig.getBooleanColumnType();
			case decimal : return dbConfig.getDecimalColumnType();
			case date : return dbConfig.getDateColumnType();
			case uri : return dbConfig.getStringColumnType();
			default : LOGGER.fatal("Unable to map " + af.getType() + " to SQL type");
		}
		return null;
	}
	
	/**
	 * This method will format and record the error in a list if the maximum of error is not reached yet.
	 * @param currElement
	 * @param rowNumber
	 * @param colName
	 * @param expectedType
	 * @param rowErrorList list where the error will be added
	 */
	private void handleParsingException(String currElement,int rowNumber, String colName, String expectedType, List<String> rowErrorList){
		if(rowErrorList.size() < MAX_ERROR_LINE){
			rowErrorList.add("row " + rowNumber + ", " + colName + " could not be parsed as " + expectedType +": \""+currElement + "\"");
		}
	}
	
	/**
	 * This method will format and record the warning related to nul character in a list.
	 * @param currElement
	 * @param rowNumber
	 * @param colName
	 * @param parsingWarningList
	 */
	private void handleNullCharacterWarning(String currElement,int rowNumber, String colName, List<String> parsingWarningList){
		parsingWarningList.add("row " + rowNumber + ", " + colName + " contains nul character (\\0) in value "+"\""+currElement+"\".");
	}
	
	/**
	 * This inner class is used to hold the results from the Insert preparation.
	 */
	private class InsertPreparationResult{
		private List<String> columnNamesList;
		private List<ColumnTypeEnum> columnType;
		private List<String> columnDefaultValues;
		
		public void setColumnType(List<ColumnTypeEnum> columnType){
			this.columnType = columnType;
		}
		public List<ColumnTypeEnum> getColumnType(){
			return columnType;
		}
		
		public void setColumnDefaultValues(List<String> columnDefaultValues){
			this.columnDefaultValues = columnDefaultValues;
		}
		public List<String> getColumnDefaultValues(){
			return columnDefaultValues;
		}
		
		public void setColumnNamesList(List<String> columnNamesList){
			this.columnNamesList = columnNamesList;
		}
		public List<String> getColumnNamesList(){
			return columnNamesList;
		}
	}

}
