/*
	Copyright (c) 2011 Canadensys
*/
package net.canadensys.dwca2sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import net.canadensys.dwca2sql.config.AbstractDatabaseConfig;
import net.canadensys.dwca2sql.config.ConfigValidator;
import net.canadensys.dwca2sql.config.DatabaseConfigFactory;
import net.canadensys.dwca2sql.config.Dwca2SQLConfig;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.dwc.text.UnsupportedArchiveException;


/**
 * Main class of Dwca2SQL for command line usage.
 * @author canadensys
 *
 */
public class Dwca2SQLMain {
	
	private static final String YES = "y";
	private static final String NO = "n";
	private static final String ANSWER_HELP = "Please answer y or n";
	
	private static final String OVERWRITE_DESTINATION = "The destination file %s already exists. Do you want to overwrite it?";
	private static final String OVERWRITE_UNZIP_FOLDER = "The destination folder to unzip the file %s already exists. Do you want to overwrite it?";
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		LogManager.getRootLogger().setLevel(Level.INFO);
		new Dwca2SQLMain(args);
	}
	
	public Dwca2SQLMain(String[] args) {
		
		Dwca2SQLConfig dwcaConfig = CliManager.parseCommandLine(args);
		
		if(ConfigValidator.validateConfig(dwcaConfig)){
			
			//make sure the user want to overwrite destination file
			if(!dwcaConfig.isForceMode() && new File(dwcaConfig.getDestinationFile()).exists()){
				if(!userWantsToContinue(String.format(OVERWRITE_DESTINATION, dwcaConfig.getDestinationFile()))){
					System.out.println("aborted by user");
					return;
				}
			}
			
			Archive dwcArchive;
			try {
				if(FilenameUtils.isExtension(dwcaConfig.getSourceFile(), ArchiveStreamFactory.ZIP)){
					//make sure the user want to overwrite destination file
					if(!dwcaConfig.isForceMode() && new File(getUnzipFolder(dwcaConfig.getSourceFile())).exists()){
						if(!userWantsToContinue(String.format(OVERWRITE_UNZIP_FOLDER, dwcaConfig.getSourceFile()))){
							System.out.println("aborted by user");
							return;
						}
					}
					
					unzipFile(dwcaConfig.getSourceFile());
					dwcArchive = ArchiveFactory.openArchive(new File(FilenameUtils.removeExtension(dwcaConfig.getSourceFile())));
				}
				else{
					dwcArchive = ArchiveFactory.openArchive(new File(dwcaConfig.getSourceFile()));
				}

				ArchiveFile dwcaCore = dwcArchive.getCore();
				
				AbstractDatabaseConfig dbConfigObj = DatabaseConfigFactory.buildDatabaseConfig(dwcaConfig.getDatabaseType());
				DwcaSQLProcessor dwcaSQLProcessor = new DwcaSQLProcessor(dwcaConfig, dbConfigObj);
				Dwca2SQLReport report = dwcaSQLProcessor.processDwcaCore(dwcaCore);
				//Print result
				report.printReport();
			} catch (UnsupportedArchiveException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getUnzipFolder(String zipFilePath){
		return FilenameUtils.removeExtension(zipFilePath);
	}
	
	private static boolean unzipFile(String zipFilePath){
		InputStream is;
		ArchiveInputStream in = null;
		OutputStream out  = null;
		try {
			is = new FileInputStream(zipFilePath);
			String folderName = getUnzipFolder(zipFilePath);
			new File(folderName).mkdir();
			
			in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
			
			ZipArchiveEntry entry = (ZipArchiveEntry)in.getNextEntry();
			while(entry != null){
				out = new FileOutputStream(new File(folderName, entry.getName()));
				IOUtils.copy(in, out);
				out.close();
				out = null;
				entry = (ZipArchiveEntry)in.getNextEntry();
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (ArchiveException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {}
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		return true;
	}
	
	/**
	 * Asks a yes or no question to the user
	 * @param question
	 * @return did the user answer yes
	 */
	private static boolean userWantsToContinue(String question){
		System.out.println(question);
		Scanner sc = new Scanner(System.in);
	    boolean isValidAnswer = true;
	    boolean isYes = false;
		do {
		     String answer = sc.next();
		     if(answer.equalsIgnoreCase(YES)){
		    	 isYes = true;
		    	 isValidAnswer = true;
		     }
		     else if (answer.equalsIgnoreCase(NO)){
		    	 isYes = false;
		    	 isValidAnswer = true;
		     }
		     else{
		    	 isValidAnswer = false;
		    	 System.out.println(ANSWER_HELP);
		     }
		} while(!isValidAnswer);
		return isYes;
	}
}
