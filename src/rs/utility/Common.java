package rs.utility;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author huynx
 * @date 2014-12-07
 *
 */

public class Common {
	private String folderName;

	/**
	 *
	 * @param folderName
	 */
	public Common (String folderName) {
		this.folderName = folderName;
	}

	/**
	 * Delete all temprory files
	 */
	public void deleteAllTemporaryFiles () {
		File directory = new File(folderName);

		//make sure directory exists
		if(!directory.exists()){
			System.out.println("Directory does not exist.");
			System.exit(0);
		} else {
			try{
				delete(directory);
			} catch(IOException e){
	               e.printStackTrace();
	               System.exit(0);
	           }
		}

		System.out.println("Done");
	}

	/**
	 *
	 * Delete files method
	 */
	private void delete(File file) throws IOException {
		if(file.isDirectory()){
			//list all the directory contents
			String files[] = file.list();
			for (String temp : files) {
				//construct the file structure
				File fileDelete = new File(file, temp);
				//delete this file
				fileDelete.delete();
			}
		}
	}


	//Get and Set methods
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}



}
