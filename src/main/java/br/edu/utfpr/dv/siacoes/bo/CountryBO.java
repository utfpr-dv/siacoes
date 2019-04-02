package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.CountryDAO;
import br.edu.utfpr.dv.siacoes.model.Country;

public class CountryBO {
	
	public List<Country> listAll() throws Exception{
		try{
			CountryDAO dao = new CountryDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public Country findById(int id) throws Exception{
		try{
			CountryDAO dao = new CountryDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, Country country) throws Exception{
		if(country.getName().isEmpty()){
			throw new Exception("Informe o nome do país.");
		}
		
		try{
			CountryDAO dao = new CountryDAO();
			
			return dao.save(idUser, country);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}

}
