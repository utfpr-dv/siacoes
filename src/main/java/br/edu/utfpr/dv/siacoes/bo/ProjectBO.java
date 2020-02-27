package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.dao.ProjectDAO;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SupervisorFeedbackReport;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

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
	
	public int findIdDepartment(int idProject) throws Exception{
		try {
			ProjectDAO dao = new ProjectDAO();
			
			return dao.findIdDepartment(idProject);
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
		if((project.getStudent() == null) || (project.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		if((project.getSupervisor() == null) || (project.getSupervisor().getIdUser() == 0)){
			throw new Exception("Informe o Professor Orientador.");
		}
		if((project.getSemester() == 0) || (project.getYear() == 0)){
			throw new Exception("Informe o ano e semestre do projeto.");
		}
		SigetConfig config = new SigetConfigBO().findByDepartment(new ProposalBO().findIdDepartment(project.getProposal().getIdProposal()));
		if((config.getMaxFileSize() > 0) && ((project.getIdProject() == 0) || !Arrays.equals(project.getFile(), new ProjectDAO().getFile(project.getIdProject()))) && (project.getFile().length > config.getMaxFileSize())) {
			throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
		}
	}
	
	public int save(int idUser, Project project) throws Exception{
		try {
			boolean isInsert = (project.getIdProject() == 0);
			byte[] oldFile = null;
			this.validate(project);
			
			ProjectDAO dao = new ProjectDAO();
			
			if(!isInsert) {
				oldFile = dao.getFile(project.getIdProject());
			}
			
			int ret = dao.save(idUser, project);
			
			try {
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				
				keys.add(new EmailMessageEntry<String, String>("documenttype", "Projeto de TCC 1"));
				keys.add(new EmailMessageEntry<String, String>("student", project.getStudent().getName()));
				keys.add(new EmailMessageEntry<String, String>("title", project.getTitle()));
				keys.add(new EmailMessageEntry<String, String>("supervisor", project.getSupervisor().getName()));
				
				if(isInsert) {
					bo.sendEmail(project.getStudent().getIdUser(), MessageType.PROJECTORTHESISSUBMITEDSTUDENT, keys);
					bo.sendEmail(project.getSupervisor().getIdUser(), MessageType.PROJECTORTHESISSUBMITEDSUPERVISOR, keys);
				} else if (!Arrays.equals(project.getFile(), oldFile)) {
					bo.sendEmail(project.getStudent().getIdUser(), MessageType.PROJECTORTHESISCHANGEDSTUDENT, keys);
					bo.sendEmail(project.getSupervisor().getIdUser(), MessageType.PROJECTORTHESISCHANGEDSUPERVISOR, keys);
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
			SigetConfig config = new SigetConfigBO().findByDepartment(idDepartment);
			
			return dao.findApprovedProject(idStudent, idDepartment, semester, year, config.isRequestFinalDocumentStage1());
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
		Project p = new ProjectBO().findCurrentProject(idUser, idDepartment, semester, year);
		
		if(p == null) {
			Deadline d = new DeadlineBO().findBySemester(idDepartment, semester, year);
			
			if((d == null) || DateUtils.getToday().getTime().after(d.getProjectDeadline())) {
				throw new Exception("A submissão de projetos já foi encerrada.");
			}
			
			Proposal proposal = new ProposalBO().findCurrentProposal(idUser, idDepartment, semester, year);
			
			if(proposal == null) {
				if(new SigetConfigBO().findByDepartment(idDepartment).isRegisterProposal()) {
					throw new Exception("Não foi encontrada a submissão da proposta. É necessário primeiramente submeter a proposta.");
				} else {
					throw new Exception("Não foi encontrado o registro de orientação. É necessário primeiramente registrar a orientação.");
				}
			}
			
			SigetConfig config = new SigetConfigBO().findByDepartment(idDepartment);
			if(config.isSupervisorAgreement() && (proposal.getSupervisorFeedback() != ProposalFeedback.APPROVED)) {
				throw new Exception("O Professor Orientador não preencheu o Termo de Concordância de Orientação.");
			}
			
			p = new Project(Session.getUser(), proposal);
			
			SupervisorChangeBO sbo = new SupervisorChangeBO();
			p.setSupervisor(sbo.findCurrentSupervisor(proposal.getIdProposal()));
			p.setCosupervisor(sbo.findCurrentCosupervisor(proposal.getIdProposal()));
			
			if(config.isValidateAttendances()) {
				if(!new AttendanceBO().validateFrequency(idUser, p.getSupervisor().getIdUser(), p.getProposal().getIdProposal(), 1, config.getAttendanceFrequency())) {
					throw new Exception("As reuniões de orientação registradas não atendem à frequência mínima exigida. As reuniões com o orientador devem ocorrer com frequência " + config.getAttendanceFrequency().toString() + ".");
				}
			}
		}
		
		return p;
	}
	
	public List<byte[]> prepareDocuments(int idUser, int idDepartment, int semester, int year) throws Exception {
		List<byte[]> ret = new ArrayList<byte[]>();
		Project p = this.findCurrentProject(idUser, idDepartment, semester, year);
		int idProposal = 0, idSupervisor = 0;
		String title = "";
		
		if(p == null){
			p = this.findLastProject(idUser, idDepartment, semester, year);
			
			if(p == null) {
				Proposal proposal = new ProposalBO().findCurrentProposal(idUser, idDepartment, semester, year);
				
				if(proposal == null) {
					proposal = new ProposalBO().findLastProposal(idUser, idDepartment);
				}
				
				if(proposal == null) {
					throw new Exception("Não foi encontrado nenhum registro de orientação para imprimir a documentação.");
				} else {
					idProposal = proposal.getIdProposal();
					idSupervisor = new SupervisorChangeBO().findCurrentSupervisor(idProposal).getIdUser();
					title = proposal.getTitle();
				}
			}
		}
		
		if(p != null) {
			idProposal = p.getProposal().getIdProposal();
			idSupervisor = p.getSupervisor().getIdUser();
			title = p.getTitle();
		}
		
		ret.add(new AttendanceBO().getReport(idUser, idProposal, idSupervisor, 1));
		
		if(new SigetConfigBO().findByDepartment(idDepartment).isSupervisorJuryAgreement()) {
			if(p == null) {
				p = new Project();
				p.setTitle(title);
				p.getStudent().setIdUser(idUser);
				p.getStudent().setName(new UserBO().findById(idUser).getName());
				p.getSupervisor().setIdUser(idSupervisor);
				p.getSupervisor().setName(new UserBO().findById(idSupervisor).getName());
			}
			
			List<SupervisorFeedbackReport> list2 = new ArrayList<SupervisorFeedbackReport>();
			list2.add(this.getSupervisorFeedbackReport(p));
			
			ret.add(new ReportUtils().createPdfStream(list2, "SupervisorFeedback", idDepartment).toByteArray());
		}
		
		return ret;
	}

}
