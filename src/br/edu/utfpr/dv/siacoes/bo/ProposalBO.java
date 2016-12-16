package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ProposalDAO;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class ProposalBO {

	public List<Proposal> listAll() throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.listAll();
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
	
	public int getProposalStage(int idProposal) throws Exception{
		try {
			ProposalDAO dao = new ProposalDAO();
			
			return dao.getProposalStage(idProposal);
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
		
		DepartmentBO bo = new DepartmentBO();
		Department department = bo.findById(proposal.getDepartment().getIdDepartment());
		
		if((department != null) && department.isSigetRegisterProposal()){
			if(proposal.getFile() == null){
				throw new Exception("É necessário enviar o arquivo da proposta.");
			}
			if(proposal.getFileType() == DocumentType.UNDEFINED){
				throw new Exception("O arquivo enviado não está no formato correto. Envie um arquivo PDF, DOC ou DOCX");
			}
		}
	}
	
	public int save(Proposal proposal) throws Exception{
		try {
			this.validate(proposal);
			
			ProposalDAO dao = new ProposalDAO();
			
			return dao.save(proposal);
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
	
}
