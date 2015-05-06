package rs.treeStructure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import rs.dataStructure.*;
import rs.dbAccess.*;
import rs.svmlight.*;
import rs.utility.Utility;


/**
 *
 * @author huynx
 * @date 2014-12-06
 *
 */

public class DecisionTree extends Thread {
	//list of products
	private List<Product> listProducts;
	//list of reviews
	private List<Review> listReviews;
	//list of users
	private List<User> listUsers;

	//set of all user's attributes
	private List<Integer> listIndexOfAttributes;
	private Utility utility;

	//pruning tree thresold
	private int pruningTreeThresold;
	private Properties prop;

	//node of tree
	private Node root;
	private int nodeNumber;

	private int rankType;

	public DecisionTree (int rankType) {
		this.rankType = rankType;
	}

	/**
	 * Construction method
	 */
	public Node Run () {
		utility = new Utility();
		DbAccess db = new DbAccess();
		//Get list products
		listProducts = db.getProducts();
		//get list users
		listUsers = db.getUsers();
		//get list reviews
		listReviews = db.getReviews();
		//get index of attributes
		listIndexOfAttributes = db.getIncreaseIndexOfAttributes();

		//init properties file
		prop = new Properties();

		InputStream input = null;
		try {
			//input = new FileInputStream("C:\\workspace\\RSWEB\\resources\\resources.properties");
			input = this.getClass().getClassLoader().getResourceAsStream("resources.properties");
			prop.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//set Prunning thresold
		pruningTreeThresold = Integer.parseInt(prop.getProperty("PrunningTreeThresold"));

		nodeNumber = -1;

		//Create Decision tree
		return CreateTree();
	}

	private Node CreateTree() {
		if (root == null) {
			root = new Node();
			nodeNumber = nodeNumber + 1;
			String fileName = "TrainningNode" + nodeNumber + ".txt";
			String modelName = "ModelNode" + nodeNumber;

			//Calculate product's rank at node root
			List<Integer> liseUserId = new ArrayList<Integer>();
			for (int i = 0; i< listUsers.size(); i++) {
				liseUserId.add(listUsers.get(i).getUserId());
			}
			utility.generateTrainingData(fileName, listProducts, listReviews, liseUserId);

			//calculate product's rank weight at node root
			Rankweight rankWeight = new Rankweight(fileName, modelName, rankType);
			//start thread calculate rank weight
			rankWeight.run();

			//Get result from process
			File f = new File("C:\\workspace\\RSWEB\\TrainningData\\" + modelName);
			while (!f.exists()) {
				try {
					sleep(100);
					//System.out.println("File " + modelName +" not found!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//read weight from modelName
			List<Double> weightVector = new ArrayList<Double>();
			String[] lineData ;
			String weight = null;
	        try {
	            // FileReader reads text files in the default encoding.
	            FileReader fileReader =
	                new FileReader("C:\\workspace\\RSWEB\\TrainningData\\" + modelName);

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader =
	                new BufferedReader(fileReader);

	            while((weight = bufferedReader.readLine()) != null) {
	            	if (weight.indexOf(":") >= 0) break;
	            }

	            // Always close files.
	            bufferedReader.close();

	            lineData = weight.split(" ");
	            if (lineData.length >= 14) {
	            	for (int m = 1; m < lineData.length -1; m++) {
	            		String element = lineData[m];
	            		weightVector.add(Double.parseDouble(element.substring(element.indexOf(":") +1)));
	            	}
	            }
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println("Unable to open file '" + modelName + "'");
	        }
	        catch(IOException ex) {
	            System.out.println("Error reading file '" + modelName + "'");
	        }

	        //Calculate rank products
	        if (weightVector.size() > 0) {
	        	if (listProducts != null) {
	        		listProducts = utility.CalRank(listProducts, weightVector);
	        	}
	        }

	        //Calculate payoff for each users
	        if (listUsers != null) {
	        	for (int i = 0; i < listUsers.size(); i++) {
	        		Double payoff = utility.CalPayoff(listUsers.get(i), listProducts);
	        		listUsers.get(i).setPayoff(payoff);
	        	}
	        }

	        //********************* Process building decision tree********************//
    		//find the best attribute which can separate set of Users
    		int selectedAttribute = this.utility.maxPayOff(listIndexOfAttributes, listUsers);
    		root.setSelectedAttribute(selectedAttribute);

    		root.setListProducts(utility.getTopKlist(listProducts, listUsers, listReviews));
    		//root.setListProducts(listProducts);

    		//write top 10 recommended products to file
	        utility.writeRecommendedProductToFile("Top10RecommendedProductsAtNode" + nodeNumber, root.getListProducts());
    		root.setHeight(0);

    		//find U(selectedAttribute) and U(!selectedAttribute)
    		List<User> listUser1 = new ArrayList<User>(); //U(selectedAttribute)
    		List<User> listUser2 = new ArrayList<User>(); //U(!selectedAttribute)
    		for (int i = 0; i <= listUsers.size()-1; i++) {
    			User user = listUsers.get(i);
    			if (user.getAttributes().get(selectedAttribute) == 1) {
    				listUser1.add(user);
    			} else {
    				listUser2.add(user);
    			}
    		}

    		//remove choiceAttribute from setOfAttributes
    		List<Integer> tmpList = new ArrayList<Integer>();
    		for (int i = 0; i<= listIndexOfAttributes.size() -1; i++) {
    			if (listIndexOfAttributes.get(i) != selectedAttribute) {
    				tmpList.add(listIndexOfAttributes.get(i));
    			}
    		}

    		//Insert child node (left or right node)
    		insert(root, tmpList, listUser1, listUser2);

    		//Print Decision tree
    		PrintDecisionTree (root);

    		//Process recommendations for users
    		//LearningToquestion (root);

    		return root;
		}

		return null;
	}

    /**
     * This method is used to insert tree's nodes
     * @param root
     * @param tmpList
     * @param listUser1
     * @param listUser2
     */
    private void insert(Node rootNode, List<Integer> tmpList, List<User> listUser1, List<User> listUser2) {

    	//Add left node to rootNode
    	if (listUser1.size() >= 20) { //each node contains at least 20 users for recommendation
    		System.out.println("left node '" + listUser1.size()  + "'");
    		//Left node
    		Node leftNode = new Node();
    		leftNode.setParentNode(rootNode);
    		leftNode.setHeight(rootNode.getHeight() + 1);
    		rootNode.setLeftNode(leftNode);

			//Calculate product's rank at node root
			nodeNumber = nodeNumber + 1;
			String fileName = "TrainningNode" + nodeNumber + ".txt";
			String modelName = "ModelNode" + nodeNumber;

			List<Integer> listeUserId = new ArrayList<Integer>();
			for (int i = 0; i< listUser1.size(); i++) {
				listeUserId.add(listUser1.get(i).getUserId());
			}

			//get training product for left node
			List<Product> listLeftTrainingProduct = utility.getListTrainingProduct(listProducts, listReviews, listeUserId) ;
			utility.generateTrainingData(fileName, listLeftTrainingProduct, listReviews, listeUserId);

			//calculate product's rank weight at node root
			Rankweight rankWeight = new Rankweight(fileName, modelName, rankType);
			//start thread calculate rank weight
			rankWeight.run();

			//Get result from process
			File f = new File("C:\\workspace\\RSWEB\\TrainningData\\" + modelName);
			while (!f.exists()) {
				try {
					sleep(100);
					//System.out.println("File " + modelName +" not found!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//read weight from modelName
			List<Double> weightVector = new ArrayList<Double>();
			String[] lineData ;
			String weight = null;
	        try {
	            // FileReader reads text files in the default encoding.
	            FileReader fileReader =
	                new FileReader("C:\\workspace\\RSWEB\\TrainningData\\" + modelName);

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader =
	                new BufferedReader(fileReader);

	            while((weight = bufferedReader.readLine()) != null) {
	            	if (weight.indexOf(":") >= 0) break;
	            }

	            // Always close files.
	            bufferedReader.close();

	            lineData = weight.split(" ");
	            if (lineData.length >= 14) {
	            	for (int m = 1; m < lineData.length -1; m++) {
	            		String element = lineData[m];
	            		weightVector.add(Double.parseDouble(element.substring(element.indexOf(":") +1)));
	            	}
	            }
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println("Unable to open file '" + modelName + "'");
	        }
	        catch(IOException ex) {
	            System.out.println("Error reading file '" + modelName + "'");
	        }

	        //Calculate rank products
	        if (weightVector.size() > 0) {
	        	if (listLeftTrainingProduct != null) {
	        		listLeftTrainingProduct = utility.CalRank(listLeftTrainingProduct, weightVector);
	        	}
	        }

	        //Calculate payoff for each users
	        if (listUser1 != null) {
	        	for (int i = 0; i < listUser1.size(); i++) {
	        		Double payoff = utility.CalPayoff(listUser1.get(i), listLeftTrainingProduct);
	        		listUser1.get(i).setPayoff(payoff);
	        	}
	        }


	        //********************* Process building decision tree********************//
    		//Set top K list products at left node
    		leftNode.setListProducts(utility.getTopKlist(listLeftTrainingProduct, listUser1, listReviews));
    		//write top 10 recommended products to file
	        utility.writeRecommendedProductToFile("Top10RecommendedProductsAtNode" + nodeNumber, leftNode.getListProducts());

    		leftNode.setSelectedAttribute(-1);

       		//find attribute which can separate set of Users
    		int selectedAttribute = this.utility.maxPayOff(tmpList, listUser1);
    		leftNode.setSelectedAttribute(selectedAttribute);

    		//find U(choiceAttribute) and U(!choiceAttribute)
    		List<User> listLeftUser1 = new ArrayList<User>(); //U(selectedAttribute)
    		List<User> listLeftUser2 = new ArrayList<User>(); //U(!selectedAttribute)
    		for (int i = 0; i <= listUser1.size()-1; i++) {
    			User user = listUser1.get(i);
    			if (user.getAttributes().get(selectedAttribute) == 1) {
    				listLeftUser1.add(user);
    			} else {
    				listLeftUser2.add(user);
    			}
    		}
    		//remove choiceAttribute from setOfAttributes
    		List<Integer> tmpLeftList = new ArrayList<Integer>();
    		for (int i= 0; i<= tmpList.size() -1; i++) {
    			if (tmpList.get(i) != selectedAttribute) {
    				tmpLeftList.add(tmpList.get(i));
    			}
    		}

    		//Checking Continuing process or pruning tree here
    		if (leftNode.getHeight() <= pruningTreeThresold) {
    			//recursive insert node
    			insert(leftNode, tmpLeftList, listLeftUser1, listLeftUser2);
    		}

    	} else {
    		//Left node
    		Node leftNode = new Node();
    		leftNode.setParentNode(rootNode);
    		rootNode.setLeftNode(leftNode);

    		//Set top K list products at left node
    		leftNode.setListProducts(rootNode.getListProducts());
    		//write top 10 recommended products to file
	        utility.writeRecommendedProductToFile("Top10RecommendedProductsAtNode" + nodeNumber, leftNode.getListProducts());

    		leftNode.setSelectedAttribute(-1);
    	}

    	//Add right node to rootNode
    	if (listUser2.size() >= 20) { //each node contains at least 20 users for recommendation
    		System.out.println("right node '" + listUser2.size()  + "'");
    		//right node
    		Node rightNode = new Node();
    		rightNode.setParentNode(rootNode);
    		rightNode.setHeight(rootNode.getHeight() + 1);
    		rootNode.setRightNode(rightNode);

			//Calculate product's rank at node root
			nodeNumber = nodeNumber + 1;
			String fileName = "TrainningNode" + nodeNumber + ".txt";
			String modelName = "ModelNode" + nodeNumber;

			List<Integer> listeUserId = new ArrayList<Integer>();
			for (int i = 0; i< listUser2.size(); i++) {
				listeUserId.add(listUser2.get(i).getUserId());
			}

			//get training product for left node
			List<Product> listRightTrainingProduct = utility.getListTrainingProduct(listProducts, listReviews, listeUserId) ;
			utility.generateTrainingData(fileName, listRightTrainingProduct, listReviews, listeUserId);

			//calculate product's rank weight at node root
			Rankweight rankWeight = new Rankweight(fileName, modelName, rankType);
			//start thread calculate rank weight
			rankWeight.run();

			//Get result from process
			File f = new File("C:\\workspace\\RSWEB\\TrainningData\\" + modelName);
			while (!f.exists()) {
				try {
					sleep(100);
					//System.out.println("File " + modelName +" not found!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//read weight from modelName
			List<Double> weightVector = new ArrayList<Double>();
			String[] lineData ;
			String weight = null;
	        try {
	            // FileReader reads text files in the default encoding.
	            FileReader fileReader =
	                new FileReader("C:\\workspace\\RSWEB\\TrainningData\\" + modelName);

	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader =
	                new BufferedReader(fileReader);

	            while((weight = bufferedReader.readLine()) != null) {
	            	if (weight.indexOf(":") >= 0) break;
	            }

	            // Always close files.
	            bufferedReader.close();

	            lineData = weight.split(" ");
	            if (lineData.length >= 14) {
	            	for (int m = 1; m < lineData.length -1; m++) {
	            		String element = lineData[m];
	            		weightVector.add(Double.parseDouble(element.substring(element.indexOf(":") +1)));
	            	}
	            }
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println("Unable to open file '" + modelName + "'");
	        }
	        catch(IOException ex) {
	            System.out.println("Error reading file '" + modelName + "'");
	        }

	        //Calculate rank products
	        if (weightVector.size() > 0) {
	        	if (listRightTrainingProduct != null) {
	        		listRightTrainingProduct = utility.CalRank(listRightTrainingProduct, weightVector);
	        	}
	        }

	        //Calculate payoff for each users
	        if (listUser2 != null) {
	        	for (int i = 0; i < listUser2.size(); i++) {
	        		Double payoff = utility.CalPayoff(listUser2.get(i), listRightTrainingProduct);
	        		listUser2.get(i).setPayoff(payoff);
	        	}
	        }

	        //********************* Process building decision tree********************//
    		//Set top K list products at left node
	        rightNode.setListProducts(utility.getTopKlist(listRightTrainingProduct, listUser2, listReviews));
    		//write top 10 recommended products to file
	        utility.writeRecommendedProductToFile("Top10RecommendedProductsAtNode" + nodeNumber, rightNode.getListProducts());

	        rightNode.setSelectedAttribute(-1);

       		//find attribute which can separate set of Users
    		int selectedAttribute = this.utility.maxPayOff(tmpList, listUser2);
    		rightNode.setSelectedAttribute(selectedAttribute);

    		//find U(choiceAttribute) and U(!choiceAttribute)
    		List<User> listRightUser3 = new ArrayList<User>(); //U(selectedAttribute)
    		List<User> listRightUser4 = new ArrayList<User>(); //U(!selectedAttribute)
    		for (int i = 0; i <= listUser2.size()-1; i++) {
    			User user = listUser2.get(i);
    			if (user.getAttributes().get(selectedAttribute) == 1) {
    				listRightUser3.add(user);
    			} else {
    				listRightUser4.add(user);
    			}
    		}
    		//remove choiceAttribute from setOfAttributes
    		List<Integer> tmpRightList = new ArrayList<Integer>();
    		for (int i= 0; i<= tmpList.size() -1; i++) {
    			if (tmpList.get(i) != selectedAttribute) {
    				tmpRightList.add(tmpList.get(i));
    			}
    		}

    		//Checking Continuing process or pruning tree here
    		if (rightNode.getHeight() <= pruningTreeThresold) {
    			//recursive insert node
    			insert(rightNode, tmpRightList, listRightUser3, listRightUser4);
    		}

    	} else {
    		//Right node
    		Node rightNode = new Node();
    		rightNode.setParentNode(rootNode);
    		rootNode.setRightNode(rightNode);
    		//Set top K list products at left node
    		rightNode.setListProducts(rootNode.getListProducts());
    		//write top 10 recommended products to file
	        utility.writeRecommendedProductToFile("Top10RecommendedProductsAtNode" + nodeNumber, rightNode.getListProducts());

    		rightNode.setSelectedAttribute(-1);
    	}
    }

    /**
     * Print Decision tree
     * @param nodeRoot
     */
	private void PrintDecisionTree (Node nodeRoot) {
    	if (nodeRoot != null) {
        	StringBuilder strBuilder = new StringBuilder();
        	strBuilder.append("Node: [" + nodeRoot.getSelectedAttribute());

        	if (nodeRoot.getParentNode()!= null) {
        		strBuilder.append(" Of parent " + nodeRoot.getParentNode().getSelectedAttribute() + "," );
        	} else {
        		strBuilder.append(",");
        	}

        	strBuilder.append(" Right node: " );
        	if (nodeRoot.getRightNode() != null && nodeRoot.getRightNode().getSelectedAttribute() != -1) {
        		strBuilder.append(nodeRoot.getRightNode().getSelectedAttribute() + "," );
        	} else {
        		strBuilder.append(" x " );
        	}

        	strBuilder.append(" Left node: " );
        	if (nodeRoot.getLeftNode() != null && nodeRoot.getLeftNode().getSelectedAttribute() != -1) {
        		strBuilder.append(nodeRoot.getLeftNode().getSelectedAttribute() + " ");
        	} else {
        		strBuilder.append(" x " );
        	}

        	strBuilder.append(", TopKList(");
        	List<Product> listProducts = nodeRoot.getListProducts();
        	if (listProducts.size() > 0) {
        		for (int i= 0; i<= listProducts.size()-1; i++) {
        			strBuilder.append(listProducts.get(i).getProductId() + ",");
        		}
        	}
        	strBuilder.append(") ");

        	strBuilder.append("] ");

        	System.out.println(strBuilder);

        	if (nodeRoot.getLeftNode() !=null && nodeRoot.getLeftNode().getSelectedAttribute() != -1) {
        		PrintDecisionTree (nodeRoot.getLeftNode());
        	} else {
        		if (nodeRoot.getLeftNode() !=null) PrintLeafNode(nodeRoot.getLeftNode());
        	}
        	if (nodeRoot.getRightNode() !=null && nodeRoot.getRightNode().getSelectedAttribute() != -1) {
        		PrintDecisionTree (nodeRoot.getRightNode());
        	} else {
        		if (nodeRoot.getRightNode() !=null) PrintLeafNode(nodeRoot.getRightNode());
        	}

    	}
    }

    /**
     * Print leaf node
     * @param leafNode
     */
	private void PrintLeafNode (Node leafNode) {
    	StringBuilder strBuilder = new StringBuilder();
    	strBuilder.append("Node: X of parent " + leafNode.getParentNode().getSelectedAttribute());
    	strBuilder.append(", TopKList(");

    	List<Product> listProducts = leafNode.getListProducts();
    	if (listProducts.size() > 0) {
    		for (int i= 0; i<= listProducts.size()-1; i++) {
    			strBuilder.append(listProducts.get(i).getProductId() + ",");
    		}
    	}
    	strBuilder.append(") ");

    	System.out.println(strBuilder);
    }


    /**
     * Learning to question
     * @param nodeRoot
     */
    private void LearningToquestion (Node nodeRoot) {
    	if (nodeRoot != null) {
    		String answer = "";

        	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("For " + prop.getProperty(Integer.toString(nodeRoot.getSelectedAttribute())) + " purpose? (Y/N): ");
            try {
            	answer = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //if user answers yes question, we go to left node
            if (answer.equals("y") || answer.equals("Y")) {
            	Node leftNode = nodeRoot.getLeftNode();
            	if (leftNode != null && leftNode.getSelectedAttribute() !=-1) {
            		LearningToquestion (leftNode);
            	} else {

            		if (leftNode !=null) {
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = leftNode.getListProducts();
                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= TopListProducts.size()-1; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");
                    		}
                    	}
                		System.out.println(strBuilder);
            		} else {
            			//print current node
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = nodeRoot.getListProducts();
                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= TopListProducts.size()-1; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");
                    		}
                    	}
                    	System.out.println(strBuilder);
            		}

            	}
            }

            //if user answers no question, we go to right node
            if (answer.equals("n") || answer.equals("N")) {
            	Node rightNode = nodeRoot.getRightNode();
            	if (rightNode != null && rightNode.getSelectedAttribute() !=-1) {
            		LearningToquestion (rightNode);
            	} else {
            		if (rightNode != null) {
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = rightNode.getListProducts();
                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= TopListProducts.size()-1; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");
                    		}
                    	}
                		System.out.println(strBuilder);
            		} else {
            			//print current node
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = nodeRoot.getListProducts();
                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= TopListProducts.size()-1; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");
                    		}
                    	}
                    	System.out.println(strBuilder);
            		}

            	}
            }

            //if user wants to stop answer, we recommend current node to user
            if (!answer.equals("n") && !answer.equals("N") && !answer.equals("y") &&  !answer.equals("Y")) {
            	if (nodeRoot != null) {
            		StringBuilder strBuilder = new StringBuilder();
            		strBuilder.append("We recommend you: " );
                	List<Product> TopListProducts = nodeRoot.getListProducts();
                	if (TopListProducts.size() > 0) {
                		for (int i= 0; i<= TopListProducts.size()-1; i++) {
                			strBuilder.append(TopListProducts.get(i).getProductId() + ",");
                		}
                	}
                	System.out.println(strBuilder);
            	}
            }

    	}
    }

}
