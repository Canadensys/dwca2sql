package net.canadensys.dwca2sql;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

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
		String sourceFolder = TestCaseUtil.getResourceFile("/vascan_dwca_test_1").getAbsolutePath();
		String destinationFile = TestCaseUtil.getDestinationFilePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFile("/",testId).getAbsolutePath();
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","mysql"};
		new Dwca2SQLMain(args);
		
		try {
			assertTrue(new File(destinationFile).exists());
			assertTrue(new File(expectedFile).exists());
			assertTrue(FileUtils.contentEquals(new File(destinationFile), new File(expectedFile)));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * TestId mysql2
	 * Test a resource with no default column and no specific data type.
	 */
	@Test
	public void testDwcaWithoutDefaultField() {
		String testId = "mysql2";
		
		String sourceFolder = TestCaseUtil.getResourceFile("/vascan_dwca_test_2").getAbsolutePath();
		String destinationFile = TestCaseUtil.getDestinationFilePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFile("/",testId).getAbsolutePath();
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","mysql"};
		new Dwca2SQLMain(args);
		
		try {
			assertTrue(new File(destinationFile).exists());
			assertTrue(new File(expectedFile).exists());
			assertTrue(FileUtils.contentEquals(new File(destinationFile), new File(expectedFile)));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * TestId mysql3
	 * Test a resource with no default column and no specific data type.
	 */
	@Test
	public void testNumberOfInsertStatement() {
		String testId = "mysql3";
		
		String sourceFolder = TestCaseUtil.getResourceFile("/dwca_test_3").getAbsolutePath();
		String destinationFile = TestCaseUtil.getDestinationFilePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFile("/",testId).getAbsolutePath();
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","mysql","--max-row-per-insert","10"};
		new Dwca2SQLMain(args);
		
		try {
			assertTrue(new File(destinationFile).exists());
			assertTrue(new File(expectedFile).exists());
			assertTrue(FileUtils.contentEquals(new File(destinationFile), new File(expectedFile)));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
