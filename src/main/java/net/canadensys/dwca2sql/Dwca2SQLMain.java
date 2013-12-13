package net.canadensys.dwca2sql;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import net.canadensys.dwca2sql.config.ConfigValidator;
import net.canadensys.dwca2sql.config.Dwca2SQLConfig;
import net.canadensys.dwca2sql.config.database.AbstractDatabaseConfig;
import net.canadensys.dwca2sql.config.database.DatabaseConfigFactory;
import net.canadensys.utils.ZipUtils;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
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
				File sourceFile = new File(dwcaConfig.getSourceFile());
				if(FilenameUtils.isExtension(dwcaConfig.getSourceFile(), ArchiveStreamFactory.ZIP)){
					//make sure the user want to overwrite destination file
					if(!dwcaConfig.isForceMode() && new File(getUnzipFolder(dwcaConfig.getSourceFile())).exists()){
						if(!userWantsToContinue(String.format(OVERWRITE_UNZIP_FOLDER, dwcaConfig.getSourceFile()))){
							System.out.println("aborted by user");
							return;
						}
					}
					
					ZipUtils.unzipFileOrFolder(sourceFile,null);
					dwcArchive = ArchiveFactory.openArchive(new File(FilenameUtils.removeExtension(dwcaConfig.getSourceFile())));
				}
				else{
					dwcArchive = ArchiveFactory.openArchive(sourceFile);
				}

				//ArchiveFile dwcaCore = dwcArchive.getCore();
				
				AbstractDatabaseConfig dbConfigObj = DatabaseConfigFactory.buildDatabaseConfig(dwcaConfig.getDatabaseType());
				DwcaSQLProcessor dwcaSQLProcessor = new DwcaSQLProcessor(dwcaConfig, dbConfigObj);
				
				List<Dwca2SQLReport> reportList = dwcaSQLProcessor.processDarwinCoreArchive(dwcArchive);
				for(Dwca2SQLReport report : reportList){
					//Print result
					report.printReport();
				}
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
