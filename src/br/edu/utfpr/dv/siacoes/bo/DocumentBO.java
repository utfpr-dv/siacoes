package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.DocumentDAO;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class DocumentBO {

	public List<Document> listAll() throws Exception{
		DocumentDAO dao = new DocumentDAO();
		
		try {
			return dao.listAll();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Document> listByModule(int idDepartment, SystemModule module) throws Exception{
		DocumentDAO dao = new DocumentDAO();
		
		try {
			return dao.listByModule(idDepartment, module);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Document findById(int id) throws Exception{
		DocumentDAO dao = new DocumentDAO();
		
		try {
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(Document document) throws Exception{
		try {
			if(document.getName().isEmpty()){
				throw new Exception("Informe o nome do documento.");
			}
			if(document.getFile() == null){
				throw new Exception("É necessário enviar o documento.");
			}
			
			DocumentDAO dao = new DocumentDAO();
			
			return dao.save(document);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int id) throws Exception{
		try {
			DocumentDAO dao = new DocumentDAO();
			
			return dao.delete(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(Document document) throws Exception{
		return this.delete(document.getIdDocument());
	}
	
	public void moveUp(int idDocument) throws Exception{
		try {
			DocumentDAO dao = new DocumentDAO();
			
			dao.moveUp(idDocument);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveUp(Document document) throws Exception{
		this.moveUp(document.getIdDocument());
	}
	
	public void moveDown(int idDocument) throws Exception{
		try {
			DocumentDAO dao = new DocumentDAO();
			
			dao.moveDown(idDocument);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void moveDown(Document document) throws Exception{
		this.moveDown(document.getIdDocument());
	}
	
}
