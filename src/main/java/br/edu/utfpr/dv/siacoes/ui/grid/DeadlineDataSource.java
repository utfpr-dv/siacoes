package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Deadline;

public class DeadlineDataSource extends BasicDataSource {

	private int semester;
	private int year;
	private Date proposal;
	private Date project;
	private Date finalProject;
	private Date thesis;
	private Date finalThesis;
	
	public DeadlineDataSource(Deadline deadline) {
		this.setId(deadline.getIdDeadline());
		this.setSemester(deadline.getSemester());
		this.setYear(deadline.getYear());
		this.setProposal(deadline.getProposalDeadline());
		this.setProject(deadline.getProjectDeadline());
		this.setFinalProject(deadline.getProjectFinalDocumentDeadline());
		this.setThesis(deadline.getThesisDeadline());
		this.setFinalThesis(deadline.getThesisFinalDocumentDeadline());
	}
	
	public static List<DeadlineDataSource> load(List<Deadline> list) {
		List<DeadlineDataSource> ret = new ArrayList<DeadlineDataSource>();
		
		for(Deadline deadline : list) {
			ret.add(new DeadlineDataSource(deadline));
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
	public Date getProposal() {
		return proposal;
	}
	public void setProposal(Date proposal) {
		this.proposal = proposal;
	}
	public Date getProject() {
		return project;
	}
	public void setProject(Date project) {
		this.project = project;
	}
	public Date getFinalProject() {
		return finalProject;
	}
	public void setFinalProject(Date finalProject) {
		this.finalProject = finalProject;
	}
	public Date getThesis() {
		return thesis;
	}
	public void setThesis(Date thesis) {
		this.thesis = thesis;
	}
	public Date getFinalThesis() {
		return finalThesis;
	}
	public void setFinalThesis(Date finalThesis) {
		this.finalThesis = finalThesis;
	}
	
}
