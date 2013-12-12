package net.canadensys.dwca2sql;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class GeneralTestCase {
	
	/**
	 * TestId general
	 * Test a resource with tricky characters for SQL : apostrophes and a value that ends with a backslash.
	 * We also cover a backslash in the middle of a value, this should be left as is.
	 */
	@Test
	public void testDwcaWithDefaultField() {
		String testId = "general";
		String sourceFolder = TestCaseUtil.TEST_RESOURCES_FOLDER+"vascan_dwca_test_general";
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

}
