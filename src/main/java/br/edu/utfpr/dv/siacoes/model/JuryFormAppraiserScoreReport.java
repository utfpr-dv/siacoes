package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class JuryFormAppraiserScoreReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idUser;
	private String description;
	private String name;
	private double scoreWriting;
	private double scoreOral;
	private double scoreArgumentation;
	private double score;

	public JuryFormAppraiserScoreReport(){
		this.setIdUser(0);
		this.setDescription("");
		this.setName("");
		this.setScoreWriting(0);
		this.setScoreOral(0);
		this.setScoreArgumentation(0);
		this.setScore(0);
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
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
