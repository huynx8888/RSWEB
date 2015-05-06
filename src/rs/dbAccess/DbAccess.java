package rs.dbAccess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rs.dataStructure.*;


/**
 *
 * @author huynx
 * @date 2014-12-06
 *
 */

public class DbAccess {
	//List of product's features
	private List<String> listOfProductFeatures;
	//List of user's features
	private List<String> ListOfUserFeatures;

	public DbAccess () {
		//init features
		listOfProductFeatures = new ArrayList<String>();
		ListOfUserFeatures = new ArrayList<String>();

		//Product' features
		listOfProductFeatures.add("DiscreteGPU");
		listOfProductFeatures.add("LargeHDD");
		listOfProductFeatures.add("IntegratedWebCam");
		listOfProductFeatures.add("IntegratedMic");
		listOfProductFeatures.add("HighDPI");
		listOfProductFeatures.add("HighBattery");
		listOfProductFeatures.add("Rugged");
		listOfProductFeatures.add("BackLitKeyboard");
		listOfProductFeatures.add("LightWeight");
		listOfProductFeatures.add("HighRAM");
		listOfProductFeatures.add("SDCard");
		listOfProductFeatures.add("OpticalDrive");

		//User's feature
		ListOfUserFeatures.add("graphic_design");
		ListOfUserFeatures.add("game_engine");
		ListOfUserFeatures.add("vector");
		ListOfUserFeatures.add("animator");
		ListOfUserFeatures.add("research");
		ListOfUserFeatures.add("industry1");
		ListOfUserFeatures.add("school");
		ListOfUserFeatures.add("industry2");
		ListOfUserFeatures.add("high_end_user");
		ListOfUserFeatures.add("low_end_user");
		ListOfUserFeatures.add("operator");
		ListOfUserFeatures.add("system_admin");
		ListOfUserFeatures.add("student");
		ListOfUserFeatures.add("industry3");
		ListOfUserFeatures.add("small_business");
		ListOfUserFeatures.add("large_business");
		ListOfUserFeatures.add("repetitive");
		ListOfUserFeatures.add("strategic");
		ListOfUserFeatures.add("real_time");
		ListOfUserFeatures.add("casino_games");
		ListOfUserFeatures.add("pictures");
		ListOfUserFeatures.add("video");
		ListOfUserFeatures.add("online_gaming");
		ListOfUserFeatures.add("social");
		ListOfUserFeatures.add("long_form");
		ListOfUserFeatures.add("short_form");
		ListOfUserFeatures.add("code2");
		ListOfUserFeatures.add("artist");

	}

	/**
	 * Get all products from database
	 * @return list products
	 */
	public List<Product> getProducts () {
		List<Product> arrayProducts = new ArrayList<Product>();

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:C:\\workspace\\RSWEB\\mydb.db");
			conn.setAutoCommit(false);
		    System.out.println("Opened database successfully");

		    stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery( "SELECT * FROM PRODUCTS;" );
		    while ( rs.next() ) {
		    	Product product = new Product();
		    	List<Integer> attributes = new ArrayList<Integer>();

		    	//set product'id
		    	product.setProductId(rs.getInt("productId"));
		    	//set default product's rank
		    	product.setRank(0);

		    	String inLine = rs.getString("productAttributes");

				//Encode Feature
				for (int i = 0; i < listOfProductFeatures.size(); i++) {
					if (inLine.indexOf(listOfProductFeatures.get(i)) >= 0) {
						attributes.add(1);
					} else {
						attributes.add(0);
					}
				}

				//set product's attributes
				product.setAttributes(attributes);

				arrayProducts.add(product);
		    }

		    rs.close();
		    stmt.close();
		    conn.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
		System.out.println("getProducts is done successfully!");

		return arrayProducts;
	}

