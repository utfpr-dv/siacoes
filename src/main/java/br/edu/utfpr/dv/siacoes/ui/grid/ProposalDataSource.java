package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Proposal;

public class ProposalDataSource extends BasicDataSource {

	private String title;
	private int semester;
	private int year;
	private String student;
	private String supervisor;
	private Date submission;
	private String hasFile;
	private String supervisorFeedback;
	
	public ProposalDataSource(Proposal proposal) {
		this.setId(proposal.getIdProposal());
		this.setTitle(proposal.getTitle());
		this.setSemester(proposal.getSemester());
		this.setYear(proposal.getYear());
		this.setStudent(proposal.getStudent().getName());
		this.setSupervisor(proposal.getSupervisor().getName());
		this.setSubmission(proposal.getSubmissionDate());
		this.setHasFile(proposal.getFile() != null ? "Sim" : "Não");
		this.setSupervisorFeedback(proposal.getSupervisorFeedback().toString());
	}
	
	public static List<ProposalDataSource> load(List<Proposal> list) {
		List<ProposalDataSource> ret = new ArrayList<ProposalDataSource>();
		
		for(Proposal proposal : list) {
			ret.add(new ProposalDataSource(proposal));
		}
		
		return ret;
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
	public Date getSubmission() {
		return submission;
	}
	public void setSubmission(Date submission) {
		this.submission = submission;
	}
	public String getHasFile() {
		return hasFile;
	}
	public void setHasFile(String hasFile) {
		this.hasFile = hasFile;
	}
	public String getSupervisorFeedback() {
		return supervisorFeedback;
	}
	public void setSupervisorFeedback(String supervisorFeedback) {
		this.supervisorFeedback = supervisorFeedback;
	}
	
}
