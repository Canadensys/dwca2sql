/*
	Copyright (c) 2011 Canadensys
*/

package net.canadensys.dwca2sql;

import net.canadensys.dwca2sql.config.Dwca2SQLConfig;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Class to handle command line (CLI) parameters.
 * @author canadensys
 */
public class CliManager {
	
	private static final Options cmdLineOptions;
	
	//private static final String CLI_HELP = "h";
	
	private static final String CLI_SOURCE = "s";
	private static final String CLI_CREATE = "c";
	private static final String CLI_INSERT = "i";
	private static final String CLI_OUTPUT = "o";
	private static final String CLI_PREFIX = "p";
	private static final String CLI_DATABASE = "d";
	private static final String CLI_FORCE = "f";
	
	private static final String CLI_MAX_ROW = "x";
	private static final String CLI_LONG_MAX_ROW = "max-row-per-insert";
	
	static{
		cmdLineOptions = new Options();
		cmdLineOptions.addOption(CLI_SOURCE, true, "source DarwinCore Archive file");
		cmdLineOptions.addOption(CLI_CREATE, false, "generate create table statement");
		cmdLineOptions.addOption(CLI_INSERT, false, "generate insert statement");
		cmdLineOptions.addOption(CLI_OUTPUT, true, "output file");
		cmdLineOptions.addOption(CLI_PREFIX, true, "table name prefix");
		cmdLineOptions.addOption(CLI_DATABASE, true, "database type (mysql,postgres)");
		cmdLineOptions.addOption(CLI_FORCE, false, "force, do not ask before overwriting existing file");
		
		cmdLineOptions.addOption(CLI_MAX_ROW,CLI_LONG_MAX_ROW, true, "maximum of rows to include in a single INSERT statement. Default 100.");
	}
	
	/**
	 * Function to parse the command line arguments into Dwca2SQLConfig.
	 * @param args
	 * @return
	 */
	public static Dwca2SQLConfig parseCommandLine(String[] args){
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmdLine;
		try {
			cmdLine = parser.parse(cmdLineOptions, args);	
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		String dwcaFilePath = cmdLine.getOptionValue(CLI_SOURCE);
		
		boolean createTable = cmdLine.hasOption(CLI_CREATE);
		boolean insertInto = cmdLine.hasOption(CLI_INSERT);
		String ouputFilePath = cmdLine.getOptionValue(CLI_OUTPUT);
		String tablePrefix = cmdLine.getOptionValue(CLI_PREFIX);
		String database = cmdLine.getOptionValue(CLI_DATABASE);
		boolean forceMode = cmdLine.hasOption(CLI_FORCE);
		String maxRowPerInsert = cmdLine.getOptionValue(CLI_MAX_ROW,Dwca2SQLConfig.DEFAULT_MAX_ROWS_PER_INSERT_STATEMENT.toString());
		
		Dwca2SQLConfig dwcaConfig = new Dwca2SQLConfig();
		dwcaConfig.setSourceFile(dwcaFilePath);
		dwcaConfig.setCreateTableStatementRequired(createTable);
		dwcaConfig.setInsertStatementRequired(insertInto);
		
		dwcaConfig.setDestinationFile(ouputFilePath);
		dwcaConfig.setDestinationTablePrefix(tablePrefix);
		dwcaConfig.setDatabaseType(database);
		dwcaConfig.setForceMode(forceMode);
		
		try{
			dwcaConfig.setMaxRowsPerInsertStatement(Integer.parseInt(maxRowPerInsert));
		}
		catch (NumberFormatException e) {
			dwcaConfig.setMaxRowsPerInsertStatement(null);
		}
		
		
		return dwcaConfig;
	}
	
	/**
	 * Print the "usage" to the standard output.
	 */
	public static void printHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "dwca2sql", cmdLineOptions );
	}

}
