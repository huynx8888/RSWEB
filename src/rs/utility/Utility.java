package rs.utility;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rs.dataStructure.*;
import rs.rankboost.DocumentRankBoost;


/**
 *
 * @author huynx
 * @date 2014-12-06
 *
 */

public class Utility {

	/**
	 * Calculate Product Ranking
	 * *****: rank(p) = W^T * p
	 */
	public List<Product> CalRank(List<Product> listProducts, List<Double> weightVector) {
		double rankp = 0;
		for (int i=0; i < listProducts.size(); i++) {
			rankp = 0;
			List<Integer> attributes = listProducts.get(i).getAttributes();
			for (int j=0; j<weightVector.size(); j++) {
				rankp = rankp + attributes.get(j) * weightVector.get(j);
			}

			listProducts.get(i).setRank(rankp);
		}

		//Sort by Rank desc
		Collections.sort(listProducts);
		return listProducts;
	}

	/**
	 * Calculate Product Ranking by rankboost
	 * *****:
	 */
	public List<Product> CalRankBoost(List<Product> listProducts, List<DocumentRankBoost> listRankBoost) {
		double rankp = 0;
		for (int i=0; i < listProducts.size(); i++) {
			rankp = 0;

			int count = 0;
			for (int j = 0; j < listRankBoost.size(); j++) {
				if (listRankBoost.get(j).getProductId() == listProducts.get(i).getProductId()) {
					count = count + 1;
					rankp = rankp + listRankBoost.get(j).getRankBoost();
				}
			}

			listProducts.get(i).setRank(rankp/count);
		}

		//Sort by Rank desc
		Collections.sort(listProducts);
		return listProducts;
	}

	/**
	 * Calculate PayOff of userId
	 * PayOff(Ui) = eval(Rank(Ui))
	 * eval(rank) = 2|{(pi,pj) belong P| rank(pi) > rank(pj)}|/(n*(n-1))
	 * @param user
	 * @param listProducts
	 * @return
	 */
	public double CalPayoff (User user, List<Product> listProducts) {
		double eval = 0;
		List<Review> reviews = user.getReviews();
		int pairs = 0;
		for (int i= 0; i < reviews.size() -1; i++) {
			Review review1 = reviews.get(i);
			Review review2 = reviews.get(i+1);
			if (findProductRank(review1.getProductId(), listProducts) >= findProductRank(review2.getProductId(), listProducts)) {
				pairs = pairs + 1;
			}
		}

		//Calculate eval
		int n = listProducts.size();
		eval = 1000000*2*pairs/(n*(n-1));
		return eval;
	}

	/**
	 * Find rank for product with productId
	 * @param productId
	 * @param listProducts
	 * @return
	 */
	private double findProductRank (int productId, List<Product> listProducts) {
		double rank = 0;
		for (int i= 0; i<= listProducts.size()-1; i++) {
			Product p = listProducts.get(i);
			if (p.getProductId() == productId) {
				rank = p.getRank();
				break;
			}
		}
		return rank;
	}

	/**
	 * This method is used to find the maxPayOff
	 * @param attributes
	 * @param listUsers
	 * @return
	 */
	public int maxPayOff(List<Integer> attributes, List<User> listUsers) {
		int selectedAttribute = 0;
		double maxPayOff = -99999;
		for (int i = 0; i <= attributes.size()-1; i++) {
			int attribute_i = attributes.get(i);
			double payOff = 0;

			for (int j = 0; j <= listUsers.size()-1; j++) {
				User user = listUsers.get(j);
				if (user.getAttributes().get(attribute_i) == 1) {
					payOff = payOff + user.getPayoff();
				}
			}

			if (payOff > maxPayOff) {
				maxPayOff = payOff;
				selectedAttribute = attribute_i;
			}

		}
		return selectedAttribute;
	}

