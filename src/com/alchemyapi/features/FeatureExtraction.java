package com.alchemyapi.features;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_KeywordParams;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class FeatureExtraction {

     	//public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
	   public Feature getFeatures (String reviewsText) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {

     		Feature feature = new Feature();
     		List<Factor> listFactors = new ArrayList<Factor>();

     		// Create an AlchemyAPI object.
			AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("testdir/api_key.txt");

			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(reviewsText);

			// 1. Get sentiment of text review
			Document doc = alchemyObj.TextGetTextSentiment(strBuilder.toString());

			String sentimentScore = getSentimentScore(doc);
			feature.setSentimentScore(sentimentScore);

			// 2. Text Extraction
			if (!"undefined".equals(sentimentScore)) {
				AlchemyAPI_KeywordParams keywordParams = new AlchemyAPI_KeywordParams();
				keywordParams.setSentiment(true);
				doc = alchemyObj.TextGetRankedKeywords(strBuilder.toString(), keywordParams);
				listFactors = getFeaturesFromDocument(doc);
			}

			feature.setListFeatures(listFactors);

//			System.out.println("Sentiment Score: " + feature.getSentimentScore());
//			for (int i= 0; i< listFactors.size(); i++) {
//				Factor factor = listFactors.get(i);
//				System.out.println("words: " + factor.getWords());
//				System.out.println("type: " + factor.getType());
//				System.out.println("score: " + factor.getScore());
//				System.out.println("relevance: " + factor.getRelevance());
//			}

			return feature;
		}

		// get sentiment score of text reviews
		private String getSentimentScore(Document doc) {
			try {
			    DOMSource domSource = new DOMSource(doc);
			    StringWriter writer = new StringWriter();
			    StreamResult result = new StreamResult(writer);

			    TransformerFactory tf = TransformerFactory.newInstance();
			    Transformer transformer = tf.newTransformer();
			    transformer.transform(domSource, result);

			    int x1 =writer.toString().indexOf("<score>") + 7;
			    int x2 =writer.toString().indexOf("</score>");

			    writer.toString().substring(x1, x2);

			    return writer.toString().substring(x1, x2);

			} catch (TransformerException ex) {
			    ex.printStackTrace();
			    return "undefined";
			} catch (StringIndexOutOfBoundsException outEx) {
				return "undefined";
			}
		}

		//get features method
		private List<Factor> getFeaturesFromDocument(Document doc) {
			try {
			    DOMSource domSource = new DOMSource(doc);
			    StringWriter writer = new StringWriter();
			    StreamResult result = new StreamResult(writer);

			    TransformerFactory tf = TransformerFactory.newInstance();
			    Transformer transformer = tf.newTransformer();
			    transformer.transform(domSource, result);

			    List<Factor> features = new ArrayList<Factor>();

			    NodeList nList = doc.getElementsByTagName("keyword");
			    for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);

//					System.out.println("\nCurrent Element :" + nNode.getNodeName());
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;
						String type = "";

						if (eElement.getElementsByTagName("type").item(0) != null) {
							type = eElement.getElementsByTagName("type").item(0).getTextContent();
						}

						if ("positive".equals(type) || "negative".equals(type)) {

//							System.out.println("First Name : " + eElement.getElementsByTagName("relevance").item(0).getTextContent());
//							System.out.println("Last Name : " + eElement.getElementsByTagName("score").item(0).getTextContent());
//							System.out.println("Nick Name : " + eElement.getElementsByTagName("type").item(0).getTextContent());
//							System.out.println("Salary : " + eElement.getElementsByTagName("text").item(0).getTextContent());

							String relevance = eElement.getElementsByTagName("relevance").item(0).getTextContent();
							String score = eElement.getElementsByTagName("score").item(0).getTextContent();
							String text = eElement.getElementsByTagName("text").item(0).getTextContent();

							Factor factor = new Factor();
							factor.setRelevance(Double.parseDouble(relevance));
							factor.setScore(Double.parseDouble(score));
							factor.setType(type);
							factor.setWords(text);
							features.add(factor);
						}

					}
				}

			    //return writer.toString();

			    return features;

			} catch (TransformerException ex) {
			    ex.printStackTrace();
			    return null;
			}

		}

}
