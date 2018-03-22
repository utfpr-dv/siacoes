package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ThesisDAO;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

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
	
	public Thesis prepareThesis(int idUser, int idDepartment, int semester, int year) throws Exception {
		Thesis thesis = this.findCurrentThesis(idUser, idDepartment, semester, year);
		
		if(thesis == null){
			DeadlineBO dbo = new DeadlineBO();
			Deadline d = dbo.findBySemester(idDepartment, semester, year);
			
			if((d == null) || DateUtils.getToday().getTime().after(d.getThesisDeadline())){
				throw new Exception("A submissão de monografias já foi encerrada.");
			}
			
			ProjectBO pbo = new ProjectBO();
			Project project = pbo.findApprovedProject(idUser, idDepartment, semester, year);
			
			if(project == null){
				throw new Exception("Não foi encontrada a submissão do projeto. É necessário submeter primeiramente o projeto.");
			}
			
			thesis = new Thesis(new UserBO().findById(idUser), project);
		}
		
		return thesis;
	}
	
	public List<byte[]> prepareDocuments(int idUser, int idDepartment, int semester, int year) throws Exception {
		List<byte[]> ret = new ArrayList<byte[]>();
		Thesis thesis = this.findCurrentThesis(idUser, idDepartment, semester, year);
		
		if(thesis == null){
			thesis = this.findLastThesis(idUser, idDepartment, semester, year);
		}
		
		if(thesis == null){
			throw new Exception("É necessário submeter a monografia para imprimir os documentos.");
		}else{
			ProjectBO pbo = new ProjectBO();
			Project project = pbo.findById(thesis.getProject().getIdProject());
			
			AttendanceBO abo = new AttendanceBO();
			AttendanceReport attendance = abo.getReport(idUser, project.getProposal().getIdProposal(), thesis.getSupervisor().getIdUser(), 2);
			
			List<AttendanceReport> list = new ArrayList<AttendanceReport>();
			list.add(attendance);
			
			ret.add(new ReportUtils().createPdfStream(list, "Attendances").toByteArray());
			
			List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
			list2.add(this.getSupervisorFeedbackReport(thesis));
			
			ret.add(new ReportUtils().createPdfStream(list2, "SupervisorFeedback").toByteArray());
		}
		
		return ret;
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
