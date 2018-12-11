package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryAppraiserDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;

public class JuryAppraiserBO {
	
	public JuryAppraiser findById(int id) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryAppraiser findByAppraiser(int idJury, int idUser) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findByAppraiser(idJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryAppraiser> listAppraisers(int idJury) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.listAppraisers(idJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(JuryAppraiser appraiser) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.save(appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean appraiserHasJury(int idJury, int idUser, Date date) throws Exception{
		try{
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.appraiserHasJury(idJury, idUser, date);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idJuryAppraiser) throws Exception {
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findIdDepartment(idJuryAppraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
