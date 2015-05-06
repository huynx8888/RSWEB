package com.alchemyapi.features;

public class Factor {
	private double relevance;
	private double score;
	private String type;
	private String words;


	public double getRelevance() {
		return relevance;
	}
	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWords() {
		return words;
	}
	public void setWords(String words) {
		this.words = words;
	}


}
