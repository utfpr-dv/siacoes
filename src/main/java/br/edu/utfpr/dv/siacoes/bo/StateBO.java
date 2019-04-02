package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.StateDAO;
import br.edu.utfpr.dv.siacoes.model.State;

public class StateBO {
	
	public List<State> listAll() throws Exception{
		try{
			StateDAO dao = new StateDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<State> listByCountry(int idCountry) throws Exception{
		try{
			StateDAO dao = new StateDAO();
			
			return dao.listByCountry(idCountry);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public State findById(int id) throws Exception{
		try{
			StateDAO dao = new StateDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, State state) throws Exception{
		if((state.getCountry() == null) || (state.getCountry().getIdCountry() == 0)){
			throw new Exception("Informe o país do estado.");
		}
		if(state.getName().isEmpty()){
			throw new Exception("Informe o nome do estado.");
		}
		if(state.getInitials().isEmpty()){
			throw new Exception("Informe a sigla do estado.");
		}
		
		try{
			StateDAO dao = new StateDAO();
			
			return dao.save(idUser, state);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}

}
