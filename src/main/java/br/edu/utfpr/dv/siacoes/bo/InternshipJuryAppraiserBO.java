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
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
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
	
	public InternshipJuryAppraiser findChair(int idInternshipJury) throws Exception{
		try {
			InternshipJuryAppraiserDAO dao = new InternshipJuryAppraiserDAO();
			
			return dao.findChair(idInternshipJury);
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
			
			if(Document.hasSignature(DocumentType.INTERNSHIPJURY, appraiser.getInternshipJury().getIdInternshipJury())) {
				throw new Exception("A banca não pode ser alterada pois a ficha de avaliação já foi assinada.");
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
	
	public int changeAppraiser(int idUser, InternshipJuryAppraiser member, InternshipJuryAppraiser substitute) throws Exception {
		if((member == null) || (member.getAppraiser() == null) || (member.getAppraiser().getIdUser() == 0)) {
			throw new Exception("Selecione o membro titular da banca.");
		}
		if((substitute == null) || (substitute.getAppraiser() == null) || (substitute.getAppraiser().getIdUser() == 0)) {
			throw new Exception("Selecione o suplente da banca.");
		}
		if(member.isChair()) {
			throw new Exception("O presidente da banca não pode ser substituído.");
		}
		if(this.findChair(member.getInternshipJury().getIdInternshipJury()).getAppraiser().getIdUser() != idUser) {
			throw new Exception("Apenas o presidente da banca pode fazer a substituição de membros.");
		}
		if(new InternshipJuryAppraiserScoreBO().hasScore(member.getInternshipJury().getIdInternshipJury(), member.getAppraiser().getIdUser())) {
			throw new Exception("A substituição não pode ser efetuada pois o membro já tem notas lançadas.");
		}
		if(Document.hasSignature(DocumentType.INTERNSHIPJURY, member.getInternshipJury().getIdInternshipJury())) {
			throw new Exception("A banca não pode ser alterada pois a ficha de avaliação já foi assinada.");
		}
		
		member.setSubstitute(true);
		this.save(idUser, member);
		
		substitute.setSubstitute(false);
		this.save(idUser, substitute);
		
		return 1;
	}

}
