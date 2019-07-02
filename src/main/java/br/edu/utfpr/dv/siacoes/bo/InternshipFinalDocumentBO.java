package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipFinalDocumentDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.StringUtils;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;

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
	
	public int save(int idUser, InternshipFinalDocument doc) throws Exception{
		if((doc.getInternship() == null) || (doc.getInternship().getIdInternship() == 0)){
			throw new Exception("Informe o estágio.");
		}
		if(doc.getTitle().isEmpty()){
			throw new Exception("Informe o título do relatório de estágio.");
		}
		if(doc.getFile() == null){
			throw new Exception("É necessário enviar o arquivo.");
		}
		
		Internship internship = new InternshipBO().findById(doc.getInternship().getIdInternship());
		if(internship.getType() != InternshipType.REQUIRED){
			throw new Exception("O envio do relatório corrigido só é permitido para o estágio obrigatório.");
		}
		
		SigesConfig config = new SigesConfigBO().findByDepartment(internship.getDepartment().getIdDepartment());
		if((config.getMaxFileSize() > 0) && (doc.getFile() != null) && ((doc.getIdInternshipFinalDocument() == 0) || !Arrays.equals(doc.getFile(), new InternshipFinalDocumentDAO().getFile(doc.getIdInternshipFinalDocument()))) && (doc.getFile().length > config.getMaxFileSize())) {
			throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
		}
		
		try{
			boolean isInsert = (doc.getIdInternshipFinalDocument() == 0);
			DocumentFeedback feedback = DocumentFeedback.NONE;
			InternshipFinalDocumentDAO dao = new InternshipFinalDocumentDAO();
			
			if(!isInsert) {
				feedback = dao.findFeedback(doc.getIdInternshipFinalDocument());
			}
			
			int ret = dao.save(idUser, doc);
			
			try {
				EmailMessageBO ebo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				keys.add(new EmailMessageEntry<String, String>("student", internship.getStudent().getName()));
				keys.add(new EmailMessageEntry<String, String>("company", internship.getCompany().getName()));
				keys.add(new EmailMessageEntry<String, String>("supervisor", internship.getSupervisor().getName()));
				keys.add(new EmailMessageEntry<String, String>("feedback", doc.getSupervisorFeedback().toString()));
				
				if(isInsert) {
					ebo.sendEmail(internship.getSupervisor().getIdUser(), MessageType.INTERNSHIPFINALDOCUMENTSUBMITTED, keys);
					ebo.sendEmail(internship.getStudent().getIdUser(), MessageType.INTERNSHIPFINALDOCUMENTSUBMITTED, keys);
				} else if((doc.getSupervisorFeedback() != DocumentFeedback.NONE) && (feedback != doc.getSupervisorFeedback())) {
					User user = new UserBO().findManager(new InternshipBO().findIdDepartment(doc.getInternship().getIdInternship()), SystemModule.SIGES);
					
					keys.add(new EmailMessageEntry<String, String>("manager", user.getName()));
					
					ebo.sendEmail(user.getIdUser(), MessageType.INTERNSHIPFINALDOCUMENTVALIDATED, keys);
					ebo.sendEmail(internship.getStudent().getIdUser(), MessageType.INTERNSHIPFINALDOCUMENTVALIDATEDSTUDENT, keys);
				}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
			
			return ret;
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
