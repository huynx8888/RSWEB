package rs.process.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rs.dataStructure.Product;
import rs.treeStructure.DecisionTree;
import rs.treeStructure.DecisionTreeRankBoost;
import rs.treeStructure.Node;

import rs.utility.Common;

public class SearchTreeAction extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	Node rootx = null;
	Node root = null;
	Properties prop;
	//表示用データ取得
	List<String> arrList = null;
	String check = "";
	String strKeepCurrentMethod = "";

	/**
	 *	doGet
	 *
	 *	公社HP更新状況把握マップの更新オブジェクトを生成して実行する。
	 *	実行すべきかを経過時間で判定する。
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestDispatcher dispatcher = request.getRequestDispatcher( "/home/index.jsp" );
		dispatcher.forward( request, response );

	}


	/**
	 *
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		 //String workingDir = System.getProperty("user.dir");
		 //System.out.println("Current working directory : " + workingDir);

		//Keep parameters
		HashMap<String,String> formDataMap = new HashMap<String,String>();
		List<String> formImage = new ArrayList<String>();

		String methodType = request.getParameter("methodType");
		if (methodType == null || "".equals(methodType)) {
			formDataMap = new HashMap<String,String>();
			formDataMap.put("titleName", "Please Check the learning method!");

			arrList = null;
			formImage = new ArrayList<String>();

			request.setAttribute("arrList", arrList);
			request.setAttribute("formDataMap", formDataMap);
			request.setAttribute("formImage", formImage);

			RequestDispatcher dispatcher = request.getRequestDispatcher( "/home/index.jsp" );
			dispatcher.forward( request, response );
			return;
		}

		//when change check between methods
		if (!strKeepCurrentMethod.equals(methodType)) {
			rootx = null;
			root = null;
			check = "";
		}

		//init properties file
		prop = new Properties();

		InputStream input = null;
		try {
			input = new FileInputStream("C:\\workspace\\RSWEB\\resources\\resources.properties");
			prop.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ("".equals(check)) {
			arrList = new ArrayList<String>();
			if (rootx == null) {
				//Delete all temporary files in directory
				Common common = new Common("C:\\workspace\\RSWEB\\TrainningData");
				common.deleteAllTemporaryFiles();

				//keep current method
				strKeepCurrentMethod = methodType;

				//Start execute decision tree for recommendation
				if ("RankSVM".equals(methodType)) {
					//rank svm
					DecisionTree dt = new DecisionTree(1);//1:Ransvm
					root = dt.Run();
					formDataMap.put("radio1", "checked");
					formDataMap.put("radio2", "");
				} else {
					//rank boost
					DecisionTreeRankBoost dt = new DecisionTreeRankBoost(2);//2:rankboost
					root = dt.Run();
					formDataMap.put("radio1", "");
					formDataMap.put("radio2", "checked");
				}

				rootx = root;

			} else {
				root = rootx;

				//Keep value for radio button
				if ("RankSVM".equals(methodType)) {
					formDataMap.put("radio1", "checked");
					formDataMap.put("radio2", "");
				} else {
					formDataMap.put("radio1", "");
					formDataMap.put("radio2", "checked");
				}

			}

			formDataMap.put("titleName", "Please answer the questions below!!");

			check = "1";

			//giving answer the question
			if (root != null) {
				 arrList.add("For " + prop.getProperty(Integer.toString(root.getSelectedAttribute())) + " purpose? (Y/N): ");
			}

		} else {
			formDataMap.put("titleName", "Please answer the questions below!!");

			if ("RankSVM".equals(methodType)) {
				formDataMap.put("radio1", "checked");
				formDataMap.put("radio2", "");
			} else {
				formDataMap.put("radio1", "");
				formDataMap.put("radio2", "checked");
			}

			check = "1";

//			Map formData = request.getParameterMap();
//			Set<String> ite = (Set<String>)formData.keySet( );
//			Iterator<String> ic = ite.iterator( );
//			Escape ecp = new Escape();
//			while (ic.hasNext()) {
//				String key = (String)ic.next();
//				String par[]=(String[])formData.get(key);
//				formDataMap.put(key,ecp.htmlEscape(par[0]));
//			}


			String answer = request.getParameter("answer");

			List<String> tmpArrList = new ArrayList<String>();
			for (int a =0; a < arrList.size(); a++) {
				if (a == arrList.size() -1) {
					tmpArrList.add(arrList.get(a) + answer);
				} else {
					tmpArrList.add(arrList.get(a));
				}
			}

			arrList = new ArrayList<String>();
			arrList = tmpArrList;

			//if user answers yes question, we go to left node
            if (answer.equals("y") || answer.equals("Y")) {
            	Node leftNode = root.getLeftNode();
            	if (leftNode != null && leftNode.getSelectedAttribute() !=-1) {
            		arrList.add("For " + prop.getProperty(Integer.toString(leftNode.getSelectedAttribute())) + " purpose? (Y/N): ");
            		root = leftNode;
            	} else {

            		if (leftNode !=null) {
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = leftNode.getListProducts();

                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= 9; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");

                    			String top10Product = "";
                    			top10Product = TopListProducts.get(i).getProductId() + "";
                    			List<Integer> attributes = new ArrayList<Integer>();
                    			attributes = TopListProducts.get(i).getAttributes();
                    			for (int k = 0; k< attributes.size(); k++) {
                    				if (attributes.get(k) == 1) {
                    					top10Product = top10Product + "," + prop.getProperty(Integer.toString(28+k));
                    				}
                    			}
                    			formImage.add(top10Product);

                    		}
                    	}

                    	arrList.add(strBuilder.toString());

                		System.out.println(strBuilder);
            		} else {
            			//print current node
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = root.getListProducts();
                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= 9; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");

                    			String top10Product = "";
                    			top10Product = TopListProducts.get(i).getProductId() + "";
                    			List<Integer> attributes = new ArrayList<Integer>();
                    			attributes = TopListProducts.get(i).getAttributes();
                    			for (int k = 0; k< attributes.size(); k++) {
                    				if (attributes.get(k) == 1) {
                    					top10Product = top10Product + "," + prop.getProperty(Integer.toString(28+k));
                    				}
                    			}
                    			formImage.add(top10Product);

                    		}
                    	}

                    	arrList.add(strBuilder.toString());

                    	System.out.println(strBuilder);

            		}
            		check = "";

            	}
            }

            //if user answers no question, we go to right node
            if (answer.equals("n") || answer.equals("N")) {
            	Node rightNode = root.getRightNode();
            	if (rightNode != null && rightNode.getSelectedAttribute() !=-1) {
            		arrList.add("For " + prop.getProperty(Integer.toString(rightNode.getSelectedAttribute())) + " purpose? (Y/N): ");
            		root = rightNode;
            	} else {
            		if (rightNode != null) {
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = rightNode.getListProducts();
                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= 9; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");

                    			String top10Product = "";
                    			top10Product = TopListProducts.get(i).getProductId() + "";
                    			List<Integer> attributes = new ArrayList<Integer>();
                    			attributes = TopListProducts.get(i).getAttributes();
                    			for (int k = 0; k< attributes.size(); k++) {
                    				if (attributes.get(k) == 1) {
                    					top10Product = top10Product + "," + prop.getProperty(Integer.toString(28+k));
                    				}
                    			}
                    			formImage.add(top10Product);

                    		}
                    	}

                    	arrList.add(strBuilder.toString());

                		System.out.println(strBuilder);
            		} else {
            			//print current node
                		StringBuilder strBuilder = new StringBuilder();
                		strBuilder.append("We recommend you: " );
                    	List<Product> TopListProducts = root.getListProducts();
                    	if (TopListProducts.size() > 0) {
                    		for (int i= 0; i<= 9; i++) {
                    			strBuilder.append(TopListProducts.get(i).getProductId() + ",");

                    			String top10Product = "";
                    			top10Product = TopListProducts.get(i).getProductId() + "";
                    			List<Integer> attributes = new ArrayList<Integer>();
                    			attributes = TopListProducts.get(i).getAttributes();
                    			for (int k = 0; k< attributes.size(); k++) {
                    				if (attributes.get(k) == 1) {
                    					top10Product = top10Product + "," + prop.getProperty(Integer.toString(28+k));
                    				}
                    			}
                    			formImage.add(top10Product);

                    		}
                    	}

                    	arrList.add(strBuilder.toString());

                    	System.out.println(strBuilder);
            		}
            		check = "";

            	}
            }

            //if user wants to stop answer, we recommend current node to user
            if (!answer.equals("n") && !answer.equals("N") && !answer.equals("y") &&  !answer.equals("Y")) {
            	if (root != null) {
            		StringBuilder strBuilder = new StringBuilder();
            		strBuilder.append("We recommend you: " );
                	List<Product> TopListProducts = root.getListProducts();
                	if (TopListProducts.size() > 0) {
                		for (int i= 0; i<= 9; i++) {
                			strBuilder.append(TopListProducts.get(i).getProductId() + ",");

                			String top10Product = "";
                			top10Product = TopListProducts.get(i).getProductId() + "";
                			List<Integer> attributes = new ArrayList<Integer>();
                			attributes = TopListProducts.get(i).getAttributes();
                			for (int k = 0; k< attributes.size(); k++) {
                				if (attributes.get(k) == 1) {
                					top10Product = top10Product + "," + prop.getProperty(Integer.toString(28+k));
                				}
                			}
                			formImage.add(top10Product);

                		}
                	}
                	arrList.add(strBuilder.toString());

                	System.out.println(strBuilder);
            	}
            	check = "";
            }

		}

//		String para1 = request.getParameter("para1");
//		String para2 = request.getParameter("para2");
//		String no = request.getParameter("no");

//		System.out.println("para1: " + para1);
//		System.out.println("para2: " + para2);
//		System.out.println("no: " + no);


//		String element = "";
//		for (int i= 0; i< 10; i++) {
//			element = "aaa" + i + ",name" + i + ",job" + i;
//			arrList.add(element);
//
//
//		}

		request.setAttribute("arrList", arrList);
		request.setAttribute("formDataMap", formDataMap);
		request.setAttribute("formImage", formImage);

		RequestDispatcher dispatcher = request.getRequestDispatcher( "/home/index.jsp" );
		dispatcher.forward( request, response );

	}

}


