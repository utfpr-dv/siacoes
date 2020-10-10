package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;

public class JuryParticipantDataSource extends BasicDataSource {
	
	private int idUser;
	private String name;
	private String studentCode;
	
	public JuryParticipantDataSource(InternshipJuryStudent student) {
		this.setId(student.getIdInternshipJuryStudent());
		this.setIdUser(student.getStudent().getIdUser());
		this.setName(student.getStudent().getName());
		this.setStudentCode(student.getStudent().getStudentCode());
	}
	
	public JuryParticipantDataSource(JuryStudent student) {
		this.setId(student.getIdJuryStudent());
		this.setIdUser(student.getStudent().getIdUser());
		this.setName(student.getStudent().getName());
		this.setStudentCode(student.getStudent().getStudentCode());
	}
	
	public static List<JuryParticipantDataSource> loadInternship(List<InternshipJuryStudent> list) {
		List<JuryParticipantDataSource> ret = new ArrayList<JuryParticipantDataSource>();
		
		for(InternshipJuryStudent student : list) {
			ret.add(new JuryParticipantDataSource(student));
		}
		
		return ret;
	}
	
	public static List<JuryParticipantDataSource> load(List<JuryStudent> list) {
		List<JuryParticipantDataSource> ret = new ArrayList<JuryParticipantDataSource>();
		
		for(JuryStudent student : list) {
			ret.add(new JuryParticipantDataSource(student));
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
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

}
