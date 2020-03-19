package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SupervisorChangeDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange.ChangeFeedback;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;

public class SupervisorChangeBO {
	
	public int save(int idUser, SupervisorChange change) throws Exception{
		if((change.getOldSupervisor() == null) || (change.getOldSupervisor().getIdUser() == 0)){
			throw new Exception("Informe o orientador atual.");
		}
		if((change.getNewSupervisor() == null) || (change.getNewSupervisor().getIdUser() == 0)){
			throw new Exception("Informe o novo orientador.");
		}
		if((change.getNewCosupervisor() != null) && (change.getNewSupervisor().getIdUser() == change.getNewCosupervisor().getIdUser())){
			throw new Exception("O orientador e coorientador não devem ser as mesmas pessoas.");
		}
		
		if(change.getNewSupervisor().getIdUser() == change.getOldSupervisor().getIdUser()){
			change.setApproved(ChangeFeedback.APPROVED);
			change.setApprovalDate(DateUtils.getNow().getTime());
			change.setApprovalComments("Não houve alteração de orientador.");
		}else{
			if(change.getComments().trim().isEmpty()){
				throw new Exception("Informe o motivo da substituição do orientador.");
			}
		}
		
		if((change.getIdSupervisorChange() != 0) && Document.hasSignature(DocumentType.SUPERVISORCHANGE, change.getIdSupervisorChange())) {
			throw new Exception("Não é possível alterar a solicitação pois ela já foi assinada.");
		}
		
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.save(idUser, change);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void sendSupervisorChangeMessage(int idSupervisorChange) throws Exception {
		SupervisorChange change = this.findById(idSupervisorChange);
		change.setProposal(new ProposalBO().findById(change.getProposal().getIdProposal()));
		User manager = new UserBO().findManager(change.getProposal().getDepartment().getIdDepartment(), SystemModule.SIGET);
		List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
		
		keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
		keys.add(new EmailMessageEntry<String, String>("type", change.isSupervisorRequest() ? "Professor(a) Orientador(a)" : "acadêmico(a)"));
		keys.add(new EmailMessageEntry<String, String>("name", change.isSupervisorRequest() ? change.getOldSupervisor().getName() : change.getProposal().getStudent().getName()));
		keys.add(new EmailMessageEntry<String, String>("title", change.getProposal().getTitle()));
		
		new EmailMessageBO().sendEmail(manager.getIdUser(), MessageType.SIGNEDSUPERVISORCHANGE, keys);
	}
	
	public SupervisorChange findPendingChange(int idProposal) throws Exception{
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.findPendingChange(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User findCurrentSupervisor(int idProposal) throws Exception{
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.findCurrentSupervisor(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public User findCurrentCosupervisor(int idProposal) throws Exception{
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.findCurrentCosupervisor(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<SupervisorChange> listByProposal(int idProposal) throws Exception {
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.listByProposal(idProposal);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<SupervisorChange> list(int idDepartment, int semester, int year, boolean onlyPending) throws Exception{
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.list(idDepartment, semester, year, onlyPending);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<SupervisorChange> list(int idSupervisor) throws Exception{
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.list(idSupervisor);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public SupervisorChange findById(int id) throws Exception{
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public byte[] getSupervisorChangeReport(int idSupervisorChange) throws Exception {
		if(Document.hasSignature(DocumentType.SUPERVISORCHANGE, idSupervisorChange)) {
			return Document.getSignedDocument(DocumentType.SUPERVISORCHANGE, idSupervisorChange);
		} else {
			SupervisorChange change = this.findById(idSupervisorChange);
			
			if(change.getOldSupervisor().getIdUser() == change.getNewSupervisor().getIdUser()) {
				throw new Exception("A requisição não pode ser impressa pois não houve troca de orientador.");
			}
			
			br.edu.utfpr.dv.siacoes.report.dataset.v1.SupervisorChange dataset = SignDatasetBuilder.build(change);
			
			List<br.edu.utfpr.dv.siacoes.report.dataset.v1.SupervisorChange> list = new ArrayList<br.edu.utfpr.dv.siacoes.report.dataset.v1.SupervisorChange>();
			list.add(dataset);
			
			return new ReportUtils().createPdfStream(list, "SupervisorChangeStatement", change.getProposal().getDepartment().getIdDepartment()).toByteArray();
		}
	}

}
