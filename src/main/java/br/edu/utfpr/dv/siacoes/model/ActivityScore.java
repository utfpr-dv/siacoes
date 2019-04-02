package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class ActivityScore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idActivity;
	private String activity;
	private double score;
	
	public ActivityScore() {
		this.setIdActivity(0);
		this.setActivity("");
		this.setScore(0);
	}
	
	public int getIdActivity() {
		return idActivity;
	}
	public void setIdActivity(int idActivity) {
		this.idActivity = idActivity;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

}
