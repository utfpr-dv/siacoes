package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class LibraryCoverReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String city;
	private String course;
	private int semester;
	private int year;
	
	public LibraryCoverReport(){
		this.setCity("");
		this.setCourse("");
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
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
