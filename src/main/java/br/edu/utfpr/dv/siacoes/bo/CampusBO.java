package br.edu.utfpr.dv.siacoes.bo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.CampusDAO;
import br.edu.utfpr.dv.siacoes.model.Campus;

public class CampusBO {
	
	public Campus findById(int id) throws Exception{
		try{
			CampusDAO dao = new CampusDAO();
			
			return dao.findById(id);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Campus findByDepartment(int idDepartment) throws Exception{
		try{
			CampusDAO dao = new CampusDAO();
			
			return dao.findByDepartment(idDepartment);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Campus> listAll(boolean onlyActive) throws Exception{
		try{
			CampusDAO dao = new CampusDAO();
			
			return dao.listAll(onlyActive);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, Campus campus) throws Exception{
		if(campus.getName().isEmpty()){
			throw new Exception("Informe o nome do câmpus.");
		}
		
		try{
			CampusDAO dao = new CampusDAO();
			
			return dao.save(idUser, campus);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
