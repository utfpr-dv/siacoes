package br.edu.utfpr.dv.siacoes.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryAppraiserScoreDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserScore;

public class JuryAppraiserScoreBO {
	
	public boolean hasScore(int idJury, int idUser) throws Exception{
		try {
			JuryAppraiserScoreDAO dao = new JuryAppraiserScoreDAO();
			
			return dao.hasScore(idJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryAppraiserScore> listScores(int idJuryAppraiser) throws Exception{
		try {
			JuryAppraiserScoreDAO dao = new JuryAppraiserScoreDAO();
			
			return dao.listScores(idJuryAppraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(JuryAppraiserScore score) throws Exception{
		if((score.getScore() < 0) || (score.getScore() > 10)){
			throw new Exception("A nota deve estar entre 0 e 10.");
		}
		if((score.getEvaluationItem() == null) || (score.getEvaluationItem().getIdEvaluationItem() == 0)){
			throw new Exception("Informe o quesito avaliado.");
		}
		if((score.getJuryAppraiser() == null) || (score.getJuryAppraiser().getIdJuryAppraiser() == 0)){
			throw new Exception("Informe o membro da banca.");
		}
		
		BigDecimal bd = new BigDecimal(score.getScore());
	    bd = bd.setScale(1, RoundingMode.HALF_UP);
	    score.setScore(bd.doubleValue());
		
		try {
			JuryAppraiserScoreDAO dao = new JuryAppraiserScoreDAO();
			
			return dao.save(score);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
