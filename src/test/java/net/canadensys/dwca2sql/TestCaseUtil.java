/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Test case related utility class
 * @author canandesys
 *
 */
public class TestCaseUtil {
	
	public static final String WORK_FOLDER = "work/";
	
	/**
	 * Returns a standardized destination file path for the testId.
	 * @param testId an identifier for the test
	 * @return path of the file matching the testId in the work folder.
	 */
	public static String getDestinationFilePath(String testId){
		return WORK_FOLDER+"actual_result_test_"+testId+".sql";
	}
	
	/**
	 * Return a File object representing a file or a folder available in the classpath
	 * @param file
	 * @return
	 */
	public static File getResourceFile(String file){
		URL sFileURL = TestCaseUtil.class.getResource(file);
		URI sFileURI = null;
		try {
			sFileURI = sFileURL.toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		return new File(sFileURI);
	}
	
	/**
	 * Returns a standardized expected File object available in the classpath.
	 * @param testId an identifier for the test
	 * @return File object or null
	 */
	public static File getExpectedFile(String root, String testId){
		URL dFileURL = TestCaseUtil.class.getResource(root+"expected_result_test_"+testId+".sql");
		URI dFileURI = null;
		try {
			dFileURI = dFileURL.toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		return new File(dFileURI);
	}

}
