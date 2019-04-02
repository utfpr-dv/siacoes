package br.edu.utfpr.dv.siacoes.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipJuryAppraiserScoreDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserScore;

public class InternshipJuryAppraiserScoreBO {
	
	private Connection conn;
	
	public InternshipJuryAppraiserScoreBO(){
		this.conn = null;
	}
	
	public InternshipJuryAppraiserScoreBO(Connection conn){
		this.conn = conn;
	}
	
	public boolean hasScore(int idInternshipJury, int idUser) throws Exception{
		try {
			InternshipJuryAppraiserScoreDAO dao = new InternshipJuryAppraiserScoreDAO(this.conn);
			
			return dao.hasScore(idInternshipJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipJuryAppraiserScore> listScores(int idInternshipJuryAppraiser) throws Exception{
		try {
			InternshipJuryAppraiserScoreDAO dao = new InternshipJuryAppraiserScoreDAO(this.conn);
			
			return dao.listScores(idInternshipJuryAppraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, InternshipJuryAppraiserScore score) throws Exception{
		if((score.getScore() < 0) || (score.getScore() > score.getInternshipEvaluationItem().getPonderosity())){
			throw new Exception("A nota deve estar entre 0 e o peso do quesito.");
		}
		if((score.getInternshipEvaluationItem() == null) || (score.getInternshipEvaluationItem().getIdInternshipEvaluationItem() == 0)){
			throw new Exception("Informe o quesito avaliado.");
		}
		if((score.getInternshipJuryAppraiser() == null) || (score.getInternshipJuryAppraiser().getIdInternshipJuryAppraiser() == 0)){
			throw new Exception("Informe o membro da banca.");
		}
		
		BigDecimal bd = new BigDecimal(score.getScore());
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    score.setScore(bd.doubleValue());
		
		try {
			InternshipJuryAppraiserScoreDAO dao = new InternshipJuryAppraiserScoreDAO(this.conn);
			
			return dao.save(idUser, score);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
