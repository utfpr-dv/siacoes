package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryAppraiserDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

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
	
	public JuryAppraiser findChair(int idJury) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findChair(idJury);
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
	
	public int save(int idUser, JuryAppraiser appraiser) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			SigetConfig config = new SigetConfigBO().findByDepartment(new JuryBO().findIdDepartment(appraiser.getJury().getIdJury()));
			
			if((config.getMaxFileSize() > 0) && (appraiser.getFile() != null) && ((appraiser.getIdJuryAppraiser() == 0) || !Arrays.equals(appraiser.getFile(), dao.getFile(appraiser.getIdJuryAppraiser()))) && (appraiser.getFile().length > config.getMaxFileSize())) {
				throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
			}
			if((config.getMaxFileSize() > 0) && (appraiser.getAdditionalFile() != null) && ((appraiser.getIdJuryAppraiser() == 0) || !Arrays.equals(appraiser.getAdditionalFile(), dao.getAdditionalFile(appraiser.getIdJuryAppraiser()))) && (appraiser.getAdditionalFile().length > config.getMaxFileSize())) {
				throw new Exception("O arquivo complementar deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
			}
			if(Document.hasSignature(DocumentType.JURY, appraiser.getJury().getIdJury())) {
				throw new Exception("A banca não pode ser alterada pois a ficha de avaliação já foi assinada.");
			}
			
			return dao.save(idUser, appraiser);
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
	
	public int changeAppraiser(int idUser, JuryAppraiser member, JuryAppraiser substitute) throws Exception {
		if((member == null) || (member.getAppraiser() == null) || (member.getAppraiser().getIdUser() == 0)) {
			throw new Exception("Selecione o membro titular da banca.");
		}
		if((substitute == null) || (substitute.getAppraiser() == null) || (substitute.getAppraiser().getIdUser() == 0)) {
			throw new Exception("Selecione o suplente da banca.");
		}
		if(member.isChair()) {
			throw new Exception("O presidente da banca não pode ser substituído.");
		}
		if(this.findChair(member.getJury().getIdJury()).getAppraiser().getIdUser() != idUser) {
			throw new Exception("Apenas o presidente da banca pode fazer a substituição de membros.");
		}
		if(new JuryAppraiserScoreBO().hasScore(member.getJury().getIdJury(), member.getAppraiser().getIdUser())) {
			throw new Exception("A substituição não pode ser efetuada pois o membro já tem notas lançadas.");
		}
		if(Document.hasSignature(DocumentType.JURY, member.getJury().getIdJury())) {
			throw new Exception("A banca não pode ser alterada pois a ficha de avaliação já foi assinada.");
		}
		
		member.setSubstitute(true);
		this.save(idUser, member);
		
		substitute.setSubstitute(false);
		this.save(idUser, substitute);
		
		return 1;
	}
	
}
