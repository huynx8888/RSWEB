package rs.dataStructure;

import java.util.List;

/**
 * 
 * @author huynx
 * @date 2014-12-06
 */

public class User {
	//user's id
	private int userId;
	//user's payoff
	private double payoff;
	//user's attributes
	private List<Integer> attributes;
	//user's reviewed products
	private List<Review> reviews;
	
	//helpful (confident)
	private double helpful;
	
	//Get and Set methods
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public double getPayoff() {
		return payoff;
	}
	public void setPayoff(double payoff) {
		this.payoff = payoff;
	}
	public List<Integer> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Integer> attributes) {
		this.attributes = attributes;
	}
	public List<Review> getReviews() {
		return reviews;
	}
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	public double getHelpful() {
		return helpful;
	}
	public void setHelpful(double helpful) {
		this.helpful = helpful;
	}
	
	
}
