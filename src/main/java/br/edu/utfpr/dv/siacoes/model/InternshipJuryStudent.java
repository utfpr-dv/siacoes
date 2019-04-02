package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class InternshipJuryStudent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idInternshipJuryStudent;
	private InternshipJury internshipJury;
	private User student;
	
	public InternshipJuryStudent(){
		this.setIdInternshipJuryStudent(0);
		this.setInternshipJury(new InternshipJury());
		this.setStudent(new User());
	}
	
	public int getIdInternshipJuryStudent(){
		return idInternshipJuryStudent;
	}
	public void setIdInternshipJuryStudent(int idInternshipJuryStudent){
		this.idInternshipJuryStudent = idInternshipJuryStudent;
	}
	public InternshipJury getInternshipJury() {
		return internshipJury;
	}
	public void setInternshipJury(InternshipJury internshipJury) {
		this.internshipJury = internshipJury;
	}
	public User getStudent() {
		return student;
	}
	public void setStudent(User student) {
		this.student = student;
	}

}
