package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SupervisorChangeDAO;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange.ChangeFeedback;

public class SupervisorChangeBO {
	
	public int save(SupervisorChange change) throws Exception{
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
		
		try {
			SupervisorChangeDAO dao = new SupervisorChangeDAO();
			
			return dao.save(change);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
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

}
