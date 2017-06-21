package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ProposalAppraiserDAO;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;

public class ProposalAppraiserBO {

	public List<ProposalAppraiser> listAppraisers(int idProposal) throws Exception{
		try {
			ProposalAppraiserDAO dao = new ProposalAppraiserDAO();
			
			return dao.listAppraisers(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public ProposalAppraiser findById(int id) throws Exception{
		try {
			ProposalAppraiserDAO dao = new ProposalAppraiserDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public ProposalAppraiser findByAppraiser(int idProposal, int idAppraiser) throws Exception{
		try {
			ProposalAppraiserDAO dao = new ProposalAppraiserDAO();
			
			return dao.findByAppraiser(idProposal, idAppraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(ProposalAppraiser appraiser) throws Exception{
		try {
			if((appraiser.getProposal() == null) || (appraiser.getProposal().getIdProposal() == 0)){
				throw new Exception("Informe a proposta.");
			}
			if((appraiser.getAppraiser() == null) || (appraiser.getAppraiser().getIdUser() == 0)){
				throw new Exception("Informe o avaliador.");
			}
			
			ProposalAppraiserDAO dao = new ProposalAppraiserDAO();
			
			//Avaliação de proposta
			
			return dao.save(appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
