package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.BugReportDAO;
import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class BugReportBO {
	
	public List<BugReport> listAll() throws Exception{
		try {
			BugReportDAO dao = new BugReportDAO();
			
			return dao.listAll();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(BugReport bug) throws Exception{
		try {
			if((bug.getUser() == null) || (bug.getUser().getIdUser() == 0)){
				throw new Exception("Informe o usuário.");
			}
			if(bug.getTitle().isEmpty()){
				throw new Exception("Informe a descrição do problema.");
			}
			if(bug.getDescription().isEmpty()){
				throw new Exception("Informe a descrição detalhada do problema.");
			}
			if(bug.getReportDate() == null){
				throw new Exception("Informe a data do problema.");
			}
			
			if((bug.getStatus() != BugStatus.REPORTED) && (bug.getStatusDate() == null)){
				bug.setStatusDate(DateUtils.getToday().getTime());
			}
			
			BugReportDAO dao = new BugReportDAO();
			
			return dao.save(bug);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public BugReport findById(int id) throws Exception{
		try {
			BugReportDAO dao = new BugReportDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}

}
