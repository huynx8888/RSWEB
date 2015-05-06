package rs.svmlight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Readweight extends Thread {
	//model name
	private String modelName;
	
	public Readweight (String modelName) {
		this.modelName = modelName;
	}
	
	//implement method run
	public void run () {
		File f = new File(this.modelName);
		while (!f.exists()) {
			try {
				sleep(100);
				System.out.println("File " + this.modelName +" not found!");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//read weight from modelName
		String weight = null;
		
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(this.modelName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((weight = bufferedReader.readLine()) != null) {
            	System.out.println(weight);
            }	

            // Always close files.
            bufferedReader.close();			
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + this.modelName + "'");				
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + this.modelName + "'");					
        }

	}

}
