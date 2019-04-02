package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.CityDAO;
import br.edu.utfpr.dv.siacoes.model.City;

public class CityBO {
	
	public List<City> listAll() throws Exception{
		try{
			CityDAO dao = new CityDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<City> listByState(int idState) throws Exception{
		try{
			CityDAO dao = new CityDAO();
			
			return dao.listByState(idState);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public City findById(int id) throws Exception{
		try{
			CityDAO dao = new CityDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, City city) throws Exception{
		if((city.getState() == null) || (city.getState().getIdState() == 0)){
			throw new Exception("Informe o estado da cidade.");
		}
		if(city.getName().isEmpty()){
			throw new Exception("Informe o nome da cidade.");
		}
		
		try{
			CityDAO dao = new CityDAO();
			
			return dao.save(idUser, city);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}

}
