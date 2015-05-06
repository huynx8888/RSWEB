package rs.svmlight;

import java.io.File;

/**
 *
 * @author huynx
 * @date 2014-12-06
 *
 */

public class Rankweight extends Thread {
	//file name
	private String fileName;
	//model name
	private String modelName;

	private int rankType;

	public Rankweight (String fileName, String modelName, int rankType) {
		this.fileName = fileName;
		this.modelName = modelName;
		this.rankType = rankType;
	}

	//implement method run
	public void run () {
		//Before generating model, we need to delete file
		File f = new File ("C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName);
		if (f.exists()) {//if file exists, we need to delete it to refresh
			boolean success = (f).delete();
			if (success) {
				System.out.println("The file " + this.modelName +" has been successfully deleted");
			}
		}

		/*
		using svmlight to calculate rankweight
		reference: http://svmlight.joachims.org/
		FORMAT DATA FILE is below:
		3 qid:1 1:1 2:1 3:0 4:0.2 5:0 # 1A
		2 qid:1 1:0 2:0 3:1 4:0.1 5:1 # 1B
		1 qid:1 1:0 2:1 3:0 4:0.4 5:0 # 1C
		1 qid:1 1:0 2:0 3:1 4:0.3 5:0 # 1D

		1 qid:2 1:0 2:0 3:1 4:0.2 5:0 # 2A
		2 qid:2 1:1 2:0 3:1 4:0.4 5:0 # 2B
		1 qid:2 1:0 2:0 3:1 4:0.1 5:0 # 2C
		1 qid:2 1:0 2:0 3:1 4:0.2 5:0 # 2D

		2 qid:3 1:0 2:0 3:1 4:0.1 5:1 # 3A
		3 qid:3 1:1 2:1 3:0 4:0.3 5:0 # 3B
		4 qid:3 1:1 2:0 3:0 4:0.4 5:1 # 3C
		1 qid:3 1:0 2:1 3:1 4:0.5 5:0 # 3D
		*/

		String filePath = "";

		//svm Rank
		if (rankType == 1) {
			filePath = "C:\\workspace\\RSWEB\\svm_rank_learn -c 0.0001 C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;
		}

		//************************************************


		//RankNet
		//C:\Study>java -jar RankLib.jar -train train.txt -ranker 1 -epoch 100 -layer 1 -node 10 -lr 0.00005 -save mymodel
		//String filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -ranker 1 -epoch 100 -layer 1 -node 10 -lr 0.00005 -train C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " -save C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;


		//AdaRank-specific parameters
		//C:\Study>java -jar RankLib.jar -train train.txt -ranker 3 -round 500 -tolerance 0.002 -max 5 -save mymodel
		//String filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -ranker 3 -round 500 -tolerance 0.002 -max 5 -train C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " -save C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;



		//Coordinate Ascent
		//C:\Study>java -jar RankLib.jar -train train.txt -ranker 4 -r 5 -i 25 -tolerance 0.001 -save mymodel
		//String filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -ranker 4 -r 5 -i 25 -tolerance 0.001 -train C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " -save C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;


		//LambdaMART
		//C:\Study>java -jar RankLib.jar -train train.txt -ranker 6 -tree 1000 -leaf 10 -shrinkage 0.1 -tc 256 -mls 1 -estop 100 -save mymodel
		//String filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -ranker 6 -tree 1000 -leaf 10 -shrinkage 0.1 -tc 256 -mls 1 -estop 100 -train C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " -save C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;



		//ListNet
		//java -jar RankLib.jar -train train.txt -ranker 7 -save mymodel
		//String filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -ranker 7 -train C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " -save C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;


		//Random Forests
		//C:\Study>java -jar RankLib.jar -train train.txt -ranker 8 -bag 300 -srate 1.0 -frate 0.3 -tree 1 -leaf 100 -shrinkage 0.1 -tc 256 -mls 1 -estop 100 -save mymodel
		//String filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -ranker 8 -bag 300 -srate 1.0 -frate 0.3 -tree 1 -leaf 100 -shrinkage 0.1 -tc 256 -mls 1 -estop 100 -train C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " -save C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;



		//rankboost
		if (rankType == 2) {
			filePath = "java -jar C:\\workspace\\RSWEB\\RankLib.jar -round 10 -ranker 2 -train C:\\workspace\\RSWEB\\TrainningData\\" + this.fileName + " -save C:\\workspace\\RSWEB\\TrainningData\\" + this.modelName;
		}


		try {
			Process p = Runtime.getRuntime().exec(filePath);
			System.out.println("finished generating model: " + this.modelName);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


}
