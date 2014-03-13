package net.canadensys.dwca2sql;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.common.base.Charsets;

public class GeneralTestCase {
	
	/**
	 * TestId general
	 * Test a resource with tricky characters for SQL : apostrophes and a value that ends with a backslash.
	 * We also cover a backslash in the middle of a value, this should be left as is.
	 */
	@Test
	public void testDwcaWithDefaultField() {
		String testId = "general";
		
		String sourceFolder = TestCaseUtil.getResourceFile("/vascan_dwca_test_general").getAbsolutePath();
		String destinationFile = TestCaseUtil.getDestinationFilePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFile("/",testId).getAbsolutePath();
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","mysql"};
		new Dwca2SQLMain(args);
		
		try {

			assertTrue(new File(destinationFile).exists());
			assertTrue(new File(expectedFile).exists());
			assertTrue(FileUtils.contentEqualsIgnoreEOL(new File(destinationFile), new File(expectedFile),Charsets.UTF_8.name()));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

}
