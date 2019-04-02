package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipEvaluationItemDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipEvaluationItem;

public class InternshipEvaluationItemBO {

	public InternshipEvaluationItem findById(int id) throws Exception{
		try {
			InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean hasScores(int idEvaluationItem){
		InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
		
		try {
			return dao.hasScores(idEvaluationItem);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			return true;
		}
	}
	
	public List<InternshipEvaluationItem> listAll(boolean onlyActives) throws Exception{
		try {
			InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
			
			return dao.listAll(onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipEvaluationItem> listByDepartment(int idDepartment, boolean onlyActives) throws Exception{
		try {
			InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
			
			return dao.listByDepartment(idDepartment, onlyActives);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
		
	public int save(int idUser, InternshipEvaluationItem item) throws Exception{
		try {
			if(item.getDescription().isEmpty()){
				throw new Exception("Informe a descrição do quesito.");
			}
			if(item.getPonderosity() <= 0){
				throw new Exception("O peso do quesito deve ser maior que zero.");
			}
			
			InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
			
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
			InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
			
			return dao.delete(idUser, id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, InternshipEvaluationItem item) throws Exception{
		return this.delete(idUser, item.getIdInternshipEvaluationItem());
	}
	
	public void moveUp(int idEvaluationItem) throws Exception{
		try {
			InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
			
			dao.moveUp(idEvaluationItem);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveUp(InternshipEvaluationItem item) throws Exception{
		this.moveUp(item.getIdInternshipEvaluationItem());
	}
	
	public void moveDown(int idEvaluationItem) throws Exception{
		try {
			InternshipEvaluationItemDAO dao = new InternshipEvaluationItemDAO();
			
			dao.moveDown(idEvaluationItem);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveDown(InternshipEvaluationItem item) throws Exception{
		this.moveDown(item.getIdInternshipEvaluationItem());
	}
	
}
