package net.canadensys.dwca2sql.config;

/**
 * dwca2sql tool configurations
 * @author canadensys
 *
 */
public class Dwca2SQLConfig {
	
	public static final String DEFAULT_DESTINATION_FILE = "dwca2sqlresult.sql";
	public static final Integer DEFAULT_MAX_ROWS_PER_INSERT_STATEMENT = 100;
	
	private String sourceFile;
	
	private boolean createTableStatementRequired = false;
	private boolean insertStatementRequired = false;
	private boolean deleteStatementRequired = false;
	
	private String destinationTablePrefix = null;
	private String destinationFile;
	private String databaseType;
	private Integer maxRowPerInsertStatement = null;
	private boolean forceMode = false;
	
	public void setDestinationTablePrefix(String destinationTablePrefix){
		this.destinationTablePrefix = destinationTablePrefix;
	}
	/**
	 * 
	 * @return destination table prefix or null if not defined
	 */
	public String getDestinationTablePrefix(){
		return destinationTablePrefix;
	}
	
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	
	public void setCreateTableStatementRequired(boolean createTableStatementRequired) {
		this.createTableStatementRequired = createTableStatementRequired;
	}
	public boolean isCreateTableStatementRequired() {
		return createTableStatementRequired;
	}

	public void setInsertStatementRequired(boolean insertStatementRequired) {
		this.insertStatementRequired = insertStatementRequired;
	}
	public boolean isInsertStatementRequired() {
		return insertStatementRequired;
	}

	public void setDeleteStatementRequired(boolean deleteStatementRequired) {
		this.deleteStatementRequired = deleteStatementRequired;
	}
	public boolean isDeleteStatementRequired() {
		return deleteStatementRequired;
	}

	public void setDestinationFile(String destinationFile){
		this.destinationFile = destinationFile;
	}
	/**
	 * 
	 * @return the current destination file name or DEFAULT_DESTINATION_FILE if the current 
	 * destination file is null.
	 */
	public String getDestinationFile(){
		return (destinationFile == null ? DEFAULT_DESTINATION_FILE : destinationFile);
	}
	
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	
	/**
	 * Number of rows to put inside the same INSERT statement
	 * @return
	 */
	public Integer getMaxRowPerInsertStatement() {
		return maxRowPerInsertStatement;
	}
	public void setMaxRowsPerInsertStatement(Integer maxRowsPerInsertStatement) {
		this.maxRowPerInsertStatement = maxRowsPerInsertStatement;
	}
	
	public boolean isForceMode() {
		return forceMode;
	}
	public void setForceMode(boolean forceMode) {
		this.forceMode = forceMode;
	}

}
