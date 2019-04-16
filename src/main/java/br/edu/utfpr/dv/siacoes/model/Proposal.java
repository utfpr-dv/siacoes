package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Proposal implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int idProposal;
	private String title;
	private String subarea;
	private User student;
	private User supervisor;
	private User cosupervisor;
	private transient byte[] file;
	private List<ProposalAppraiser> appraisers;
	private int semester;
	private int year;
	private Date submissionDate;
	private Department department;
	private boolean invalidated;
	private ProposalFeedback supervisorFeedback;
	private Date supervisorFeedbackDate;
	private String supervisorComments;
	private transient boolean fileUploaded;
	
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
		this.setInvalidated(false);
		this.setSupervisorFeedback(ProposalFeedback.NONE);
		this.setSupervisorFeedbackDate(null);
		this.setSupervisorComments("");
		this.setFileUploaded(false);
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
		this.setInvalidated(false);
		this.setSupervisorFeedback(ProposalFeedback.NONE);
		this.setSupervisorFeedbackDate(null);
		this.setSupervisorComments("");
		this.setFileUploaded(false);
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
	public boolean isInvalidated() {
		return invalidated;
	}
	public void setInvalidated(boolean invalidated) {
		this.invalidated = invalidated;
	}
	public ProposalFeedback getSupervisorFeedback() {
		return supervisorFeedback;
	}
	public void setSupervisorFeedback(ProposalFeedback supervisorFeedback) {
		this.supervisorFeedback = supervisorFeedback;
	}
	public Date getSupervisorFeedbackDate() {
		return supervisorFeedbackDate;
	}
	public void setSupervisorFeedbackDate(Date supervisorFeedbackDate) {
		this.supervisorFeedbackDate = supervisorFeedbackDate;
	}
	public String getSupervisorComments() {
		return supervisorComments;
	}
	public void setSupervisorComments(String supervisorComments) {
		this.supervisorComments = supervisorComments;
	}
	public boolean isFileUploaded() {
		return fileUploaded;
	}
	public void setFileUploaded(boolean fileUploaded) {
		this.fileUploaded = fileUploaded;
	}
	
}
