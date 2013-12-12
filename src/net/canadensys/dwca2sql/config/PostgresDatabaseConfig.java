/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql.config;

/**
 * Specific configuration for Postgres databases
 *
 */
public class PostgresDatabaseConfig extends AbstractDatabaseConfig{

	private char stringSeparatorchar = '\'';
	private char columnEscapeChar = '"';

	@Override
	public char getStringSeparatorChar() {
		return stringSeparatorchar;
	}

	@Override
	public char getColumnEscapeChar() {
		return columnEscapeChar;
	}

}
