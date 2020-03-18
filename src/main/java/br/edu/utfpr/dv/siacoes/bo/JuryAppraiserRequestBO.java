package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryAppraiserRequestDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class JuryAppraiserRequestBO {

	public JuryAppraiserRequest findById(int id) throws Exception{
		try {
			JuryAppraiserRequestDAO dao = new JuryAppraiserRequestDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryAppraiserRequest findByAppraiser(int idJury, int idUser) throws Exception{
		try {
			JuryAppraiserRequestDAO dao = new JuryAppraiserRequestDAO();
			
			return dao.findByAppraiser(idJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryAppraiserRequest> listAppraisers(int idJuryRequest) throws Exception{
		try {
			JuryAppraiserRequestDAO dao = new JuryAppraiserRequestDAO();
			
			return dao.listAppraisers(idJuryRequest);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, JuryAppraiserRequest appraiser) throws Exception{
		try {
			if(Document.hasSignature(DocumentType.JURYREQUEST, appraiser.getJuryRequest().getIdJuryRequest())) {
				throw new Exception("A solicitação não pode ser alterada pois ela já foi assinada.");
			}
			
			JuryAppraiserRequestDAO dao = new JuryAppraiserRequestDAO();
			
			return dao.save(idUser, appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean appraiserHasJuryRequest(int idJuryRequest, int idUser, Date date) throws Exception{
		try{
			JuryAppraiserRequestDAO dao = new JuryAppraiserRequestDAO();
			
			return dao.appraiserHasJuryRequest(idJuryRequest, idUser, date);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
