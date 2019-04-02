package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ActivityGroupDAO;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;

public class ActivityGroupBO {
	
	public List<ActivityGroup> listAll() throws Exception{
		try{
			ActivityGroupDAO dao = new ActivityGroupDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public ActivityGroup findById(int id) throws Exception{
		try{
			ActivityGroupDAO dao = new ActivityGroupDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public ActivityGroup findByActivity(int idActivity) throws Exception{
		try{
			ActivityGroupDAO dao = new ActivityGroupDAO();
			
			return dao.findByActivity(idActivity);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, ActivityGroup group) throws Exception{
		if(group.getDescription().isEmpty()){
			throw new Exception("Informe a descrição do grupo.");
		}
		if(group.getMinimumScore() <= 0){
			throw new Exception("Informe a pontuação mínima do grupo.");
		}
		if(group.getMaximumScore() <= 0){
			throw new Exception("Informe a pontuação máxima do grupo.");
		}
		
		try{
			ActivityGroupDAO dao = new ActivityGroupDAO();
			
			return dao.save(idUser, group);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public void moveUp(int idActivityGroup) throws Exception{
		try {
			ActivityGroupDAO dao = new ActivityGroupDAO();
			
			dao.moveUp(idActivityGroup);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveUp(ActivityGroup group) throws Exception{
		this.moveUp(group.getIdActivityGroup());
	}
	
	public void moveDown(int idActivityGroup) throws Exception{
		try {
			ActivityGroupDAO dao = new ActivityGroupDAO();
			
			dao.moveDown(idActivityGroup);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveDown(ActivityGroup group) throws Exception{
		this.moveDown(group.getIdActivityGroup());
	}

}
