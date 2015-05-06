package rs.dataStructure;

/**
 * 
 * @author huynx
 * @date 2014-12-06
 *
 */

public class Review implements Comparable {
	//user's id
	private int userId;
	//product's id
	private int productId;
	//user's rated products
	private double rate;
	
	//sentiment classification
	private double scoreOfSentiment;

	//Get and Set methods
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	
	public double getScoreOfSentiment() {
		return scoreOfSentiment;
	}
	public void setScoreOfSentiment(double scoreOfSentiment) {
		this.scoreOfSentiment = scoreOfSentiment;
	}
	//implement compare method
	public int compareTo(Object o) {
		if (rate == ((Review)o).rate) {
			return 0;
		} else if (rate < ((Review)o).rate) {
			return 1;
		} else {
			return -1;
		}
	} 
	
}
