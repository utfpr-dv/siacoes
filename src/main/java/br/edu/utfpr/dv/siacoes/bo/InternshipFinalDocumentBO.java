package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipFinalDocumentDAO;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;

public class InternshipFinalDocumentBO {
	
	public InternshipFinalDocument findById(int id) throws Exception{
		try{
			InternshipFinalDocumentDAO dao = new InternshipFinalDocumentDAO();
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public InternshipFinalDocument findByInternship(int idInternship) throws Exception{
		try{
			InternshipFinalDocumentDAO dao = new InternshipFinalDocumentDAO();
			
			return dao.findByInternship(idInternship);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipFinalDocument> listAll() throws Exception{
		try{
			InternshipFinalDocumentDAO dao = new InternshipFinalDocumentDAO();
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipFinalDocument> listBySemester(int idDepartment, int semester, int year, boolean includePrivate) throws Exception{
		try{
			InternshipFinalDocumentDAO dao = new InternshipFinalDocumentDAO();
			
			return dao.listBySemester(idDepartment, semester, year, includePrivate);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<InternshipFinalDocument> listByDepartment(int idDepartment, boolean includePrivate) throws Exception{
		try{
			InternshipFinalDocumentDAO dao = new InternshipFinalDocumentDAO();
			
			return dao.listByDepartment(idDepartment, includePrivate);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(InternshipFinalDocument doc) throws Exception{
		if((doc.getInternship() == null) || (doc.getInternship().getIdInternship() == 0)){
			throw new Exception("Informe o estágio.");
		}
		if(doc.getTitle().isEmpty()){
			throw new Exception("Informe o título do relatório de estágio.");
		}
		if(doc.getFile() == null){
			throw new Exception("É necessário enviar o arquivo.");
		}
		
		InternshipBO bo = new InternshipBO();
		Internship internship = bo.findById(doc.getInternship().getIdInternship());
		if(internship.getType() != InternshipType.REQUIRED){
			throw new Exception("O envio do relatório corrigido só é permitido para o estágio obrigatório.");
		}
		
		try{
			InternshipFinalDocumentDAO dao = new InternshipFinalDocumentDAO();
			
			return dao.save(doc);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
