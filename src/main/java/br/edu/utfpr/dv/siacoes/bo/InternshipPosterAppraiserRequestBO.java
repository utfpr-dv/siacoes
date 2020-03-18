package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipPosterAppraiserRequestDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class InternshipPosterAppraiserRequestBO {
	
	public InternshipPosterAppraiserRequest findById(int id) throws Exception{
		try {
			InternshipPosterAppraiserRequestDAO dao = new InternshipPosterAppraiserRequestDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipPosterAppraiserRequest findByAppraiser(int idJury, int idUser) throws Exception{
		try {
			InternshipPosterAppraiserRequestDAO dao = new InternshipPosterAppraiserRequestDAO();
			
			return dao.findByAppraiser(idJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipPosterAppraiserRequest> listAppraisers(int idJuryRequest) throws Exception{
		try {
			InternshipPosterAppraiserRequestDAO dao = new InternshipPosterAppraiserRequestDAO();
			
			return dao.listAppraisers(idJuryRequest);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, InternshipPosterAppraiserRequest appraiser) throws Exception{
		try {
			if(Document.hasSignature(DocumentType.INTERNSHIPPOSTERREQUEST, appraiser.getRequest().getIdInternshipPosterRequest())) {
				throw new Exception("A solicitação não pode ser alterada pois ela já foi assinada.");
			}
			
			InternshipPosterAppraiserRequestDAO dao = new InternshipPosterAppraiserRequestDAO();
			
			return dao.save(idUser, appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
