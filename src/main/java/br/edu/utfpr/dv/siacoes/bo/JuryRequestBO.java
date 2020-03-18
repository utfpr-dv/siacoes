package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.JuryRequestDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;

public class JuryRequestBO {
	
	public List<JuryRequest> listBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return dao.listBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryRequest> listByAppraiser(int idUser, int semester, int year) throws Exception{
		try {
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return dao.listByAppraiser(idUser, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryRequest findById(int id) throws Exception{
		try {
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryRequest findByStage(int idProposal, int stage) throws Exception{
		try {
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return dao.findByStage(idProposal, stage);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryRequest findByProject(int idProject) throws Exception {
		try {
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return dao.findByProject(idProject);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryRequest findByThesis(int idThesis) throws Exception {
		try {
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return dao.findByThesis(idThesis);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idJuryRequest) throws Exception{
		try {
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return dao.findIdDepartment(idJuryRequest);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, JuryRequest jury) throws Exception{
		try {
			if((jury.getProposal() == null) || (jury.getProposal().getIdProposal() == 0)){
				throw new Exception("Informe o projeto ou monografia a que a banca pertence.");
			}
			if(jury.getLocal().isEmpty()){
				throw new Exception("Informe o local da banca.");
			}
			if((jury.getDate() == null) || jury.getDate().before(DateUtils.getNow().getTime())) {
				throw new Exception("A data e horário da banca não pode ser inferior à data e horário atual.");
			}
			if((jury.getJury() != null) && (jury.getJury().getIdJury() != 0)) {
				throw new Exception("A banca já foi confirmada e a solicitação não pode ser alterada.");
			}
			if(Document.hasSignature(DocumentType.JURYREQUEST, jury.getIdJuryRequest())) {
				throw new Exception("A solicitação não pode ser alterada pois ela já foi assinada.");
			}
			if(jury.getAppraisers() != null){
				User supervisor = jury.getSupervisor();
				User cosupervisor = jury.getCosupervisor();
				boolean find = false, findSupervisor = false, findCosupervisor = false;
				int countChair = 0, countMembers = 0, countSubstitutes = 0;
				
				for(JuryAppraiserRequest appraiser : jury.getAppraisers()){
					if(new JuryAppraiserRequestBO().appraiserHasJuryRequest(jury.getIdJuryRequest(), appraiser.getAppraiser().getIdUser(), jury.getDate()) || new JuryAppraiserBO().appraiserHasJury(0, appraiser.getAppraiser().getIdUser(), jury.getDate())){
						throw new Exception("O membro " + appraiser.getAppraiser().getName() + " já tem uma banca marcada que conflita com este horário.");
					}
					
					if(appraiser.getAppraiser().getIdUser() == supervisor.getIdUser()) {
						findSupervisor = true;
					}
					
					if((cosupervisor != null) && (appraiser.getAppraiser().getIdUser() == cosupervisor.getIdUser())) {
						findCosupervisor = true;
					}
					
					if(appraiser.isChair() && ((appraiser.getAppraiser().getIdUser() == supervisor.getIdUser()) || ((cosupervisor != null) && (appraiser.getAppraiser().getIdUser() == cosupervisor.getIdUser())))){
						find = true;
					}
					
					if(appraiser.isSubstitute()) {
						countSubstitutes++;
					} else if(!appraiser.isChair()) {
						countMembers++;
					} else {
						countChair++;
					}
				}
				
				if(find) {
					jury.setSupervisorAbsenceReason("");
				} else if(jury.getSupervisorAbsenceReason().trim().isEmpty()) {
					throw new Exception("Informe o motido do Professor Orientador ou o Coorientador não estar presidindo a banca.");
				}
				if(findSupervisor && findCosupervisor) {
					throw new Exception("O Coorientador não pode fazer parte da banca quando o orientador já compõe a mesma");
				}
				if(countChair == 0) {
					throw new Exception("É preciso indicar o presidente da banca.");
				} else if(countChair > 1) {
					throw new Exception("Apenas um membro pode ser presidente da banca.");
				}
				
				SigetConfig config = new SigetConfigBO().findByDepartment(new ProposalBO().findIdDepartment(jury.getProposal().getIdProposal()));
				if(countMembers < config.getMinimumJuryMembers()) {
					throw new Exception("A banca deverá ser composta por, no mínimo, " + String.valueOf(config.getMinimumJuryMembers()) + " membros titulares (sem contar o presidente).");
				}
				if(countSubstitutes < config.getMinimumJurySubstitutes()) {
					throw new Exception("A banca deverá ser conter, no mínimo, " + String.valueOf(config.getMinimumJurySubstitutes()) + " suplente(s).");
				}
			}
			
			JuryRequestDAO dao = new JuryRequestDAO();
			
			return  dao.save(idUser, jury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendRequestSignedMessage(int idJuryRequest) throws Exception {
		JuryRequest request = this.findById(idJuryRequest);
		request.setProposal(new ProposalBO().findById(request.getProposal().getIdProposal()));
		User manager = new UserBO().findManager(request.getProposal().getDepartment().getIdDepartment(), SystemModule.SIGET);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", request.getProposal().getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", request.getProposal().getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("title", request.getProposal().getTitle()));
		keys.add(new EmailMessageEntry<String, String>("stage", String.valueOf(request.getStage())));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDJURYREQUEST, keys);
	}
	
	public void sendRequestSignJuryRequestMessage(int idJuryRequest, List<User> users) throws Exception {
		JuryRequest request = this.findById(idJuryRequest);
		request.setProposal(new ProposalBO().findById(request.getProposal().getIdProposal()));
		
		for(User user : users) {
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("name", user.getName()));
			keys.add(new EmailMessageEntry<String, String>("student", request.getProposal().getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("supervisor", request.getProposal().getSupervisor().getName()));
			keys.add(new EmailMessageEntry<String, String>("title", request.getProposal().getTitle()));
			keys.add(new EmailMessageEntry<String, String>("stage", String.valueOf(request.getStage())));
			
			new EmailMessageBO().sendEmail(user.getIdUser(), MessageType.REQUESTSIGNJURYREQUEST, keys, false);
		}
	}
	
	public boolean delete(int idUser, int id) throws Exception {
		JuryRequest request = this.findById(id);
		
		return this.delete(idUser, request);
	}
	
	private boolean delete(int idUser, JuryRequest request) throws Exception {
		if((request.getJury() != null) && (request.getJury().getIdJury() != 0)) {
			throw new Exception("A solicitação não pode ser excluída pois a banca já foi confirmada.");
		}
		if(Document.hasSignature(DocumentType.JURYREQUEST, request.getIdJuryRequest())) {
			throw new Exception("A solicitação não pode ser excluída pois ela já foi assinada.");
		}
		
		try {
			return new JuryRequestDAO().delete(idUser, request.getIdJuryRequest());
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean canAddAppraiser(JuryRequest jury, User appraiser) throws Exception{
		if(jury.getAppraisers() != null){
			for(JuryAppraiserRequest ja : jury.getAppraisers()){
				if(ja.getAppraiser().getIdUser() == appraiser.getIdUser()){
					throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
				}
			}
		}else if(jury.getIdJuryRequest() != 0){
			JuryAppraiserRequestBO bo = new JuryAppraiserRequestBO();
			JuryAppraiserRequest ja = bo.findByAppraiser(jury.getIdJuryRequest(), appraiser.getIdUser());
			
			if(ja != null){
				throw new Exception("O professor " + appraiser.getName() + " já faz parte da banca.");
			}
		}
		
		return true;
	}
	
	public JuryRequest prepareJuryRequest(int idProposal, int semester, int year) throws Exception {
		JuryRequest jury = new JuryRequest();
		
		jury.setAppraisers(new ArrayList<JuryAppraiserRequest>());
		jury.getProposal().setIdProposal(idProposal);
		
		Thesis thesis = new ThesisBO().findByProposal(idProposal);
		Project project = null;
		
		if(thesis != null) {
			jury.setStage(2);
			project = thesis.getProject();
		} else {
			project = new ProjectBO().findByProposal(idProposal);
			
			if(project != null) {
				if((project.getYear() < year) || (project.getSemester() < semester)) {
					jury.setStage(2);
				} else {
					jury.setStage(1);
				}
			} else {
				jury.setStage(1);
			}
		}
		
		JuryRequest request = this.findByStage(idProposal, jury.getStage());
		if((request != null) && (request.getIdJuryRequest() != 0)) {
			return request;
		}
		
		/*boolean hasJury = false;
		if(jury.getStage() == 2) {
			if((thesis != null) && (thesis.getIdThesis() != 0)) {
				Jury j = new JuryDAO().findByThesis(thesis.getIdThesis());
				
				if((j != null) && (j.getIdJury() != 0)) {
					hasJury = true;
				}
			}
		} else {
			if((project != null) && (project.getIdProject() != 0)) {
				Jury j = new JuryDAO().findByProject(project.getIdProject());
				
				if((j != null) && (j.getIdJury() != 0)) {
					hasJury =  true;
				}
			}
		}
		if(hasJury) {
			throw new Exception("A banca já foi agendada pelo Professor Responsável pelo TCC.");
		}*/
		
		User supervisor = new SupervisorChangeBO().findCurrentSupervisor(idProposal);
		
		JuryAppraiserRequest chair = new JuryAppraiserRequest();
		
		chair.setAppraiser(supervisor);
		chair.setSubstitute(false);
		chair.setChair(true);
		
		jury.getAppraisers().add(chair);
		
		if(jury.getStage() == 2) {
			Jury j = new JuryBO().findByProject(project.getIdProject());
			
			if(j.getAppraisers() == null) {
				j.setAppraisers(new JuryAppraiserBO().listAppraisers(j.getIdJury()));
			}
			
			for(JuryAppraiser a : j.getAppraisers()) {
				if(!a.isChair() && !a.isSubstitute()) {
					JuryAppraiserRequest appraiser = new JuryAppraiserRequest();
					
					appraiser.setAppraiser(a.getAppraiser());
					appraiser.setSubstitute(false);
					appraiser.setChair(false);
					
					jury.getAppraisers().add(appraiser);
				}
			}
		} else {
			List<ProposalAppraiser> appraisers = new ProposalAppraiserBO().listAppraisers(idProposal);
			
			for(ProposalAppraiser a : appraisers) {
				JuryAppraiserRequest appraiser = new JuryAppraiserRequest();
				
				appraiser.setAppraiser(a.getAppraiser());
				appraiser.setSubstitute(false);
				appraiser.setChair(false);
				
				jury.getAppraisers().add(appraiser);
			}
		}
		
		return jury;
	}
	
	public byte[] getJuryRequestForm(int idProposal, int semester, int year) throws Exception {
		Thesis thesis = new ThesisBO().findByProposal(idProposal);
		Project project = null;
		int stage = 0;
		
		if(thesis != null) {
			stage = 2;
			project = thesis.getProject();
		} else {
			project = new ProjectBO().findByProposal(idProposal);
			
			if(project != null) {
				if((project.getYear() < year) || (project.getSemester() < semester)) {
					stage = 2;
				} else {
					stage = 1;
				}
			} else {
				stage = 1;
			}
		}
		
		JuryRequest jury = this.findByStage(idProposal, stage);
		if((jury != null) && (jury.getIdJuryRequest() != 0)) {
			return this.getJuryRequestForm(jury.getIdJuryRequest());
		} else {
			throw new Exception("A solicitação de agendamento de banca ainda não foi preenchida.");
		}
	}
	
	public byte[] getJuryRequestForm(int idJuryRequest) throws Exception {
		if(Document.hasSignature(DocumentType.JURYREQUEST, idJuryRequest)) {
			return Document.getSignedDocument(DocumentType.JURYREQUEST, idJuryRequest);
		} else {
			br.edu.utfpr.dv.siacoes.report.dataset.v1.JuryRequest dataset = SignDatasetBuilder.build(this.getJuryRequestFormReport(idJuryRequest));
			
			List<br.edu.utfpr.dv.siacoes.report.dataset.v1.JuryRequest> list = new ArrayList<br.edu.utfpr.dv.siacoes.report.dataset.v1.JuryRequest>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "JuryFormRequest", this.findIdDepartment(idJuryRequest)).toByteArray();
		}
	}
	
	public JuryFormReport getJuryRequestFormReport(int idJuryRequest) throws Exception {
		try {
			JuryFormReport report = new JuryFormReport();
			JuryRequest jury = this.findById(idJuryRequest);
			
			if((jury == null) || (jury.getIdJuryRequest() == 0)) {
				throw new Exception("A solicitação de agendamento de banca ainda não foi preenchida.");
			}
			
			report.setDate(jury.getDate());
			report.setStage(jury.getStage());
			report.setLocal(jury.getLocal());
			report.setSupervisor(jury.getSupervisor().getName());
			report.setIdSupervisor(jury.getSupervisor().getIdUser());
			
			if(jury.getSupervisorAbsenceReason().trim().isEmpty()) {
				report.setComments(jury.getComments());	
			} else {
				report.setComments("Motivo da ausência do Professor Orientador: " + jury.getSupervisorAbsenceReason() + "\n\n" + jury.getComments());
			}
			
			if(jury.getStage() == 2) {
				Thesis thesis = new ThesisBO().findByProposal(jury.getProposal().getIdProposal());
				
				if((thesis != null) && (thesis.getIdThesis() != 0)) {
					report.setTitle(thesis.getTitle());
					report.setStudent(thesis.getStudent().getName());
					report.setIdStudent(thesis.getStudent().getIdUser());
				} else {
					Project project = new ProjectBO().findByProposal(jury.getProposal().getIdProposal());
					
					report.setTitle(project.getTitle());
					report.setStudent(project.getStudent().getName());
					report.setIdStudent(project.getStudent().getIdUser());
				}
			} else {
				Project project = new ProjectBO().findByProposal(jury.getProposal().getIdProposal());
				
				if((project != null) && (project.getIdProject() != 0)) {
					report.setTitle(project.getTitle());
					report.setStudent(project.getStudent().getName());
					report.setIdStudent(project.getStudent().getIdUser());
				} else {
					Proposal proposal = new ProposalBO().findById(jury.getProposal().getIdProposal());
					
					report.setTitle(proposal.getTitle());
					report.setStudent(proposal.getStudent().getName());
					report.setIdStudent(proposal.getStudent().getIdUser());
				}
			}
			
			jury.setAppraisers(new JuryAppraiserRequestBO().listAppraisers(idJuryRequest));
			int member = 1, substitute = 1;
			
			for(JuryAppraiserRequest appraiser : jury.getAppraisers()) {
				JuryFormAppraiserReport appraiserReport = new JuryFormAppraiserReport();
				
				appraiserReport.setIdUser(appraiser.getAppraiser().getIdUser());
				appraiserReport.setName(appraiser.getAppraiser().getName());
				appraiserReport.setDate(report.getDate());
				appraiserReport.setStage(report.getStage());
				appraiserReport.setStudent(report.getStudent());
				appraiserReport.setTitle(report.getTitle());
				
				if(appraiser.isChair()) {
					appraiserReport.setDescription("Presidente");
				} else if(appraiser.isSubstitute()) {
					appraiserReport.setDescription("Suplente " + String.valueOf(substitute));
					substitute = substitute + 1;
				} else {
					appraiserReport.setDescription("Membro " + String.valueOf(member));
					member = member + 1;
				}
				
				if(appraiser.isChair()) {
					report.getAppraisers().add(0, appraiserReport);
				} else {
					report.getAppraisers().add(appraiserReport);
				}
			}
			
			return report;
		} catch(SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
