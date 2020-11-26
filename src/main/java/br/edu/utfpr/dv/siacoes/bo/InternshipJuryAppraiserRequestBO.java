package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryAppraiserRequestDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class InternshipJuryAppraiserRequestBO {

	public InternshipJuryAppraiserRequest findById(int id) throws Exception{
		try {
			InternshipJuryAppraiserRequestDAO dao = new InternshipJuryAppraiserRequestDAO();

			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			throw new Exception(e.getMessage());
		}
	}

	public InternshipJuryAppraiserRequest findByAppraiser(int idInternshipJury, int idUser) throws Exception{
		try {
			InternshipJuryAppraiserRequestDAO dao = new InternshipJuryAppraiserRequestDAO();

			return dao.findByAppraiser(idInternshipJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			throw new Exception(e.getMessage());
		}
	}

	public List<InternshipJuryAppraiserRequest> listAppraisers(int idInternshipJuryRequest) throws Exception{
		try {
			InternshipJuryAppraiserRequestDAO dao = new InternshipJuryAppraiserRequestDAO();

			return dao.listAppraisers(idInternshipJuryRequest);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			throw new Exception(e.getMessage());
		}
	}

	public int save(int idUser, InternshipJuryAppraiserRequest appraiser) throws Exception{
		try {
			if(Document.hasSignature(DocumentType.INTERNSHIPJURYREQUEST, appraiser.getInternshipJuryRequest().getIdInternshipJuryRequest())) {
				throw new Exception("A solicitação não pode ser alterada pois ela já foi assinada.");
			}

			InternshipJuryAppraiserRequestDAO dao = new InternshipJuryAppraiserRequestDAO();

			return dao.save(idUser, appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			throw new Exception(e.getMessage());
		}
	}

	public boolean appraiserHasInternshipJuryRequest(int idInternshipJuryRequest, int idUser, Date date) throws Exception{
		try{
			InternshipJuryAppraiserRequestDAO dao = new InternshipJuryAppraiserRequestDAO();

			return dao.appraiserHasJuryRequest(idInternshipJuryRequest, idUser, date);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			throw new Exception(e.getMessage());
		}
	}

}
