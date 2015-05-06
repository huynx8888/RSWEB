package com.alchemyapi.features;

import java.util.List;

public class Feature {
	//sentiment Score of reviews
	private String sentimentScore;

	//list of features
	private List<Factor> listFeatures;

	public String getSentimentScore() {
		return sentimentScore;
	}

	public void setSentimentScore(String sentimentScore) {
		this.sentimentScore = sentimentScore;
	}

	public List<Factor> getListFeatures() {
		return listFeatures;
	}

	public void setListFeatures(List<Factor> listFeatures) {
		this.listFeatures = listFeatures;
	}


}
