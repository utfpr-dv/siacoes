package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Calendar;
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
		Date startDate, endDate;
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -59);
		startDate = cal.getTime();
		
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 59);
		endDate = cal.getTime();
		
		try{
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.appraiserHasJury(idJury, idUser, startDate, endDate);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
