/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql;

/**
 * Test case related utility class
 * @author canandesys
 *
 */
public class TestCaseUtil {
	
	public static final String TEST_RESOURCES_FOLDER = "test/resources/";
	
	/**
	 * Returns a standardized destination file path for the testId
	 * @param testId an identifier for the test
	 * @return
	 */
	public static String getDestinationFileRelativePath(String testId){
		return TEST_RESOURCES_FOLDER+"actual_result_test_"+testId+".sql";
	}
	
	/**
	 * Returns a standardized expected file path for the testId
	 * @param testId an identifier for the test
	 * @return
	 */
	public static String getExpectedFileRelativePath(String testId){
		return TEST_RESOURCES_FOLDER+"expected_result_test_"+testId+".sql";
	}

}
