package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SemesterDAO;
import br.edu.utfpr.dv.siacoes.model.Semester;

public class SemesterBO {

	public List<Semester> listAll() throws Exception{
		try{
			SemesterDAO dao = new SemesterDAO();
			
			return dao.listAll();
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Semester> listByCampus(int idCampus) throws Exception{
		try{
			SemesterDAO dao = new SemesterDAO();
			
			return dao.listByCampus(idCampus);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Semester findBySemester(int idCampus, int semester, int year) throws Exception{
		try{
			SemesterDAO dao = new SemesterDAO();
			
			Semester s = dao.findBySemester(idCampus, semester, year);
			
			if(s == null) {
				return new Semester(idCampus, semester, year);
			} else {
				return s;
			}
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Semester findByDate(int idCampus, Date date) throws Exception{
		try{
			SemesterDAO dao = new SemesterDAO();
			
			return dao.findByDate(idCampus, date);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, Semester semester) throws Exception{
		try{
			SemesterDAO dao = new SemesterDAO();
			
			return dao.save(idUser, semester);
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
