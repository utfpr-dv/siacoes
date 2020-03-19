package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ProposalDAO;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.report.dataset.v1.SupervisorAgreement;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class ProposalBO {
	
	public List<User> listSupervisors(int idProposal, boolean includeCosupervisor) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listSupervisors(idProposal, includeCosupervisor);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

	public List<Proposal> listAll(boolean includeInvalidated) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listAll(includeInvalidated);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Proposal> listBySemester(int idDepartment, int semester, int year) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listBySemester(idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Proposal> listByAppraiser(int idAppraiser, int semester, int year) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listByAppraiser(idAppraiser, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Proposal> listByStudent(int idStudent, int idDepartment) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listByStudent(idStudent, idDepartment);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Proposal> listBySupervisor(int idSupervisor) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listBySupervisor(idSupervisor);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int getProposalStage(int idProposal) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.getProposalStage(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean invalidated(int idUser, int idProposal) throws Exception{
		ProjectBO bo = new ProjectBO();
		Project project = bo.findByProposal(idProposal);
		
		if((project != null) && (project.getIdProject() != 0)) {
			throw new Exception("Não é possível invalidar a proposta pois o projeto já foi enviado.");
		}
		
		try {
			ProposalDAO dao = new ProposalDAO();
			
			Proposal proposal = dao.findById(idProposal);
			
			proposal.setIdProposal(0);
			proposal.setFile(null);
			proposal.setTitle("A Definir");
			proposal.setSubarea("A Definir");
			
			int newId = dao.save(idUser, proposal);
			
			return dao.invalidated(idProposal, newId);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void validate(Proposal proposal) throws Exception {
		SigetConfig config = new SigetConfigBO().findByDepartment(proposal.getDepartment().getIdDepartment());
		
		if(proposal.getFile() != null) {
			if(proposal.getTitle().isEmpty()) {
				throw new Exception("Informe o título da proposta.");
			}
			if(proposal.getSubarea().isEmpty()) {
				throw new Exception("Informe a área e subárea da proposta.");
			}
			if((config.getMaxFileSize() > 0) && ((proposal.getIdProposal() == 0) || !Arrays.equals(proposal.getFile(), new ProposalDAO().getFile(proposal.getIdProposal()))) && (proposal.getFile().length > config.getMaxFileSize())) {
				throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
			}
		}
		if((proposal.getStudent() == null) || (proposal.getStudent().getIdUser() == 0)) {
			throw new Exception("Informe o acadêmico.");
		}
		if((proposal.getSupervisor() == null) || (proposal.getSupervisor().getIdUser() == 0)) {
			throw new Exception("Informe o Professor Orientador.");
		}
		if((proposal.getSemester() == 0) || (proposal.getYear() == 0)) {
			throw new Exception("Informe o ano e semestre da proposta.");
		}
		if(this.getCountTutored(proposal.getIdProposal(), proposal.getDepartment().getIdDepartment(), proposal.getSupervisor().getIdUser(), proposal.getSemester(), proposal.getYear()) >= config.getMaxTutoredStage1()) {
			throw new Exception("O orientador " + proposal.getSupervisor().getName() + " já atingiu o limite de " + config.getMaxTutoredStage1() + " orientados para o TCC 1.");
		}
	}
	
	public int getCountTutored(int idProposal, int idDepartment, int idSupervisor, int semester, int year) throws Exception { 
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.getCountTutored(idProposal, idDepartment, idSupervisor, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(int idUser, Proposal proposal) throws Exception{
		int ret = 0;
		boolean isInsert = (proposal.getIdProposal() == 0);
		byte[] oldFile = null;
		List<Boolean> listEmail = new ArrayList<Boolean>();
		
		if(!isInsert){
			try {
				ProposalDAO dao = new ProposalDAO();
				
				oldFile = dao.findProposalFile(proposal.getIdProposal());
			} catch (SQLException e) {
				oldFile = null;
			}
		}
		
		if(proposal.getAppraisers() != null){
			for(ProposalAppraiser appraiser : proposal.getAppraisers()){
				listEmail.add(appraiser.getIdProposalAppraiser() == 0);
			}
		}
		
		try {
			this.validate(proposal);
			
			ProposalDAO dao = new ProposalDAO();
			
			ret = dao.save(idUser, proposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
		
		try{
			EmailMessageBO bo = new EmailMessageBO();
			List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
			
			keys.add(new EmailMessageEntry<String, String>("student", proposal.getStudent().getName()));
			keys.add(new EmailMessageEntry<String, String>("supervisor", proposal.getSupervisor().getName()));
			if(proposal.getCosupervisor() == null){
				keys.add(new EmailMessageEntry<String, String>("cosupervisor", ""));
			}else{
				keys.add(new EmailMessageEntry<String, String>("cosupervisor", proposal.getCosupervisor().getName()));	
			}
			keys.add(new EmailMessageEntry<String, String>("title", proposal.getTitle()));
			keys.add(new EmailMessageEntry<String, String>("subarea", proposal.getSubarea()));
			
			if(proposal.getFile() == null){
				if(isInsert){
					bo.sendEmail(proposal.getStudent().getIdUser(), MessageType.PROPOSALREGISTERSTUDENT, keys);
					bo.sendEmail(proposal.getSupervisor().getIdUser(), MessageType.PROPOSALREGISTERSUPERVISOR, keys);
					if((proposal.getCosupervisor() != null) && (proposal.getCosupervisor().getIdUser() != 0)){
						bo.sendEmail(proposal.getCosupervisor().getIdUser(), MessageType.PROPOSALREGISTERSUPERVISOR, keys);
					}
				}
			}else{
				if(oldFile == null){
					bo.sendEmail(proposal.getStudent().getIdUser(), MessageType.PROPOSALSUBMITEDSTUDENT, keys);
					bo.sendEmail(proposal.getSupervisor().getIdUser(), MessageType.PROPOSALSUBMITEDSUPERVISOR, keys);
					if((proposal.getCosupervisor() != null) && (proposal.getCosupervisor().getIdUser() != 0)){
						bo.sendEmail(proposal.getCosupervisor().getIdUser(), MessageType.PROPOSALSUBMITEDSUPERVISOR, keys);
					}
				}else if(!Arrays.equals(proposal.getFile(), oldFile)){
					bo.sendEmail(proposal.getStudent().getIdUser(), MessageType.PROPOSALCHANGEDSTUDENT, keys);
					bo.sendEmail(proposal.getSupervisor().getIdUser(), MessageType.PROPOSALCHANGEDSUPERVISOR, keys);
					if((proposal.getCosupervisor() != null) && (proposal.getCosupervisor().getIdUser() != 0)){
						bo.sendEmail(proposal.getCosupervisor().getIdUser(), MessageType.PROPOSALCHANGEDSUPERVISOR, keys);
					}
				}
			}
			
			if(proposal.getAppraisers() != null){
				User manager = new UserBO().findManager(proposal.getDepartment().getIdDepartment(), SystemModule.SIGET);
				String availableDate = DateUtils.format(DateUtils.addDay(new DeadlineBO().findBySemester(proposal.getDepartment().getIdDepartment(), proposal.getSemester(), proposal.getYear()).getProposalDeadline(), 1), "dd/MM/yyyy");
				
				for(int i = 0; i < proposal.getAppraisers().size(); i++){
					if(listEmail.get(i)){
						keys = new ArrayList<EmailMessageEntry<String, String>>();
						
						keys.add(new EmailMessageEntry<String, String>("student", proposal.getStudent().getName()));
						keys.add(new EmailMessageEntry<String, String>("supervisor", proposal.getSupervisor().getName()));
						if(proposal.getCosupervisor() == null) {
							keys.add(new EmailMessageEntry<String, String>("cosupervisor", ""));
						} else {
							keys.add(new EmailMessageEntry<String, String>("cosupervisor", proposal.getCosupervisor().getName()));	
						}
						keys.add(new EmailMessageEntry<String, String>("title", proposal.getTitle()));
						keys.add(new EmailMessageEntry<String, String>("subarea", proposal.getSubarea()));
						keys.add(new EmailMessageEntry<String, String>("appraiser", proposal.getAppraisers().get(i).getAppraiser().getName()));
						keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
						keys.add(new EmailMessageEntry<String, String>("availabledate", availableDate));
						
						bo.sendEmail(proposal.getAppraisers().get(i).getAppraiser().getIdUser(), MessageType.PROPOSALAPPRAISERREGISTER, keys);
					}
				}
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		return ret;
	}
	
	public int saveSupervisorFeedback(Proposal proposal) throws Exception {
		try {
			if(proposal.getIdProposal() == 0) {
				throw new Exception("O acadêmico deve submeter a Proposta de TCC 1 para que ela seja avaliada.");
			}
			if(proposal.getSupervisorFeedback() == ProposalFeedback.NONE) {
				throw new Exception("Informe o parecer sobre a orientação de TCC.");
			}
			if(Document.hasSignature(DocumentType.SUPERVISORAGREEMENT, proposal.getIdProposal())) {
				throw new Exception("Não é possível efetuar alterações no parecer pois ele já foi assinado.");
			}
			
			ProposalDAO dao = new ProposalDAO();
			
			return dao.saveSupervisorFeedback(proposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendSupervisorFeedbackSignedMessage(int idProposal) throws Exception {
		Proposal proposal = this.findById(idProposal);
		User manager = new UserBO().findManager(proposal.getDepartment().getIdDepartment(), SystemModule.SIGET);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("student", proposal.getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("supervisor", proposal.getSupervisor().getName()));
		keys.add(new EmailMessageEntry<String, String>("title", proposal.getTitle()));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDSUPERVISORAGREEMENT, keys);
	}
	
	public byte[] getSupervisorFeedbackReport(int idProposal) throws Exception {
		if(Document.hasSignature(DocumentType.SUPERVISORAGREEMENT, idProposal)) {
			return Document.getSignedDocument(DocumentType.SUPERVISORAGREEMENT, idProposal);
		} else {
			Proposal proposal = this.findById(idProposal);
			SupervisorAgreement dataset = SignDatasetBuilder.build(proposal);
			
			List<SupervisorAgreement> list = new ArrayList<SupervisorAgreement>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "SupervisorAgreement", proposal.getDepartment().getIdDepartment()).toByteArray();
		}
	}
	
	public String getStudentName(int id) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.getStudentName(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Proposal findById(int id) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Proposal findByProject(int idProject) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.findByProject(idProject);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Proposal findByThesis(int idThesis) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.findByThesis(idThesis);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Proposal findCurrentProposal(int idStudent, int idDepartment, int semester, int year) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.findCurrentProposal(idStudent, idDepartment, semester, year);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Proposal findLastProposal(int idStudent, int idDepartment) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.findLastProposal(idStudent, idDepartment);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public Proposal prepareProposal(int idUser, int idDepartment, int semester, int year, boolean onlyRegister) throws Exception {
		Proposal p = this.findCurrentProposal(idUser, idDepartment, semester, year);
		
		if(p == null){
			Deadline d = new DeadlineBO().findBySemester(idDepartment, semester, year);
			
			if((d == null) || DateUtils.getToday().getTime().after(d.getProposalDeadline())) {
				if(onlyRegister) {
					throw new Exception("O registro de orientações já foi encerrado.");
				} else {
					throw new Exception("A submissão de propostas já foi encerrada.");	
				}
			}
			
			if(new ProjectBO().findApprovedProject(idUser, idDepartment, semester, year) != null) {
				throw new Exception("Você já foi aprovado na disciplina de TCC 1 e não pode fazer " +
						(onlyRegister ? "o registro de uma nova orientação." : "a submissão de uma nova proposta."));
			}
			
			p = new Proposal(new UserBO().findById(idUser));
		}
		
		return p;
	}
	
	public long getCurrentProposals() throws Exception{
		try {
			return new ProposalDAO().getCurrentProposals(DateUtils.getSemester(), DateUtils.getYear());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdCampus(int idProposal) throws Exception {
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.findIdCampus(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int findIdDepartment(int idProposal) throws Exception {
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.findIdDepartment(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<ProposalAppraiser> listProposalFeedback(int idDepartment, int semester, int year) throws Exception {
		List<Proposal> proposals = this.listBySemester(idDepartment, semester, year);
		List<ProposalAppraiser> list = new ArrayList<ProposalAppraiser>();
		
		for(Proposal p : proposals) {
			List<ProposalAppraiser> appraisers = new ProposalAppraiserBO().listAppraisers(p.getIdProposal());
			
			if(appraisers.size() > 0) {
				for(ProposalAppraiser a : appraisers) {
					a.setProposal(p);
					list.add(a);
				}
			} else {
				ProposalAppraiser a = new ProposalAppraiser();
				
				a.getAppraiser().setName("--");
				a.setProposal(p);
				
				list.add(a);
			}
		}
		
		return list;
	}
	
	public byte[] getProposalFeedbackReport(int idDepartment, int semester, int year) throws Exception {
		try {
			return new ReportUtils().createPdfStream(this.listProposalFeedback(idDepartment, semester, year), "ProposalFeedbackList", idDepartment).toByteArray();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
