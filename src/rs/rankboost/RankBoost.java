package rs.rankboost;

import java.io.File;

/**
 *
 * @author huynx
 * @date 2014-12-06
 *
 */

public class RankBoost extends Thread {
	//file name
	private String fileName;
	//model name
	private String modelName;

	//rank boost
	private String rankBoost;

	public RankBoost (String fileName, String modelName, String rankBoost) {
		this.fileName = fileName;
		this.modelName = modelName;
		this.rankBoost = rankBoost;
	}

	//implement method run
	public void run () {
		//Before generating model, we need to delete file
		File f = new File ("C:\\workspace\\RSWEB\\TrainningData\\" + this.rankBoost);
		if (f.exists()) {//if file exists, we need to delete it to refresh
			boolean success = (f).delete();
			if (success) {
				System.out.println("The file " + this.rankBoost +" has been successfully deleted");
			}
		}

		//rankboost
		String filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -load C:\\workspace\\RSWEB\\TrainningData\\" +  this.modelName + " -rank C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + "  -score C:\\workspace\\RSWEB\\TrainningData\\" + this.rankBoost;

		try {
			Process p = Runtime.getRuntime().exec(filePath);
			System.out.println("finished generating model: " + this.rankBoost);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


}
