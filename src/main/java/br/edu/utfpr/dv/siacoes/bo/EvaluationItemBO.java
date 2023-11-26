package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.EvaluationItemDAO;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;

public class EvaluationItemBO {
	
	public EvaluationItem findById(int id) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean hasScores(int idEvaluationItem){
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			return dao.hasScores(idEvaluationItem);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			return true;
		}
	}
	
	public List<EvaluationItem> listByFormat(int idFormat, boolean onlyActives) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			return dao.listByFormat(idFormat, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<EvaluationItem> listByStage(int stage, int idFormat, boolean onlyActives) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			if(stage == 0){
				return dao.listByFormat(idFormat, onlyActives);
			}else{
				return dao.listByStage(stage, idFormat, onlyActives);
			}
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, EvaluationItem item) throws Exception{
		try {
			if(item.getFormat().getIdThesisFormat() == 0) {
				throw new Exception("Informe o formato de TCC do quesito.");
			}
			if(item.getDescription().isEmpty()){
				throw new Exception("Informe a descrição do quesito.");
			}
			if(item.getPonderosity() <= 0){
				throw new Exception("O peso do quesito deve ser maior que zero.");
			}
			
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			return dao.save(idUser, item);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, int id) throws Exception{
		if(this.hasScores(id)){
			throw new Exception("Este quesito já tem notas lançadas e não pode ser excluído.");
		}
		
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			return dao.delete(idUser, id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, EvaluationItem item) throws Exception{
		return this.delete(idUser, item.getIdEvaluationItem());
	}

}