	/**
	 * Get all users from database
	 * @return list users
	 */
	public List<User> getUsers () {
		List<User> arrayUsers = new ArrayList<User>();

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:C:\\workspace\\RSWEB\\mydb.db");
			conn.setAutoCommit(false);
		    System.out.println("Opened database successfully");

		    stmt = conn.createStatement();

		    //FOR TEST ten-folds cross validation
		    ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS; " );

		    while ( rs.next() ) {
		    	User user = new User();
		    	List<Integer> attributes = new ArrayList<Integer>();
		    	List<Review> reviews = new ArrayList<Review>();

		    	//set user'id
		    	user.setUserId(rs.getInt("userId"));
		    	//set default user's payoff
		    	user.setPayoff(0);

		    	String inLine = rs.getString("userAttributes");

				//Encode Feature
				for (int i = 0; i < ListOfUserFeatures.size(); i++) {
					if (inLine.indexOf(ListOfUserFeatures.get(i)) >= 0) {
						attributes.add(1);
					} else {
						attributes.add(0);
					}
				}

				//set user's attributes
				user.setAttributes(attributes);

				//find list reviews for user
				Statement stmt1 = null;
				stmt1 = conn.createStatement();
				ResultSet rsreview = stmt1.executeQuery( "SELECT * FROM REVIEWS Where userId = " + rs.getInt("userId") + ";" );
				while ( rsreview.next() ) {
					Review review = new Review();
					//set user'id
					review.setUserId(rs.getInt("userId"));
					//set product'id
					review.setProductId(rsreview.getInt("productId"));
					//set user's rating
					review.setRate(rsreview.getDouble("rating"));
					reviews.add(review);
				}

				//set user's reviews list
				Collections.sort(reviews);

				//START: This step is used to evaluate system.
				if (reviews.size() > 0) {
					try {
						File file = new File("C:\\workspace\\RSWEB\\TrainningData\\" + reviews.get(0).getUserId() + "_reivews.txt");
						if (!file.exists()) {
							file.createNewFile();
						}
						FileWriter fw = new FileWriter(file.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);

						for (int i= 0; i<reviews.size(); i++ ) {
							bw.write(reviews.get(i).getProductId() + "," + reviews.get(i).getRate());
							if (i != reviews.size() -1) {
								bw.write("\n");
							}
						}
						bw.close();

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//END///////////////////////////////////////

				user.setReviews(reviews);

				arrayUsers.add(user);
		    }

		    rs.close();
		    stmt.close();
		    conn.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
		System.out.println("getUsers is done successfully!");

		return arrayUsers;
	}

	/**
	 * get all reviews from database
	 * @return list reviews
	 */
	public List<Review> getReviews () {
		List<Review> arrayReviews = new ArrayList<Review>();

		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:C:\\workspace\\RSWEB\\mydb.db");
			conn.setAutoCommit(false);
		    System.out.println("Opened database successfully");

		    stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery( "SELECT * FROM REVIEWS  ;" );// WHERE ID < 6001 AND ID >= 5001
		    while ( rs.next() ) {
		    	Review review = new Review();

		    	//set product'id
		    	review.setProductId(rs.getInt("productId"));
		    	//set user'id
		    	review.setUserId(rs.getInt("userId"));
		    	//set user's rating
		    	review.setRate(rs.getDouble("rating"));

		    	arrayReviews.add(review);
		    }

		    rs.close();
		    stmt.close();
		    conn.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
		System.out.println("getReviews is done successfully!");

		return arrayReviews;
	}

	/**
	 *
	 * @return list index of attributes
	 * ex: [work, graphic_intensive, game_dev, graphic_design, student, system_admin] correspond to [0,1,2,3,4,5]
	 */
	public List<Integer> getIncreaseIndexOfAttributes () {
		List<Integer> listIndexOfAttributes = new ArrayList<Integer>();
		for (int i= 0; i < ListOfUserFeatures.size(); i++) {
			listIndexOfAttributes.add(i);
		}

		return listIndexOfAttributes;
	}

}
