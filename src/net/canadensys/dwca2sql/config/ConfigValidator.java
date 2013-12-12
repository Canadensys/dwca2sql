/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql.config;

import java.io.File;

import net.canadensys.dwca2sql.CliManager;
import net.canadensys.dwca2sql.config.DatabaseConfigFactory.DatabaseTypeEnum;

/**
 * Configuration validator for Dwca2SQL.
 * @author canadensys
 */
public class ConfigValidator {
	
	/**
	 * Validates the Dwca2SQLConfig and print the usage upon error.
	 * @param dwcaConfig
	 * @return
	 */
	public static boolean validateConfig(Dwca2SQLConfig dwcaConfig){
		
		if(dwcaConfig == null){
			CliManager.printHelp();
			return false;
		}
		
		if(dwcaConfig.getSourceFile() == null || !(new File(dwcaConfig.getSourceFile()).exists())){
			System.out.println("You must provide an existing source file/folder.");
			CliManager.printHelp();
			return false;
		}
		
		if(!dwcaConfig.isCreateTableStatementRequired() && !dwcaConfig.isInsertStatementRequired()){
			System.out.println("You must specify to type of script to generate.");
			CliManager.printHelp();
			return false;
		}
		
		if(dwcaConfig.getDatabaseType() != null){
			try{
				DatabaseTypeEnum.valueOf(dwcaConfig.getDatabaseType().toUpperCase());
			}
			catch(IllegalArgumentException iaEx){
				System.out.println("Invalid database type :" + dwcaConfig.getDatabaseType());
				CliManager.printHelp();
				return false;
			}
		}
		
		if(dwcaConfig.getMaxRowPerInsertStatement()==null){
			System.out.println("Invalid max-row-per-insert. Must be an integer.");
			CliManager.printHelp();
			return false;
		}
		
		return true;
	}

}
