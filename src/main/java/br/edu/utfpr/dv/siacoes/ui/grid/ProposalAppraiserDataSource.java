package br.edu.utfpr.dv.siacoes.ui.grid;

import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;

public class ProposalAppraiserDataSource extends BasicDataSource {

	private int idUser;
	private String name;
	private String feedback;
	private String indication;
	private DocumentType fileType;
	private int semester;
	private int year;
	private String title;
	private String student;
	private LocalDate submission;
	
	public ProposalAppraiserDataSource(ProposalAppraiser appraiser) {
		this.setId(appraiser.getIdProposalAppraiser());
		this.setIdUser(appraiser.getAppraiser().getIdUser());
		this.setName(appraiser.getAppraiser().getName());
		this.setFeedback(appraiser.getFeedback().toString());
		this.setIndication(appraiser.isSupervisorIndication() ? "Orientador" : "Resp. TCC");
		this.setFileType(appraiser.getFile() == null ? DocumentType.UNDEFINED : DocumentType.PDF);
		this.setSemester(appraiser.getProposal().getSemester());
		this.setYear(appraiser.getProposal().getYear());
		this.setTitle(appraiser.getProposal().getTitle());
		this.setStudent(appraiser.getProposal().getStudent().getName());
		this.setSubmission(DateUtils.convertToLocalDate(appraiser.getProposal().getSubmissionDate()));
	}
	
	public static List<ProposalAppraiserDataSource> load(List<ProposalAppraiser> list) {
		List<ProposalAppraiserDataSource> ret = new ArrayList<ProposalAppraiserDataSource>();
		
		for(ProposalAppraiser appraiser : list) {
			ret.add(new ProposalAppraiserDataSource(appraiser));
		}
		
		return ret;
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public String getIndication() {
		return indication;
	}
	public void setIndication(String indication) {
		this.indication = indication;
	}
	public DocumentType getFileType() {
		return fileType;
	}
	public void setFileType(DocumentType fileType) {
		this.fileType = fileType;
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
	public LocalDate getSubmission() {
		return submission;
	}
	public void setSubmission(LocalDate submission) {
		this.submission = submission;
	}
	
}
