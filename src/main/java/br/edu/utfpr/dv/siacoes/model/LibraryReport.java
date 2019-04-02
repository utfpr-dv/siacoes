package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class LibraryReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int sequence;
	private String student;
	private String title;
	private String fileName;
	private boolean companyInfo;
	private boolean patent;
	private byte[] file;
	
	public LibraryReport(){
		this.setSequence(0);
		this.setStudent("");
		this.setTitle("");
		this.setFileName("");
		this.setCompanyInfo(false);
		this.setPatent(false);
		this.setFile(null);
	}
	
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getStudent() {
		return student;
	}
	public void setStudent(String student) {
		this.student = student;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isCompanyInfo() {
		return companyInfo;
	}
	public void setCompanyInfo(boolean companyInfo) {
		this.companyInfo = companyInfo;
	}
	public boolean isPatent() {
		return patent;
	}
	public void setPatent(boolean patent) {
		this.patent = patent;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}

}
