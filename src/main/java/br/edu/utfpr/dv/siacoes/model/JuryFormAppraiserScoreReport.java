package br.edu.utfpr.dv.siacoes.model;

public class JuryFormAppraiserScoreReport {
	
	private String description;
	private String name;
	private double scoreWriting;
	private double scoreOral;
	private double scoreArgumentation;
	private double score;

	public JuryFormAppraiserScoreReport(){
		this.setDescription("");
		this.setName("");
		this.setScoreWriting(0);
		this.setScoreOral(0);
		this.setScoreArgumentation(0);
		this.setScore(0);
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getScoreWriting() {
		return scoreWriting;
	}
	public void setScoreWriting(double scoreWriting) {
		this.scoreWriting = scoreWriting;
	}
	public double getScoreOral() {
		return scoreOral;
	}
	public void setScoreOral(double scoreOral) {
		this.scoreOral = scoreOral;
	}
	public double getScoreArgumentation() {
		return scoreArgumentation;
	}
	public void setScoreArgumentation(double scoreArgumentation) {
		this.scoreArgumentation = scoreArgumentation;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

}
