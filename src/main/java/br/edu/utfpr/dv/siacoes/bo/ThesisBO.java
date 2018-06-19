package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ThesisDAO;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class ThesisBO {
	
	public int findIdProject(int idThesis) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findIdProject(idThesis);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdJury(int idThesis) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findIdJury(idThesis);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idThesis) throws Exception{
		try {
			ThesisDAO dao = new ThesisDAO();
			
			return dao.findIdDepartment(idThesis);
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
			boolean isInsert = (thesis.getIdThesis() == 0);
			byte[] oldFile = null;
			this.validate(thesis);
			
			ThesisDAO dao = new ThesisDAO();
			
			if(!isInsert) {
				oldFile = dao.getFile(thesis.getIdThesis());
			}
			
			int ret = dao.save(thesis);
			
			try {
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				keys.add(new EmailMessageEntry<String, String>("documenttype", "Monografia de TCC 2"));
				keys.add(new EmailMessageEntry<String, String>("student", thesis.getStudent().getName()));
				keys.add(new EmailMessageEntry<String, String>("title", thesis.getTitle()));
				keys.add(new EmailMessageEntry<String, String>("supervisor", thesis.getSupervisor().getName()));
				
				if(isInsert) {
					bo.sendEmail(thesis.getStudent().getIdUser(), MessageType.PROJECTORTHESISSUBMITEDSTUDENT, keys);
					bo.sendEmail(thesis.getSupervisor().getIdUser(), MessageType.PROJECTORTHESISSUBMITEDSUPERVISOR, keys);
				} else if (!Arrays.equals(thesis.getFile(), oldFile)) {
					bo.sendEmail(thesis.getStudent().getIdUser(), MessageType.PROJECTORTHESISCHANGEDSTUDENT, keys);
					bo.sendEmail(thesis.getSupervisor().getIdUser(), MessageType.PROJECTORTHESISCHANGEDSUPERVISOR, keys);
				}
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
			
			return ret;
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
			
			SupervisorChangeBO sbo = new SupervisorChangeBO();
			thesis.setSupervisor(sbo.findCurrentSupervisor(project.getProposal().getIdProposal()));
			thesis.setCosupervisor(sbo.findCurrentCosupervisor(project.getProposal().getIdProposal()));
			
			SigetConfig config = new SigetConfigBO().findByDepartment(idDepartment);
			if(config.isValidateAttendances()) {
				if(!new AttendanceBO().validateFrequency(idUser, thesis.getSupervisor().getIdUser(), project.getProposal().getIdProposal(), 2, config.getAttendanceFrequency())) {
					throw new Exception("As reuniões de orientação registradas não atendem à frequência mínima exigida. As reuniões com o orientador devem ocorrer com frequência " + config.getAttendanceFrequency().toString() + ".");
				}
			}
		}
		
		return thesis;
	}
	
	public List<byte[]> prepareDocuments(int idUser, int idDepartment, int semester, int year) throws Exception {
		List<byte[]> ret = new ArrayList<byte[]>();
		Thesis thesis = this.findCurrentThesis(idUser, idDepartment, semester, year);
		int idProposal = 0, idSupervisor = 0;
		
		if(thesis == null){
			thesis = this.findLastThesis(idUser, idDepartment, semester, year);
			
			if(thesis == null) {
				Project project = new ProjectBO().findLastProject(idUser, idDepartment, semester, year);
				
				if(project == null) {
					throw new Exception("Não foi encontrada nenhuma submissão de TCC 1.");
				} else {
					idProposal = project.getProposal().getIdProposal();
					idSupervisor = new SupervisorChangeBO().findCurrentSupervisor(idProposal).getIdUser();
				}
			}
		}
		
		if(thesis != null) {
			idProposal = new ThesisDAO().findIdProposal(thesis.getIdThesis());
			idSupervisor = thesis.getSupervisor().getIdUser();
		}
		
		AttendanceBO abo = new AttendanceBO();
		AttendanceReport attendance = abo.getReport(idUser, idProposal, idSupervisor, 2);
		
		List<AttendanceReport> list = new ArrayList<AttendanceReport>();
		list.add(attendance);
		
		ret.add(new ReportUtils().createPdfStream(list, "Attendances", idDepartment).toByteArray());
		
		if(new SigetConfigBO().findByDepartment(idDepartment).isSupervisorJuryAgreement()) {
			if(thesis == null) {
				thesis = new Thesis();
				thesis.setTitle(attendance.getTitle());
				thesis.getStudent().setIdUser(idUser);
				thesis.getStudent().setName(attendance.getStudent());
				thesis.getSupervisor().setIdUser(idSupervisor);
				thesis.getSupervisor().setName(attendance.getSupervisor());
			}
			
			List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
			list2.add(this.getSupervisorFeedbackReport(thesis));
			
			ret.add(new ReportUtils().createPdfStream(list2, "SupervisorFeedback", idDepartment).toByteArray());
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
