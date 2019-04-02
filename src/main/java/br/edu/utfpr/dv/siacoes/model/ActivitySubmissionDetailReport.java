package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class ActivitySubmissionDetailReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String activity;
	private int group;
	private String unit;
	private double score;
	private double amount;
	private double total;
	private int semester;
	private int year;
	
	public ActivitySubmissionDetailReport(){
		this.setActivity("");
		this.setGroup(0);
		this.setUnit("");
		this.setScore(0);
		this.setAmount(0);
		this.setTotal(0);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
	}
	
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

}
