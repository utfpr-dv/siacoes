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
		EvaluationItemDAO dao = new EvaluationItemDAO();
		
		try {
			return dao.hasScores(idEvaluationItem);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			return true;
		}
	}
	
	public List<EvaluationItem> listAll(boolean onlyActives) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			return dao.listAll(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<EvaluationItem> listByDepartment(int idDepartment, boolean onlyActives) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			return dao.listByDepartment(idDepartment, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<EvaluationItem> listByStage(int stage, int idDepartment, boolean onlyActives) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			if(stage == 0){
				return dao.listByDepartment(idDepartment, onlyActives);
			}else{
				return dao.listByStage(stage, idDepartment, onlyActives);
			}
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, EvaluationItem item) throws Exception{
		try {
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
	
	public void moveUp(int idEvaluationItem) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			dao.moveUp(idEvaluationItem);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveUp(EvaluationItem item) throws Exception{
		this.moveUp(item.getIdEvaluationItem());
	}
	
	public void moveDown(int idEvaluationItem) throws Exception{
		try {
			EvaluationItemDAO dao = new EvaluationItemDAO();
			
			dao.moveDown(idEvaluationItem);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveDown(EvaluationItem item) throws Exception{
		this.moveDown(item.getIdEvaluationItem());
	}

}
