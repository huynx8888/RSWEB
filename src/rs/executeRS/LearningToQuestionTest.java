package rs.executeRS;
import rs.treeStructure.*;
import rs.utility.Common;

public class LearningToQuestionTest {
	public static void main(String args[]) {
		//Delete all temporary files in directory
		Common common = new Common("TrainningData");
		common.deleteAllTemporaryFiles();
		
		//Start execute decision tree for recommendation
		DecisionTree dt = new DecisionTree();
		dt.Run();
	}

}
