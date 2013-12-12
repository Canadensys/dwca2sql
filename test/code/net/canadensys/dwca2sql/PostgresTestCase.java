/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * JUnit 4 test class to generate simple tests on a Postgresql compliant file.
 * @author canandesys
 *
 */
public class PostgresTestCase {
	
	/**
	 * TestId postgres1
	 * Test a resource with only one default column (language) and no specific
	 * data type.
	 */
	@Test
	public void testDwcaWithDefaultField() {
		String testId = "postgres1";
		
		String sourceFolder = TestCaseUtil.TEST_RESOURCES_FOLDER+"vascan_dwca_test_1";
		String destinationFile = TestCaseUtil.getDestinationFileRelativePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFileRelativePath(testId);
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","postgres"};
		new Dwca2SQLMain(args);
		
		try {
			org.junit.Assert.assertTrue(FileUtils.contentEquals(new File(destinationFile), new File(expectedFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
