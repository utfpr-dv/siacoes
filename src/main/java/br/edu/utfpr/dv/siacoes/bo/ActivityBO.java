package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ActivityDAO;
import br.edu.utfpr.dv.siacoes.model.Activity;

public class ActivityBO {
	
	public boolean needsFillAmount(int idActivity) throws Exception{
		try{
			ActivityDAO dao = new ActivityDAO();
			
			return dao.needsFillAmount(idActivity);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Activity> listAll() throws Exception{
		try{
			ActivityDAO dao = new ActivityDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Activity> listByDepartment(int idDepartment) throws Exception{
		try{
			ActivityDAO dao = new ActivityDAO();
			
			return dao.listByDepartment(idDepartment);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<Activity> listByGroup(int idDepartment, int idGroup) throws Exception{
		try{
			ActivityDAO dao = new ActivityDAO();
			
			return dao.listByGroup(idDepartment, idGroup);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public Activity findById(int id) throws Exception{
		try{
			ActivityDAO dao = new ActivityDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, Activity activity) throws Exception{
		if((activity.getDepartment() == null) || (activity.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento/coordenação.");
		}
		if(activity.getDescription().isEmpty()){
			throw new Exception("Informe a descrição da atividade.");
		}
		if((activity.getGroup() == null) || (activity.getGroup().getIdActivityGroup() == 0)){
			throw new Exception("Informe o grupo da atividade.");
		}
		if((activity.getUnit() == null) || (activity.getUnit().getIdActivityUnit() == 0)){
			throw new Exception("Informe a unidade de pontuação da atividade.");
		}
		if(activity.getScore() <= 0){
			throw new Exception("Informe a pontuação da atividade");
		}
		
		try{
			ActivityDAO dao = new ActivityDAO();
			
			return dao.save(idUser, activity);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public void moveUp(int idActivity) throws Exception{
		try {
			ActivityDAO dao = new ActivityDAO();
			
			dao.moveUp(idActivity);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveUp(Activity activity) throws Exception{
		this.moveUp(activity.getIdActivity());
	}
	
	public void moveDown(int idActivity) throws Exception{
		try {
			ActivityDAO dao = new ActivityDAO();
			
			dao.moveDown(idActivity);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveDown(Activity activity) throws Exception{
		this.moveDown(activity.getIdActivity());
	}

}
