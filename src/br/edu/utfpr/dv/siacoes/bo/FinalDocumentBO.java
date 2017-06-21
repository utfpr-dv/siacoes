package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.FinalDocumentDAO;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;

public class FinalDocumentBO {
	
	public FinalDocument findById(int id) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public FinalDocument findCurrentThesis(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findCurrentThesis(idStudent, idDepartment, semester, year);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public FinalDocument findCurrentProject(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.findCurrentProject(idStudent, idDepartment, semester, year);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listAll() throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listBySemester(int idDepartment, int semester, int year, boolean includePrivate) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listBySemester(idDepartment, semester, year, includePrivate);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listBySupervisor(int idSupervisor, int semester, int year) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listBySupervisor(idSupervisor, semester, year);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<FinalDocument> listByDepartment(int idDepartment, boolean includePrivate) throws Exception{
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.listByDepartment(idDepartment, includePrivate);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(FinalDocument thesis) throws Exception{
		if(((thesis.getThesis() == null) || (thesis.getThesis().getIdThesis() == 0)) && ((thesis.getProject() == null) || (thesis.getProject().getIdProject() == 0))){
			throw new Exception("Informe o projeto ou monografia.");
		}
		if(thesis.getTitle().isEmpty()){
			throw new Exception("Informe o título do projeto/monografia.");
		}
		if(thesis.getFile() == null){
			throw new Exception("É necessário enviar o arquivo.");
		}
		
		try{
			FinalDocumentDAO dao = new FinalDocumentDAO();
			
			return dao.save(thesis);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
