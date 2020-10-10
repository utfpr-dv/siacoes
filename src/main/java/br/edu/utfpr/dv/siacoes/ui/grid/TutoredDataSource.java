package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Tutored;

public class TutoredDataSource extends BasicDataSource {

	private String student;
	private int stage;
	private String title;
	private int semester;
	private int year;
	
	public TutoredDataSource(Tutored tutored) {
		this.setId(tutored.getProposal().getIdProposal());
		this.setStudent(tutored.getStudent().getName());
		this.setStage(tutored.getStage());
		this.setTitle(tutored.getTitle());
		this.setSemester(tutored.getSemester());
		this.setYear(tutored.getYear());
	}
	
	public static List<TutoredDataSource> load(List<Tutored> list) {
		List<TutoredDataSource> ret = new ArrayList<TutoredDataSource>();
		
		for(Tutored tutored : list) {
			ret.add(new TutoredDataSource(tutored));
		}
		
		return ret;
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
