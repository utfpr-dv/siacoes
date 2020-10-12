package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipFinalDocumentDataSource extends BasicDataSource {

	private String student;
	private String company;
	private LocalDate submission;
	private String _private;
	private String feedback;
	
	public InternshipFinalDocumentDataSource(InternshipFinalDocument doc) {
		this.setId(doc.getIdInternshipFinalDocument());
		this.setStudent(doc.getInternship().getStudent().getName());
		this.setCompany(doc.getInternship().getCompany().getName());
		this.setSubmission(DateUtils.convertToLocalDate(doc.getSubmissionDate()));
		this.setPrivate(doc.isPrivate() ? "Sim" : "Não");
		this.setFeedback(doc.getSupervisorFeedback().toString());
	}
	
	public static List<InternshipFinalDocumentDataSource> load(List<InternshipFinalDocument> list) {
		List<InternshipFinalDocumentDataSource> ret = new ArrayList<InternshipFinalDocumentDataSource>();
		
		for(InternshipFinalDocument doc : list) {
			ret.add(new InternshipFinalDocumentDataSource(doc));
		}
		
		return ret;
	}
	
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public LocalDate getSubmission() {
		return submission;
	}
	public void setSubmission(LocalDate submission) {
		this.submission = submission;
	}
	public String getPrivate() {
		return _private;
	}
	public void setPrivate(String _private) {
		this._private = _private;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
}
