/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql.config.database;

/**
 * Abstract class to allow different implementations for specific databases.
 *
 */
public abstract class AbstractDatabaseConfig {
	
	//SQL statement
	protected String defaultCreateTableStatement = "CREATE TABLE %s (%s);"; 
	protected String insertIntoStatement = "INSERT INTO %s (%s) VALUES ";
	
	//Default values
	protected String stringColumnType = "TEXT";
	protected String integerColumnType = "INTEGER";
	protected String booleanColumnType = "BOOL";
	protected String decimalColumnType = "DECIMAL(12,2)";
	protected String dateColumnType = "DATE";
	
	protected String nullValue = "NULL";
	
	/**
	 * Character used to quote a String
	 * @return
	 */
	public abstract char getStringSeparatorChar();
	
	/**
	 * Escape character for column name
	 * Ex. MySQL use `
	 * @return
	 */
	public abstract char getColumnEscapeChar();
	
	/**
	 * String used to represent the type String in the database
	 * Ex. VARCHAR(255)
	 * @return
	 */
	public String getStringColumnType(){
		return stringColumnType;
	}
	
	/**
	 * String used to represent the type Integer in the database
	 * Ex. INTEGER
	 * @return
	 */
	public String getIntegerColumnType(){
		return integerColumnType;
	}
	
	/**
	 * String used to represent the type Boolean in the database
	 * Ex. BOOL, if booleans are not supported return valid type (ex. TINYINT).
	 * @return
	 */
	public String getBooleanColumnType() {
		return booleanColumnType;
	}

	/**
	 * String used to represent the type decimal(Double) in the database
	 * Ex. DECIMAL(12,2)
	 * @return
	 */
	public String getDecimalColumnType() {
		return decimalColumnType;
	}

	/**
	 * String used to represent the type Date in the database
	 * Ex. Date
	 * @return
	 */
	public String getDateColumnType() {
		return dateColumnType;
	}
	
	/**
	 * String used to represent a NULL value in the database
	 * @return
	 */
	public String getNullValue(){
		return nullValue;
	}
	
	/**
	 * Returns a String representing a CREATE TABLE statement that allows String.format
	 * with the table name and comma-separated table columns and types. @See AbstractDatabaseConfig.CREATE_STM
	 * Could be overloaded for specific database implementation.
	 */
	public String getCreateTableStatement(){
		return defaultCreateTableStatement;
	}
	
	/**
	 * Returns a String representing a INSERT INTO statement that allows String.format
	 * with the table name, comma-separated table columns and comma-separated column values. @See AbstractDatabaseConfig.INSERT_STM
	 * Could be overloaded for specific database implementation.
	 */
	public String getInsertIntoStatement(){
		return insertIntoStatement;
	}

}
