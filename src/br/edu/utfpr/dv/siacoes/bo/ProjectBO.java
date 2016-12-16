package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ProjectDAO;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class ProjectBO {
	
	public int findIdJury(int idProject) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findIdJury(idProject);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Project> listAll() throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.listAll();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Project> listBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.listBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void validate(Project project) throws Exception{
		if((project.getProposal() == null) || (project.getProposal().getIdProposal() == 0)){
			throw new Exception("Selecione a proposta relacionada ao projeto.");
		}
		if(project.getTitle().isEmpty()){
			throw new Exception("Informe o título do projeto.");
		}
		if(project.getSubarea().isEmpty()){
			throw new Exception("Informe a área e subárea do projeto.");
		}
		if(project.getFile() == null){
			throw new Exception("É necessário enviar o arquivo do projeto.");
		}
		if(project.getFileType() == DocumentType.UNDEFINED){
			throw new Exception("O arquivo enviado não está no formato correto. Envie um arquivo PDF, DOC ou DOCX");
		}
		if((project.getStudent() == null) || (project.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		if((project.getSupervisor() == null) || (project.getSupervisor().getIdUser() == 0)){
			throw new Exception("Informe o Professor Orientador.");
		}
		if((project.getSemester() == 0) || (project.getYear() == 0)){
			throw new Exception("Informe o ano e semestre do projeto.");
		}
		if(project.getAbstract().isEmpty()){
			throw new Exception("Informe o resumo do projeto.");
		}
	}
	
	public int save(Project project) throws Exception{
		try {
			this.validate(project);
			
			ProjectDAO dao = new ProjectDAO();
			
			return dao.save(project);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Project findById(int id) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Project findCurrentProject(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findCurrentProject(idStudent, idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Project findApprovedProject(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findApprovedProject(idStudent, idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Project findLastProject(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findLastProject(idStudent, idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public SupervisorFeedbackReport getSupervisorFeedbackReport(int idProject) throws Exception{
		return this.getSupervisorFeedbackReport(this.findById(idProject));
	}
	
	public SupervisorFeedbackReport getSupervisorFeedbackReport(Project project){
		SupervisorFeedbackReport feedback = new SupervisorFeedbackReport();
		
		feedback.setStage(1);
		feedback.setTitle(project.getTitle());
		feedback.setStudent(project.getStudent().getName());
		feedback.setSupervisor(project.getSupervisor().getName());
		
		return feedback;
	}

}
