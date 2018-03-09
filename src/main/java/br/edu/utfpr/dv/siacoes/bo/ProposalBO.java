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
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;

public class ProposalBO {
	
	public List<User> listSupervisors(int idProposal) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listSupervisors(idProposal);
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
	
	public List<Proposal> listByStudent(int idStudent) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listByStudent(idStudent);
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
	
	public boolean invalidated(int idProposal) throws Exception{
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
			proposal.setFileType(DocumentType.UNDEFINED);
			proposal.setTitle("A Definir");
			proposal.setSubarea("A Definir");
			
			int newId = dao.save(proposal);
			
			return dao.invalidated(idProposal, newId);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void validate(Proposal proposal) throws Exception{
		if(proposal.getTitle().isEmpty()){
			throw new Exception("Informe o título da proposta.");
		}
		if(proposal.getSubarea().isEmpty()){
			throw new Exception("Informe a área e subárea da proposta.");
		}
		if((proposal.getStudent() == null) || (proposal.getStudent().getIdUser() == 0)){
			throw new Exception("Informe o acadêmico.");
		}
		if((proposal.getSupervisor() == null) || (proposal.getSupervisor().getIdUser() == 0)){
			throw new Exception("Informe o Professor Orientador.");
		}
		if((proposal.getSemester() == 0) || (proposal.getYear() == 0)){
			throw new Exception("Informe o ano e semestre da proposta.");
		}
	}
	
	public int save(Proposal proposal) throws Exception{
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
			
			ret = dao.save(proposal);
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
					bo.sendEmail(proposal.getStudent().getIdUser(), MessageType.PROPOSALCHANGESTUDENT, keys);
					bo.sendEmail(proposal.getSupervisor().getIdUser(), MessageType.PROPOSALCHANGESUPERVISOR, keys);
					if((proposal.getCosupervisor() != null) && (proposal.getCosupervisor().getIdUser() != 0)){
						bo.sendEmail(proposal.getCosupervisor().getIdUser(), MessageType.PROPOSALCHANGESUPERVISOR, keys);
					}
				}
			}
			
			if(proposal.getAppraisers() != null){
				for(int i = 0; i < proposal.getAppraisers().size(); i++){
					if(listEmail.get(i)){
						keys = new ArrayList<EmailMessageEntry<String, String>>();
						
						keys.add(new EmailMessageEntry<String, String>("student", proposal.getStudent().getName()));
						keys.add(new EmailMessageEntry<String, String>("supervisor", proposal.getSupervisor().getName()));
						if(proposal.getCosupervisor() == null){
							keys.add(new EmailMessageEntry<String, String>("cosupervisor", ""));
						}else{
							keys.add(new EmailMessageEntry<String, String>("cosupervisor", proposal.getCosupervisor().getName()));	
						}
						keys.add(new EmailMessageEntry<String, String>("title", proposal.getTitle()));
						keys.add(new EmailMessageEntry<String, String>("subarea", proposal.getSubarea()));
						keys.add(new EmailMessageEntry<String, String>("appraiser", proposal.getAppraisers().get(i).getAppraiser().getName()));
						
						bo.sendEmail(proposal.getAppraisers().get(i).getAppraiser().getIdUser(), MessageType.PROPOSALAPPRAISERREGISTER, keys);
					}
				}
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		return ret;
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
	
	public long getCurrentProposals() throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.getCurrentProposals();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
}
