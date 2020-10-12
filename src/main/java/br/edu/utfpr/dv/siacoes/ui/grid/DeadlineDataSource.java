package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class DeadlineDataSource extends BasicDataSource {

	private int semester;
	private int year;
	private LocalDate proposal;
	private LocalDate project;
	private LocalDate finalProject;
	private LocalDate thesis;
	private LocalDate finalThesis;
	
	public DeadlineDataSource(Deadline deadline) {
		this.setId(deadline.getIdDeadline());
		this.setSemester(deadline.getSemester());
		this.setYear(deadline.getYear());
		this.setProposal(DateUtils.convertToLocalDate(deadline.getProposalDeadline()));
		this.setProject(DateUtils.convertToLocalDate(deadline.getProjectDeadline()));
		this.setFinalProject(DateUtils.convertToLocalDate(deadline.getProjectFinalDocumentDeadline()));
		this.setThesis(DateUtils.convertToLocalDate(deadline.getThesisDeadline()));
		this.setFinalThesis(DateUtils.convertToLocalDate(deadline.getThesisFinalDocumentDeadline()));
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
	public LocalDate getProposal() {
		return proposal;
	}
	public void setProposal(LocalDate proposal) {
		this.proposal = proposal;
	}
	public LocalDate getProject() {
		return project;
	}
	public void setProject(LocalDate project) {
		this.project = project;
	}
	public LocalDate getFinalProject() {
		return finalProject;
	}
	public void setFinalProject(LocalDate finalProject) {
		this.finalProject = finalProject;
	}
	public LocalDate getThesis() {
		return thesis;
	}
	public void setThesis(LocalDate thesis) {
		this.thesis = thesis;
	}
	public LocalDate getFinalThesis() {
		return finalThesis;
	}
	public void setFinalThesis(LocalDate finalThesis) {
		this.finalThesis = finalThesis;
	}
	
}
