package br.edu.utfpr.dv.siacoes.bo;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
	
	public int save(int idUser, Document document) throws Exception{
		try {
			if(document.getName().isEmpty()){
				throw new Exception("Informe o nome do documento.");
			}
			if(document.getFile() == null){
				throw new Exception("É necessário enviar o documento.");
			}
			
			DocumentDAO dao = new DocumentDAO();
			
			return dao.save(idUser, document);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, int id) throws Exception{
		try {
			DocumentDAO dao = new DocumentDAO();
			
			return dao.delete(idUser, id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean delete(int idUser, Document document) throws Exception{
		return this.delete(idUser, document.getIdDocument());
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
	
	public byte[] downloadAllDocuments(int idDepartment, SystemModule module) throws Exception{
		List<Document> list = this.listByModule(idDepartment, module);
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(ret);
		
		for(Document doc : list){
			ZipEntry e = new ZipEntry(doc.getName() + doc.getType().getExtension());
			out.putNextEntry(e);
			
			out.write(doc.getFile(), 0, doc.getFile().length);
			out.closeEntry();
		}
		
		out.flush();
		out.close();
		
		return ret.toByteArray();
	}
	
}
