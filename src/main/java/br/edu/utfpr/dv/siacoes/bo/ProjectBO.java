package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.dao.ProjectDAO;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class ProjectBO {
	
	public int findIdProposal(int idProject) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findIdProposal(idProject);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdJury(int idProject) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findIdJury(idProject);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdCampus(int idProject) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findIdCampus(idProject);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Project findByProposal(int idProposal) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findByProposal(idProposal);
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
	
	public List<Project> listBySupervisor(int idSupervisor) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.listBySupervisor(idSupervisor);
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
	
	public Project prepareProject(int idUser, int idDepartment, int semester, int year) throws Exception {
		ProjectBO bo = new ProjectBO();
		Project p = bo.findCurrentProject(idUser, idDepartment, semester, year);
		
		if(p == null){
			DeadlineBO dbo = new DeadlineBO();
			Deadline d = dbo.findBySemester(idDepartment, semester, year);
			
			if((d == null) || DateUtils.getToday().getTime().after(d.getProjectDeadline())){
				throw new Exception("A submissão de projetos já foi encerrada.");
			}
			
			ProposalBO pbo = new ProposalBO();
			Proposal proposal = pbo.findCurrentProposal(idUser, idDepartment, semester, year);
			
			if(proposal == null){
				if(new SigetConfigBO().findByDepartment(idDepartment).isRegisterProposal()){
					throw new Exception("Não foi encontrada a submissão da proposta. É necessário primeiramente submeter a proposta.");
				}else{
					throw new Exception("Não foi encontrada o registro de orientação. É necessário primeiramente registrar a orientação.");
				}
			}
			
			p = new Project(Session.getUser(), proposal);
		}
		
		return p;
	}
	
	public List<byte[]> prepareDocuments(int idUser, int idDepartment, int semester, int year) throws Exception {
		List<byte[]> ret = new ArrayList<byte[]>();
		Project p = this.findCurrentProject(idUser, idDepartment, semester, year);
		
		if(p == null){
			p = this.findLastProject(idUser, idDepartment, semester, year);
		}
		
		if(p == null){
			throw new Exception("É necessário submeter o projeto para imprimir os documentos.");
		}else{
			AttendanceBO abo = new AttendanceBO();
			AttendanceReport attendance = abo.getReport(idUser, p.getProposal().getIdProposal(), p.getSupervisor().getIdUser(), 1);
			
			List<AttendanceReport> list = new ArrayList<AttendanceReport>();
			list.add(attendance);
			
			ret.add(new ReportUtils().createPdfStream(list, "Attendances").toByteArray());
			
			List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
			list2.add(this.getSupervisorFeedbackReport(p));
			
			ret.add(new ReportUtils().createPdfStream(list2, "SupervisorFeedback").toByteArray());
		}
		
		return ret;
	}

}
