package rs.data.collections;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.FileWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.alchemyapi.features.Factor;
import com.alchemyapi.features.Feature;
import com.alchemyapi.features.FeatureExtraction;

public class exportJsonData {
	String inputPath;
	String outputPath;

	public exportJsonData(String inputPath, String outputPath) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
	}

	public static void main (String[] args) {
		try {


			File folder = new File("input/");
			File[] listOfFiles = folder.listFiles();



			//Products infor
			File fProducts = new File("output/products.json");
			if (!fProducts.exists()){
				fProducts.createNewFile();
			}

			//Reviews infor
			File fReviews = new File("output/reviews.json");
			if (!fReviews.exists()){
				fReviews.createNewFile();
			}

			//Users infor
			File fUsers = new File("output/users.json");
			if (!fUsers.exists()){
				fUsers.createNewFile();
			}

			//true = append file
			FileWriter fileWriterProducts = new FileWriter("output/" + fProducts.getName(), true);
			BufferedWriter bufferedWriterProducts = new BufferedWriter(fileWriterProducts);

			FileWriter fileWriterReviews = new FileWriter("output/" + fReviews.getName(), true);
			BufferedWriter bufferedWriterReviews = new BufferedWriter(fileWriterReviews);

			FileWriter fileWriterUsers = new FileWriter("output/" + fUsers.getName(), true);
			BufferedWriter bufferedWriterUsers = new BufferedWriter(fileWriterUsers);


			//Read all files in folder /input
			for (File file : listOfFiles) {
			    if (file.isFile()) {

			        //System.out.println(file.getName());

					JSONObject objProducts = new JSONObject();
					JSONObject objReviews = new JSONObject();
					JSONObject objUsers = new JSONObject();

					JSONParser parser = new JSONParser();
					Object obj = parser.parse(new FileReader("input/" + file.getName()));
					JSONObject jsonObject = (JSONObject) obj;

					//Write to Products/////////////////////
					JSONObject products = (JSONObject) jsonObject.get("ProductInfo");
					objProducts.put("ProductID", (String)products.get("ProductID"));
					objProducts.put("Name", (String)products.get("Name"));
					objProducts.put("Features", (String)products.get("Features"));
					objProducts.put("Price", (String)products.get("Price"));
					//objProducts.put("ImgURL", (String)products.get("ImgURL"));

					fileWriterProducts.write(objProducts.toJSONString());
					fileWriterProducts.write("\n");
					/////////////////////////////////////////

					//Write to Reviews and Users
					JSONArray reviews = (JSONArray) jsonObject.get("Reviews");
					Iterator<JSONObject> iterator = reviews.iterator();
					while (iterator.hasNext()) {
						objReviews = new JSONObject();
						objUsers = new JSONObject();
						JSONObject jsonObj = iterator.next();

						//write to Reivews
						objReviews.put("ReviewID", (String)jsonObj.get("ReviewID"));
						objReviews.put("UserID", (String)jsonObj.get("ReviewID"));
						objReviews.put("ProductID", (String)products.get("ProductID"));
						objReviews.put("Rate", (String)jsonObj.get("Overall"));
						//objReviews.put("Title", (String)jsonObj.get("Title"));
						//objReviews.put("Date", (String)jsonObj.get("Date"));

//						objReviews.put("Comment", (String)jsonObj.get("Content"));


						////////////AlChemyAPI CALL
						//Extract features from user' reviews
						FeatureExtraction extractObj = new FeatureExtraction();
						List<Factor> listFactors = new ArrayList<Factor>();

						System.out.println(file.getName() + "-->" + (String)jsonObj.get("Content"));

						Feature feature = extractObj.getFeatures((String)jsonObj.get("Content"));

						listFactors = feature.getListFeatures();

						objReviews.put("SentimentScore", feature.getSentimentScore());

						List<JSONObject> lstJSONObject = new ArrayList<JSONObject>();
						for (int i= 0; i< listFactors.size(); i++) {
							Factor factor = listFactors.get(i);
							JSONObject jObjFeatures = new JSONObject();
							jObjFeatures.put("words:", factor.getWords());
							jObjFeatures.put("type:", factor.getType());
							jObjFeatures.put("score:", factor.getScore());
							jObjFeatures.put("relevance:", factor.getRelevance());

							lstJSONObject.add(jObjFeatures);
						}

						if (lstJSONObject.size() > 0) {
							objReviews.put("Features", lstJSONObject);
						} else {
							objReviews.put("Features", "");
						}
			            ////////////AlChemyAPI CALL


						fileWriterReviews.write(objReviews.toJSONString());
						fileWriterReviews.write("\n");
						///////////

						//write to Users
						objUsers.put("UserID", (String)jsonObj.get("ReviewID"));
						objUsers.put("Author", (String)jsonObj.get("Author"));
						fileWriterUsers.write(objUsers.toJSONString());
						fileWriterUsers.write("\n");
						///////////
					}

			    }
			}




			bufferedWriterProducts.close();
			bufferedWriterReviews.close();
			bufferedWriterUsers.close();

		}  catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		catch (XPathExpressionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


	}

}
