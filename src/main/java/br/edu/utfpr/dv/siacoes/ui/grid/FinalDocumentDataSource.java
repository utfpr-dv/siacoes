package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.FinalDocument;

public class FinalDocumentDataSource extends BasicDataSource {
	
	private int stage;
	private int semester;
	private int year;
	private String student;
	private String title;
	private Date submission;
	private String _private;
	private String supervisorFeedback;
	
	public FinalDocumentDataSource(FinalDocument doc) {
		this.setId(doc.getIdFinalDocument());
		this.setStage(doc.getStage());
		this.setSemester(doc.getSemester());
		this.setYear(doc.getYear());
		this.setStudent(doc.getStudent().getName());
		this.setTitle(doc.getTitle());
		this.setSubmission(doc.getSubmissionDate());
		this.setPrivate(doc.isPrivate() ? "Sim" : "Não");
		this.setSupervisorFeedback(doc.getSupervisorFeedback().toString());
	}
	
	public static List<FinalDocumentDataSource> load(List<FinalDocument> list) {
		List<FinalDocumentDataSource> ret = new ArrayList<FinalDocumentDataSource>();
		
		for(FinalDocument doc : list) {
			ret.add(new FinalDocumentDataSource(doc));
		}
		
		return ret;
	}
	
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getSubmission() {
		return submission;
	}
	public void setSubmission(Date submission) {
		this.submission = submission;
	}
	public String getPrivate() {
		return _private;
	}
	public void setPrivate(String _private) {
		this._private = _private;
	}
	public String getSupervisorFeedback() {
		return supervisorFeedback;
	}
	public void setSupervisorFeedback(String supervisorFeedback) {
		this.supervisorFeedback = supervisorFeedback;
	}

}
