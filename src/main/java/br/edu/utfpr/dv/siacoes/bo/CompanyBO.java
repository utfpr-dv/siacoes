package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.CompanyDAO;
import br.edu.utfpr.dv.siacoes.model.Company;

public class CompanyBO {
	
	public List<Company> listAll() throws Exception{
		try{
			CompanyDAO dao = new CompanyDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public Company findById(int id) throws Exception{
		try{
			CompanyDAO dao = new CompanyDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, Company company) throws Exception{
		if((company.getCity() == null) || (company.getCity().getIdCity() == 0)){
			throw new Exception("Informe a cidade da empresa.");
		}
		if(company.getName().isEmpty()){
			throw new Exception("Informe o nome da empresa.");
		}
		
		try{
			CompanyDAO dao = new CompanyDAO();
			
			return dao.save(idUser, company);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getActiveCompanies() throws Exception{
		try {
			CompanyDAO dao = new CompanyDAO();
			
			return dao.getActiveCompanies();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public long getActiveCompaniesWithAgreement() throws Exception{
		try {
			CompanyDAO dao = new CompanyDAO();
			
			return dao.getActiveCompaniesWithAgreement();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
