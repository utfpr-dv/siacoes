package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Thesis implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idThesis;
	private Project project;
	private String title;
	private String subarea;
	private User student;
	private User supervisor;
	private User cosupervisor;
	private transient byte[] file;
	private int semester;
	private int year;
	private Date submissionDate;
	private String thesisAbstract;
	
	public Thesis(){
		this.setIdThesis(0);
		this.setProject(new Project());
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
	
	public Thesis(User student, Project project){
		this.setIdThesis(0);
		this.setProject(project);
		this.setTitle(project.getTitle());
		this.setSubarea(project.getSubarea());
		this.setStudent(student);
		this.setSupervisor(project.getSupervisor());
		this.setCosupervisor(project.getCosupervisor());
		this.setFile(null);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setSubmissionDate(DateUtils.getToday().getTime());
		this.setAbstract(project.getAbstract());
	}
	
	public int getIdThesis() {
		return idThesis;
	}
	public void setIdThesis(int idThesis) {
		this.idThesis = idThesis;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
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
		return thesisAbstract;
	}
	public void setAbstract(String thesisAbstract){
		this.thesisAbstract = thesisAbstract;
	}
	
}