	/**
	 * Get Top k list product at node q
	 * @param listProducts
	 * @param listUser
	 * @param listReviews
	 * @return
	 */
	public List<Product> getTopKlist (List<Product> listProducts, List<User> listUser, List<Review> listReviews) {
		List<Product> returnListProducts = new ArrayList<Product>();
		List<Integer> listint = new ArrayList<Integer>();
		for (int i= 0; i<= listUser.size()-1; i++) {
			for (int j = 0; j<= listReviews.size()-1; j++) {
				if (listUser.get(i).getUserId() == listReviews.get(j).getUserId()) {
					if (!listint.contains(listReviews.get(j).getProductId())) {
						listint.add(listReviews.get(j).getProductId());
					}
				}
			}
		}

		for (int k = 0; k <= listint.size()-1; k++) {
			for (int l = 0; l <= listProducts.size()-1; l++) {
				if (listint.get(k) == listProducts.get(l).getProductId()) {
					returnListProducts.add(listProducts.get(l));
					break;
				}
			}
		}

		//sort by product's rank desc
		Collections.sort(returnListProducts);
		return returnListProducts;
	}

	/**
	 * Generate TrainingData for each node of the tree
	 */
	public void generateTrainingData (String fileName, List<Product> listProducts, List<Review> listReviews, List<Integer> listeUserId) {
		try {
			File file = new File("C:\\workspace\\RSWEB\\TrainningData\\" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String content;
			int count = -1;
			int tmpUserId = -1;

			for (int i=0; i < listReviews.size(); i++) {
				if (!listeUserId.contains(listReviews.get(i).getUserId())) {
					continue;
				}

				if (tmpUserId != listReviews.get(i).getUserId()) {
					tmpUserId = listReviews.get(i).getUserId();
					count = count + 1;
				}
				content = "";
				content = content + listReviews.get(i).getRate() + " ";
				content = content + "qid:" + count  + " ";


				int productId = listReviews.get(i).getProductId();
				List<Integer> attributes = new ArrayList<Integer>();
				//Get Product
				for (int j = 0; j < listProducts.size(); j++) {
					if (listProducts.get(j).getProductId() == productId) {
						attributes = listProducts.get(j).getAttributes();
						break;
					}
				}

				//Encode Feature
				for (int k = 0; k < attributes.size(); k++) {
					int index = k + 1;
					content = content + index + ":" + attributes.get(k) + " ";
				}

				bw.write(content);
				bw.newLine();
			}

			bw.close();
			System.out.println("Done generating file: " + fileName);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

		}

	}

	/**
	 * Get list training product at each node
	 * @param listProducts
	 * @param listReviews
	 * @param listeUserId
	 * @return
	 */
	public List<Product> getListTrainingProduct (List<Product> listProducts, List<Review> listReviews, List<Integer> listeUserId) {
		List<Product> listTrainProduct = new ArrayList<Product>();
		List<Integer> tempListProductId = new ArrayList<Integer>();

		for (int i=0; i<listeUserId.size(); i++) {
			for (int j=0; j<listReviews.size(); j++) {
				if (listReviews.get(j).getUserId() == listeUserId.get(i)) {
					if (!tempListProductId.contains(listReviews.get(j).getProductId())) {
						tempListProductId.add(listReviews.get(j).getProductId());
					}
				}
			}
		}

		//set list training product
		for (int i = 0; i < tempListProductId.size(); i++) {
			for (int j = 0; j < listProducts.size(); j++) {
				if (listProducts.get(j).getProductId() == tempListProductId.get(i)) {
					listTrainProduct.add(listProducts.get(j));
					break;
				}
			}
		}

		return listTrainProduct;
	}

	/**
	 * At each node we need to write recommended products to file
	 */
	public void writeRecommendedProductToFile (String fileName, List<Product> listProducts) {
		try {
			File file = new File("C:\\workspace\\RSWEB\\TrainningData\\" + fileName + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			//Write rank(p) for each products
			File file11 = new File("C:\\workspace\\RSWEB\\TrainningData\\" + "ListRanksAtNode" + fileName.substring(30) + ".txt");
			if (!file11.exists()) {
				file11.createNewFile();
			}
			FileWriter fw11 = new FileWriter(file11.getAbsoluteFile());
			BufferedWriter bw11 = new BufferedWriter(fw11);

			int productId;

			for (int i=0; i<listProducts.size(); i++) {
				productId = listProducts.get(i).getProductId();

				bw.write("" + productId);
				bw.newLine();
				if (i==9) break;
			}

			//Write rank(p) for each products
			for (int i=0; i<listProducts.size(); i++) {
				productId = listProducts.get(i).getProductId();

				bw11.write("Product:," + productId + ", Rank: " + listProducts.get(i).getRank());
				bw11.newLine();
			}

			bw.close();
			bw11.close();
			System.out.println("Done write list 10 recommended products to file: " + fileName);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

		}

	}


}
