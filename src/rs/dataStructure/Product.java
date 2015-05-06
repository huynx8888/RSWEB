package rs.dataStructure;

import java.util.List;

/**
 *
 * @author huynx
 * @date 2014-12-06
 *
 */

public class Product implements Comparable {
	//product's id
	private int productId;
	// product's rank
	private double rank;
	//product's attributes
	private List<Integer> attributes;

//	//implement compare method
//	public int compareTo (Object o) {
//		if (rank == ((Product)o).rank) {
//			return 0;
//		} else if (rank < ((Product)o).rank) {
//			return 1;
//		} else {
//			return -1;
//		}
//	}

	//implement compare method
	public int compareTo (Object o) {
		if (String.valueOf(rank).equals(String.valueOf( ((Product)o).rank ))) {
			return 0;
		} else if (String.valueOf(rank).compareTo(String.valueOf( ((Product)o).rank )) < 0) {
			return 1;
		} else {
			return -1;
		}
	}

	//Get and Set methods
	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public double getRank() {
		return rank;
	}

	public void setRank(double rank) {
		this.rank = rank;
	}

	public List<Integer> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Integer> attributes) {
		this.attributes = attributes;
	}

}
