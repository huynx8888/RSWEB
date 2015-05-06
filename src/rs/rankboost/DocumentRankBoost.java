package rs.rankboost;

public class DocumentRankBoost implements Comparable {
	private int ProductId;

	private double rankBoost;

	public int getProductId() {
		return ProductId;
	}

	public void setProductId(int productId) {
		ProductId = productId;
	}

	public double getRankBoost() {
		return rankBoost;
	}

	public void setRankBoost(double rankBoost) {
		this.rankBoost = rankBoost;
	}

	//implement compare method
	public int compareTo (Object o) {
		if (ProductId == ((DocumentRankBoost)o).ProductId) {
			return 0;
		} else if (ProductId < ((DocumentRankBoost)o).ProductId) {
			return 1;
		} else {
			return -1;
		}
	}


}
