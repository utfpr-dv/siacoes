package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryAppraiserDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;

public class InternshipJuryAppraiserBO {
	
	public InternshipJuryAppraiser findById(int id) throws Exception{
		try {
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipJuryAppraiser findByAppraiser(int idInternshipJury, int idUser) throws Exception{
		try {
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.findByAppraiser(idInternshipJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipJuryAppraiser> listAppraisers(int idInternshipJury) throws Exception{
		try {
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.listAppraisers(idInternshipJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(InternshipJuryAppraiser appraiser) throws Exception{
		try {
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.save(appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean appraiserHasJury(int idInternshipJury, int idUser, Date date) throws Exception{
		Date startDate, endDate;
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -59);
		startDate = cal.getTime();
		
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 59);
		endDate = cal.getTime();
		
		try{
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.appraiserHasJury(idInternshipJury, idUser, startDate, endDate);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idInternshipJury) throws Exception{
		try {
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.findIdDepartment(idInternshipJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
