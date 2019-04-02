package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Project implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int idProject;
	private Proposal proposal;
	private String title;
	private String subarea;
	private User student;
	private User supervisor;
	private User cosupervisor;
	private transient byte[] file;
	private int semester;
	private int year;
	private Date submissionDate;
	private String projectAbstract;
	
	public Project(){
		this.setIdProject(0);
		this.setProposal(new Proposal());
		this.setTitle("");
		this.setSubarea("");
		this.setStudent(new User());
		this.setSupervisor(new User());
		this.setCosupervisor(null);
		this.setFile(null);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setSubmissionDate(DateUtils.getToday().getTime());
		this.setAbstract("");
	}
	
	public Project(User student, Proposal proposal){
		this.setIdProject(0);
		this.setProposal(proposal);
		this.setTitle(proposal.getTitle());
		this.setSubarea(proposal.getSubarea());
		this.setStudent(student);
		this.setSupervisor(proposal.getSupervisor());
		this.setCosupervisor(proposal.getCosupervisor());
		this.setFile(null);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setSubmissionDate(DateUtils.getToday().getTime());
		this.setAbstract("");
	}
	
	public int getIdProject() {
		return idProject;
	}
	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}
	public Proposal getProposal() {
		return proposal;
	}
	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubarea() {
		return subarea;
	}
	public void setSubarea(String subarea) {
		this.subarea = subarea;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}
	public User getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}
	public User getCosupervisor(){
		return cosupervisor;
	}
	public void setCosupervisor(User cosupervisor){
		this.cosupervisor = cosupervisor;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
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
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public String getAbstract(){
		return projectAbstract;
	}
	public void setAbstract(String projectAbstract){
		this.projectAbstract = projectAbstract;
	}
	
}
