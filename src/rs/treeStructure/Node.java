package rs.treeStructure;
import java.util.ArrayList;
import java.util.List;

import rs.dataStructure.*;


/**
 * 
 * @author huynx
 * @date 2014-12-06
 *
 */

public class Node {
	//attribute is selected at each node which can separate set of users to U(a) and U(!a)
	private int selectedAttribute;
	private Node parentNode;
	private Node leftNode;
	private Node rightNode;
	
	//heigt of the tree
	private int height;
	
	//Top of K list products which are recommended to user
	private List<Product> listProducts;

	//Get and Set methods
	public int getSelectedAttribute() {
		return selectedAttribute;
	}

	public void setSelectedAttribute(int selectedAttribute) {
		this.selectedAttribute = selectedAttribute;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public Node getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node leftNode) {
		this.leftNode = leftNode;
	}

	public Node getRightNode() {
		return rightNode;
	}

	public void setRightNode(Node rightNode) {
		this.rightNode = rightNode;
	}

	public List<Product> getListProducts() {
		return listProducts;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setListProducts(List<Product> plistProducts) {
		//set Top 10 products in the list
		if (plistProducts.size() >= 10) {
			List<Product> tmplistProducts = new ArrayList<Product>();
			for (int i= 0; i < plistProducts.size(); i++) {
				tmplistProducts.add(plistProducts.get(i));
			}
			this.listProducts = tmplistProducts;
		} else {
			this.listProducts = plistProducts;
		}

	}

}
