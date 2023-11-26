package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryStudentDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;

public class InternshipJuryStudentBO {
	
	public InternshipJuryStudent findById(int id) throws Exception{
		try {
			InternshipJuryStudentDAO dao = new InternshipJuryStudentDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipJuryStudent findByStudent(int idInternshipJury, int idStudent) throws Exception{
		try {
			InternshipJuryStudentDAO dao = new InternshipJuryStudentDAO();
			
			return dao.findByStudent(idInternshipJury, idStudent);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipJuryStudent> listByJury(int idInternshipJury) throws Exception{
		try {
			InternshipJuryStudentDAO dao = new InternshipJuryStudentDAO();
			
			return dao.listByJury(idInternshipJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipJuryStudent> listByStudent(int idStudent) throws Exception{
		try {
			InternshipJuryStudentDAO dao = new InternshipJuryStudentDAO();
			
			return dao.listByStudent(idStudent);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, InternshipJuryStudent student) throws Exception{
		if((student.getInternshipJury() == null) || (student.getInternshipJury().getIdInternshipJury() == 0)){
			throw new Exception("Informe a banca.");
		}
		
		if((student.getStudent() == null) || (student.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		
		try {
			InternshipJuryStudentDAO dao = new InternshipJuryStudentDAO();
			
			return dao.save(idUser, student);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, int id) throws Exception{
		try {
			InternshipJuryStudentDAO dao = new InternshipJuryStudentDAO();
			
			return dao.delete(idUser, id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
