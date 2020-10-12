package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class InternshipLibraryDataSource extends BasicDataSource {

	private LocalDate submission;
	private String title;
	private String student;
	private String company;
	
	public InternshipLibraryDataSource(InternshipFinalDocument doc) {
		this.setId(doc.getIdInternshipFinalDocument());
		this.setSubmission(DateUtils.convertToLocalDate(doc.getSubmissionDate()));
		this.setTitle(doc.getInternship().getReportTitle());
		this.setStudent(doc.getInternship().getStudent().getName());
		this.setCompany(doc.getInternship().getCompany().getName());
	}
	
	public static List<InternshipLibraryDataSource> load(List<InternshipFinalDocument> list) {
		List<InternshipLibraryDataSource> ret = new ArrayList<InternshipLibraryDataSource>();
		
		for(InternshipFinalDocument doc : list) {
			ret.add(new InternshipLibraryDataSource(doc));
		}
		
		return ret;
	}
	
	public LocalDate getSubmission() {
		return submission;
	}
	public void setSubmission(LocalDate submission) {
		this.submission = submission;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
}
