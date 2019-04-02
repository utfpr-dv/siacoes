package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class JuryBySemester implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int year;
	private int semester;
	private int juryStage1;
	private int juryStage2;
	
	public JuryBySemester() {
		this.setYear(DateUtils.getYear());
		this.setSemester(DateUtils.getSemester());
		this.setJuryStage1(0);
		this.setJuryStage2(0);
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getJuryStage1() {
		return juryStage1;
	}
	public void setJuryStage1(int juryStage1) {
		this.juryStage1 = juryStage1;
	}
	public int getJuryStage2() {
		return juryStage2;
	}
	public void setJuryStage2(int juryStage2) {
		this.juryStage2 = juryStage2;
	}

}
