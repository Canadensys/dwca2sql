/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql;

import java.io.File;
import java.io.IOException;

import net.canadensys.dwca2sql.Dwca2SQLMain;

import org.apache.commons.io.FileUtils;
import org.junit.Test;


/**
 * JUnit 4 test class to generate simple tests on a mySQL compliant file.
 * @author canandesys
 *
 */
public class MySQLTestCase {

	
	/**
	 * TestId mysql1
	 * Test a resource with only one default column (language) and no specific
	 * data type.
	 */
	@Test
	public void testDwcaWithDefaultField() {
		String testId = "mysql1";
		String sourceFolder = TestCaseUtil.TEST_RESOURCES_FOLDER+"vascan_dwca_test_1";
		String destinationFile = TestCaseUtil.getDestinationFileRelativePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFileRelativePath(testId);
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","mysql"};
		new Dwca2SQLMain(args);
		
		try {
			org.junit.Assert.assertTrue(FileUtils.contentEquals(new File(destinationFile), new File(expectedFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * TestId mysql2
	 * Test a resource with no default column and no specific data type.
	 */
	@Test
	public void testDwcaWithoutDefaultField() {
		String testId = "mysql2";
		
		String sourceFolder = TestCaseUtil.TEST_RESOURCES_FOLDER+"vascan_dwca_test_2";
		String destinationFile = TestCaseUtil.getDestinationFileRelativePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFileRelativePath(testId);
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","mysql"};
		new Dwca2SQLMain(args);
		
		try {
			org.junit.Assert.assertTrue(FileUtils.contentEquals(new File(destinationFile), new File(expectedFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * TestId mysql3
	 * Test a resource with no default column and no specific data type.
	 */
	@Test
	public void testNumberOfInsertStatement() {
		String testId = "mysql3";
		
		String sourceFolder = TestCaseUtil.TEST_RESOURCES_FOLDER+"dwca_test_3";
		String destinationFile = TestCaseUtil.getDestinationFileRelativePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFileRelativePath(testId);
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","mysql","--max-row-per-insert","10"};
		new Dwca2SQLMain(args);
		
		try {
			org.junit.Assert.assertTrue(FileUtils.contentEquals(new File(destinationFile), new File(expectedFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
