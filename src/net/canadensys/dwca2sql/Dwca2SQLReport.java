/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * This class is used to hold the result of the dwca processing and to display
 * the report on the standard output.
 * @author canadensys
 */
public class Dwca2SQLReport {
	
	private String targetFile;
	private List<String> parsingError = null;
	private List<String> parsingWarning = null;
	private long totalTime;
	private int totalRow;
	private Exception exception = null;
	
	public Dwca2SQLReport(String targetFile){
		this.targetFile = targetFile;
	}
	
	/**
	 * Sets result for processing without exceptions.
	 * @param parsingError if none, this can be null or empty
	 * @param totalTime in millisecond
	 * @param totalRow
	 * @param parsingWarning if none, this can be null or empty
	 */
	public void setResult(List<String> parsingError, long totalTime, int totalRow, List<String> parsingWarning){
		this.parsingError = parsingError;
		this.totalTime = totalTime;
		this.totalRow = totalRow;
		this.parsingWarning = parsingWarning;
	}
	
	/**
	 * Sets the result as a triggered exception
	 * @param exception
	 */
	public void setResult(Exception exception){
		this.exception = exception;
	}

	public List<String> getParsingError() {
		return parsingError;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public int getTotalRow() {
		return totalRow;
	}
	
	public String getTargetFile() {
		return targetFile;
	}
	
	/**
	 * Checks if the process was successful by making sure there is no exceptions or parsing errors.
	 * @return
	 */
	public boolean isSuccessful(){
		return (exception==null) && ((parsingError==null)||(parsingError.isEmpty()));
	}
	
	/**
	 * Prints result on the standard output
	 */
	public void printReport(){
		
		if(isSuccessful()){
			System.out.println("SQL file was successfully generated:");
		}
		else{
			System.out.println("SQL file could not be generated:");
		}
		
		if(exception != null){
			exception.printStackTrace();
			return;
		}
		
		if(parsingWarning != null && !parsingWarning.isEmpty()){
			System.out.println("Parsing warning(s):");
			System.out.println(StringUtils.join(parsingWarning, '\n'));
			System.out.println("You should fix the above warnings but the file " + targetFile + " is still valid.");
		}
		
		if(parsingError == null || parsingError.isEmpty()){
			System.out.println("-Processing time: "+totalTime + " ms");
			System.out.println("-Number of rows: "+totalRow);
			System.out.println("-File :" + targetFile);
		}
		else{
			System.out.println("Parsing ERRORS:");
			System.out.println(StringUtils.join(parsingError, '\n'));
			if(parsingError.size()==DwcaSQLProcessor.MAX_ERROR_LINE){
				System.out.println("...");
			}
			System.out.print("WARNING, the file " + targetFile + " is incomplete and should not be used.");
		}
	}
}
