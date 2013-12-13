package net.canadensys.dwca2sql;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

		String sourceFolder = TestCaseUtil.getResourceFile("/vascan_dwca_test_1").getAbsolutePath();
		String destinationFile = TestCaseUtil.getDestinationFilePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFile("/",testId).getAbsolutePath();

		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","postgres"};
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
