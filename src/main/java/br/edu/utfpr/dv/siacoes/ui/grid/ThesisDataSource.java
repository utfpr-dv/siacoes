package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Thesis;

public class ThesisDataSource extends BasicDataSource {

	private int semester;
	private int year;
	private String student;
	private String supervisor;
	private String title;
	private Date submisstion;
	
	public ThesisDataSource(Thesis thesis) {
		this.setId(thesis.getIdThesis());
		this.setSemester(thesis.getSemester());
		this.setYear(thesis.getYear());
		this.setStudent(thesis.getStudent().getName());
		this.setSupervisor(thesis.getSupervisor().getName());
		this.setTitle(thesis.getTitle());
		this.setSubmisstion(thesis.getSubmissionDate());
	}
	
	public static List<ThesisDataSource> load(List<Thesis> list) {
		List<ThesisDataSource> ret = new ArrayList<ThesisDataSource>();
		
		for(Thesis thesis : list) {
			ret.add(new ThesisDataSource(thesis));
		}
		
		return ret;
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
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getSubmisstion() {
		return submisstion;
	}
	public void setSubmisstion(Date submisstion) {
		this.submisstion = submisstion;
	}
	
}
