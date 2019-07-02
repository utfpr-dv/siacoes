package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryAppraiserDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

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
	
	public int save(int idUser, InternshipJuryAppraiser appraiser) throws Exception{
		try {
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipJuryBO().findIdDepartment(appraiser.getInternshipJury().getIdInternshipJury()));
			
			if((config.getMaxFileSize() > 0) && (appraiser.getFile() != null) && ((appraiser.getIdInternshipJuryAppraiser() == 0) || !Arrays.equals(appraiser.getFile(), dao.getFile(appraiser.getIdInternshipJuryAppraiser()))) && (appraiser.getFile().length > config.getMaxFileSize())) {
				throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
			}
			if((config.getMaxFileSize() > 0) && (appraiser.getAdditionalFile() != null) && ((appraiser.getIdInternshipJuryAppraiser() == 0) || !Arrays.equals(appraiser.getAdditionalFile(), dao.getAdditionalFile(appraiser.getIdInternshipJuryAppraiser()))) && (appraiser.getAdditionalFile().length > config.getMaxFileSize())) {
				throw new Exception("O arquivo complementar deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
			}
			
			return dao.save(idUser, appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean appraiserHasJury(int idInternshipJury, int idUser, Date date) throws Exception{
		try{
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.appraiserHasJury(idInternshipJury, idUser, date);
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
