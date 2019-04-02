package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Semester implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Campus campus;
	private int semester;
	private int year;
	private Date startDate;
	private Date endDate;
	
	public Semester() {
		this.setCampus(new Campus());
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setStartDate(DateUtils.getStartDate(this.getSemester(), this.getYear()));
		this.setEndDate(DateUtils.getEndDate(this.getSemester(), this.getYear()));
	}
	
	public Semester(int idCampus, int semester, int year) {
		this.setCampus(new Campus());
		this.getCampus().setIdCampus(idCampus);
		this.setSemester(semester);
		this.setYear(year);
		this.setStartDate(DateUtils.getStartDate(this.getSemester(), this.getYear()));
		this.setEndDate(DateUtils.getEndDate(this.getSemester(), this.getYear()));
	}
	
	public Semester(int idCampus) {
		this.setCampus(new Campus());
		this.getCampus().setIdCampus(idCampus);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setStartDate(DateUtils.getStartDate(this.getSemester(), this.getYear()));
		this.setEndDate(DateUtils.getEndDate(this.getSemester(), this.getYear()));
	}
	
	public Campus getCampus() {
		return campus;
	}
	public void setCampus(Campus campus) {
		this.campus = campus;
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
