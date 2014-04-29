package net.canadensys.dwca2sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple helper class for String formating purpose.
 * @author cgendreau
 *
 */
public class SimpleSQLFormatHelper {
	
	//This Regex will match all the strings that ends with one or more backslashes
	//By default, the regular expressions ^ and $ ignore line terminators and
	//only match at the beginning and the end, respectively, of the entire input sequence.
	private static final Pattern ENDING_BACKSLASH_PATTERN = Pattern.compile("\\\\+$");
	
	private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("\\W");
	
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
	 * This method will format and replace all non alphanumeric character(s) in the componentName with an underscore (_).
	 * @param componentName table or a column used in SQL statement.
	 * @param format Format to be used with String.format()
	 * @param arg to be passed to String.format() if provided
	 * @return
	 */
	public static String formatSQLStatementComponent(String componentName, String format, String arg){
		if(StringUtils.isNotBlank(arg)){
			return String.format(format, NON_ALPHANUMERIC_PATTERN.matcher(componentName).replaceAll("_"), arg);
		}
		return String.format(format, NON_ALPHANUMERIC_PATTERN.matcher(componentName).replaceAll("_"));
	}
	
	/**
	 * @see formatSQLStatementComponent(String,String,String[])
	 */
	public static String formatSQLStatementComponent(String componentName, String format){
		return formatSQLStatementComponent(componentName, format, (String)null);
	}

}
