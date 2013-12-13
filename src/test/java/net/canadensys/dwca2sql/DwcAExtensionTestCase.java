package net.canadensys.dwca2sql;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Ensure that extension related statements are included in the SQL file.
 * The content of those line is out of scope of this test case.
 * @author canadensys
 *
 */
public class DwcAExtensionTestCase {
	
	@Test
	public void testDwcaWithDefaultField() {
		String testId = "with_ext";
		
		String sourceFolder = TestCaseUtil.getResourceFile("/vascan_zipped_dwca.zip").getAbsolutePath();
		String destinationFile = TestCaseUtil.getDestinationFilePath(testId);
		String expectedFile = TestCaseUtil.getExpectedFile("/",testId).getAbsolutePath();
		
		String[] args = {"-ci", "-s",sourceFolder,"-o",destinationFile,"-f","-d","postgres"};
		new Dwca2SQLMain(args);
		
		try {
			assertTrue(new File(destinationFile).exists());
			assertTrue(new File(expectedFile).exists());
			
			//Since the order of the extension is not guarantee, the order of the content is unpredictable
			String destinationFileContent = StringUtils.chomp(FileUtils.readFileToString(new File(destinationFile)));
			String expectedFileContent = StringUtils.chomp(FileUtils.readFileToString(new File(expectedFile)));
			
			
			List<String> destinationLines = new ArrayList<String>(Arrays.asList(destinationFileContent.split(";")));
			List<String> expectedLines = new ArrayList<String>(Arrays.asList(expectedFileContent.split(";")));
			assertEquals(expectedLines.size(), destinationLines.size());
			
			//check that all the CREATE TABLE statements were generated
			String createTableStatement;
			for(String currLine : expectedLines){
				if(currLine.startsWith("CREATE TABLE") || currLine.startsWith("INSERT INTO") ){
					createTableStatement = StringUtils.substringBefore(currLine, "(");
					assertTrue(destinationFileContent.contains(createTableStatement));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

}
