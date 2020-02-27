package br.edu.utfpr.dv.siacoes.report.dataset.v1;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.sign.SignDataset;

public class Attendance extends SignDataset {
	
	private static final long serialVersionUID = 1L;
	
	private int idGroup;
	private int stage;
	private String title;
	private int idStudent;
	private int idSupervisor;
	private List<br.edu.utfpr.dv.siacoes.model.Attendance> attendances;
	
	public Attendance(){
		this.setIdGroup(0);
		this.setStage(1);
		this.setTitle("");
		this.setAttendances(new ArrayList<br.edu.utfpr.dv.siacoes.model.Attendance>());
		this.setIdStudent(0);
		this.setIdSupervisor(0);
	}
	
	public int getIdGroup() {
		return idGroup;
	}
	public void setIdGroup(int idGroup) {
		this.idGroup = idGroup;
	}
	public int getStage(){
		return stage;
	}
	public void setStage(int stage){
		this.stage = stage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStudent() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdStudent()) {
				return sign.getName();
			}
		}
		
		return "";
	}
	public String getSupervisor() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdSupervisor()) {
				return sign.getName();
			}
		}
		
		return "";
	}
	public List<br.edu.utfpr.dv.siacoes.model.Attendance> getAttendances() {
		return attendances;
	}
	public void setAttendances(List<br.edu.utfpr.dv.siacoes.model.Attendance> attendances) {
		this.attendances = attendances;
	}
	public int getIdStudent() {
		return idStudent;
	}
	public void setIdStudent(int idStudent) {
		this.idStudent = idStudent;
	}
	public int getIdSupervisor() {
		return idSupervisor;
	}
	public void setIdSupervisor(int idSupervisor) {
		this.idSupervisor = idSupervisor;
	}
	public InputStream getStudentSignature() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdStudent()) {
				return sign.getSignature();
			}
		}
		
		return null;
	}
	public InputStream getSupervisorSignature() {
		for(Signature sign : this.getSignatures()) {
			if(sign.getIdUser() == this.getIdSupervisor()) {
				return sign.getSignature();
			}
		}
		
		return null;
	}

}
