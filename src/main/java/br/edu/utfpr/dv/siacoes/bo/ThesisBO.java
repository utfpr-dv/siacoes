package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ThesisDAO;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class ThesisBO {
	
	public int findIdJury(int idThesis) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findIdJury(idThesis);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

	public List<Thesis> listAll() throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.listAll();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Thesis> listBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.listBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Thesis> listBySupervisor(int idSupervisor) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.listBySupervisor(idSupervisor);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void validate(Thesis thesis) throws Exception{
		if((thesis.getProject() == null) || (thesis.getProject().getIdProject() == 0)){
			throw new Exception("Selecione o projeto relacionado à monografia.");
		}
		if(thesis.getTitle().isEmpty()){
			throw new Exception("Informe o título da monografia.");
		}
		if(thesis.getSubarea().isEmpty()){
			throw new Exception("Informe a área e subárea da monografia.");
		}
		if(thesis.getFile() == null){
			throw new Exception("É necessário enviar o arquivo da monografia.");
		}
		if(thesis.getFileType() == DocumentType.UNDEFINED){
			throw new Exception("O arquivo enviado não está no formato correto. Envie um arquivo PDF, DOC ou DOCX");
		}
		if((thesis.getStudent() == null) || (thesis.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		if((thesis.getSupervisor() == null) || (thesis.getSupervisor().getIdUser() == 0)){
			throw new Exception("Informe o Professor Orientador.");
		}
		if((thesis.getSemester() == 0) || (thesis.getYear() == 0)){
			throw new Exception("Informe o ano e semestre da monografia.");
		}
		if(thesis.getAbstract().isEmpty()){
			throw new Exception("Informe o resumo da monografia.");
		}
	}
	
	public int save(Thesis thesis) throws Exception{
		try {
			this.validate(thesis);
			
			ThesisDAO dao = new ThesisDAO();
			
			return dao.save(thesis);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Thesis findById(int id) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Thesis findByProposal(int idProposal) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findByProposal(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Thesis findCurrentThesis(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findCurrentThesis(idStudent, idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Thesis findApprovedThesis(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findApprovedThesis(idStudent, idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Thesis findLastThesis(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findLastThesis(idStudent, idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public SupervisorFeedbackReport getSupervisorFeedbackReport(int idThesis) throws Exception{
		return this.getSupervisorFeedbackReport(this.findById(idThesis));
	}
	
	public SupervisorFeedbackReport getSupervisorFeedbackReport(Thesis thesis){
		SupervisorFeedbackReport feedback = new SupervisorFeedbackReport();
		
		feedback.setStage(2);
		feedback.setTitle(thesis.getTitle());
		feedback.setStudent(thesis.getStudent().getName());
		feedback.setSupervisor(thesis.getSupervisor().getName());
		
		return feedback;
	}
	
}
