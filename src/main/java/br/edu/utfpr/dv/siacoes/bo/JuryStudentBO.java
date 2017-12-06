package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryStudentDAO;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;

public class JuryStudentBO {
	
	public JuryStudent findById(int id) throws Exception{
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryStudent findByStudent(int idJury, int idStudent) throws Exception{
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.findByStudent(idJury, idStudent);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryStudent> listByJury(int idJury) throws Exception{
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.listByJury(idJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryStudent> listByStudent(int idStudent) throws Exception{
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.listByStudent(idStudent);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	/*public int save(JuryStudent student) throws Exception{
		if((student.getJury() == null) || (student.getJury().getIdJury() == 0)){
			throw new Exception("Informe a banca.");
		}
		
		if((student.getStudent() == null) || (student.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		
		try {
			JuryStudentDAO dao = new JuryStudentDAO();
			
			return dao.save(student);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}*/

}
