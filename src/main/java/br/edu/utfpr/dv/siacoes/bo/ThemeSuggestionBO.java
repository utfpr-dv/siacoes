package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ThemeSuggestionDAO;
import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;

public class ThemeSuggestionBO {
	
	public ThemeSuggestion findById(int id) throws Exception{
		try{
			ThemeSuggestionDAO dao = new ThemeSuggestionDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<ThemeSuggestion> listAll(boolean onlyActives) throws Exception{
		try{
			ThemeSuggestionDAO dao = new ThemeSuggestionDAO();
			
			return dao.listAll(onlyActives);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<ThemeSuggestion> listByDepartment(int idDepartment, boolean onlyActives) throws Exception{
		try{
			ThemeSuggestionDAO dao = new ThemeSuggestionDAO();
			
			return dao.listByDepartment(idDepartment, onlyActives);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, ThemeSuggestion theme) throws Exception{
		if((theme.getDepartment() == null) || (theme.getDepartment().getIdDepartment() == 0)){
			throw new Exception("Informe o departamento.");
		}
		if(theme.getTitle().isEmpty()){
			throw new Exception("Informe o título.");
		}
		if(theme.getProponent().isEmpty()){
			throw new Exception("Informe o proponente.");
		}
		if(theme.getObjectives().isEmpty()){
			throw new Exception("Informe os objetivos.");
		}
		if(theme.getProposal().isEmpty()){
			throw new Exception("Descreva a proposta.");
		}
		
		try{
			ThemeSuggestionDAO dao = new ThemeSuggestionDAO();
			
			return dao.save(idUser, theme);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, int id) throws Exception{
		try{
			ThemeSuggestionDAO dao = new ThemeSuggestionDAO();
			
			return dao.delete(idUser, id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, ThemeSuggestion theme) throws Exception{
		return this.delete(idUser, theme.getIdThemeSuggestion());
	}

}
