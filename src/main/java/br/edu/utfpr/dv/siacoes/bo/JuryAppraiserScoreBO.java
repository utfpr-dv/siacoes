package br.edu.utfpr.dv.siacoes.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryAppraiserScoreDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserScore;

public class JuryAppraiserScoreBO {
	
	private Connection conn;
	
	public JuryAppraiserScoreBO(){
		this.conn = null;
	}
	
	public JuryAppraiserScoreBO(Connection conn){
		this.conn = conn;
	}
	
	public boolean hasScore(int idJury, int idUser) throws Exception{
		try {
			JuryAppraiserScoreDAO dao = new JuryAppraiserScoreDAO(this.conn);
			
			return dao.hasScore(idJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryAppraiserScore> listScores(int idJuryAppraiser) throws Exception{
		try {
			JuryAppraiserScoreDAO dao = new JuryAppraiserScoreDAO(this.conn);
			
			return dao.listScores(idJuryAppraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, JuryAppraiserScore score) throws Exception{
		if((score.getScore() < 0) || (score.getScore() > score.getEvaluationItem().getPonderosity())){
			throw new Exception("A nota deve estar entre 0 e peso do quesito.");
		}
		if((score.getEvaluationItem() == null) || (score.getEvaluationItem().getIdEvaluationItem() == 0)){
			throw new Exception("Informe o quesito avaliado.");
		}
		if((score.getJuryAppraiser() == null) || (score.getJuryAppraiser().getIdJuryAppraiser() == 0)){
			throw new Exception("Informe o membro da banca.");
		}
		
		BigDecimal bd = new BigDecimal(score.getScore());
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    score.setScore(bd.doubleValue());
		
		try {
			JuryAppraiserScoreDAO dao = new JuryAppraiserScoreDAO(this.conn);
			
			return dao.save(idUser, score);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
