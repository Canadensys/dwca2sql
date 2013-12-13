/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql.config.database;


/**
 * Specific configuration for MySQL databases
 *
 */
public class MySQLDatabaseConfig extends AbstractDatabaseConfig{
	
	private String createTableStatement = "CREATE TABLE %s (%s) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;"; 
	
	private char stringSeparatorchar = '\'';
	private char columnEscapeChar = '`';

	@Override
	public char getStringSeparatorChar() {
		return stringSeparatorchar;
	}

	@Override
	public char getColumnEscapeChar() {
		return columnEscapeChar;
	}
	
	@Override
	public String getCreateTableStatement(){
		return createTableStatement;
	}

}
