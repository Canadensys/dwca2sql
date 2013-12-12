/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql.config;

/**
 * Factory responsible for creating the instance of a specific database configuration object.
 * @author canandesys
 *
 */
public class DatabaseConfigFactory {
	
	public enum DatabaseTypeEnum {MYSQL,POSTGRES};
	
	/**
	 * This function is responsible for building the proper database configuration object.
	 * If no database configuration can be found with the provided name the Postgres one will
	 * be returned.
	 * @param databaseName
	 * @return
	 */
	public static AbstractDatabaseConfig buildDatabaseConfig(String databaseName){
		if(databaseName != null){
			if(databaseName.equalsIgnoreCase(DatabaseTypeEnum.MYSQL.toString())){
				return new MySQLDatabaseConfig();
			}
			else if(databaseName.equalsIgnoreCase(DatabaseTypeEnum.POSTGRES.toString())){
				return new PostgresDatabaseConfig();
			}
		}
		return new PostgresDatabaseConfig();
	}
}
