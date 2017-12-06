package br.edu.utfpr.dv.siacoes.model;

import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Proposal {

	private int idProposal;
	private String title;
	private String subarea;
	private User student;
	private User supervisor;
	private User cosupervisor;
	private byte[] file;
	private DocumentType fileType;
	private List<ProposalAppraiser> appraisers;
	private int semester;
	private int year;
	private Date submissionDate;
	private Department department;
	
	public Proposal(){
		this.setIdProposal(0);
		this.setTitle("");
		this.setSubarea("");
		this.setStudent(new User());
		this.setSupervisor(new User());
		this.setCosupervisor(null);
		this.setFile(null);
		this.setAppraisers(null);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setSubmissionDate(DateUtils.getToday().getTime());
		this.setDepartment(new Department());
	}
	
	public Proposal(User student){
		this.setIdProposal(0);
		this.setTitle("");
		this.setSubarea("");
		this.setStudent(student);
		this.setSupervisor(new User());
		this.setCosupervisor(null);
		this.setFile(null);
		this.setAppraisers(null);
		this.setSemester(DateUtils.getSemester());
		this.setYear(DateUtils.getYear());
		this.setSubmissionDate(DateUtils.getToday().getTime());
		this.setDepartment(new Department());
	}
	
	public int getIdProposal() {
		return idProposal;
	}
	public void setIdProposal(int idProposal) {
		this.idProposal = idProposal;
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
	public DocumentType getFileType(){
		return fileType;
	}
	public void setFileType(DocumentType fileType){
		this.fileType = fileType;
	}
	public List<ProposalAppraiser> getAppraisers(){
		return this.appraisers;
	}
	public void setAppraisers(List<ProposalAppraiser> appraisers){
		this.appraisers = appraisers;
	}
	public int getSemester(){
		return this.semester;
	}
	public void setSemester(int semester){
		this.semester = semester;
	}
	public int getYear(){
		return this.year;
	}
	public void setYear(int year){
		this.year = year;
	}
	public Date getSubmissionDate(){
		return this.submissionDate;
	}
	public void setSubmissionDate(Date submissionDate){
		this.submissionDate = submissionDate;
	}
	public String toString(){
		return this.getTitle();
	}
	public void setDepartment(Department department){
		this.department = department;
	}
	public Department getDepartment(){
		return department;
	}
	
}
