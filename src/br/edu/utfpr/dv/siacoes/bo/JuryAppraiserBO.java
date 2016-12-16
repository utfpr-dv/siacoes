package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.dao.JuryAppraiserDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.StatementReport;

public class JuryAppraiserBO {
	
	public JuryAppraiser findById(int id) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findById(id);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public JuryAppraiser findByAppraiser(int idJury, int idUser) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.findByAppraiser(idJury, idUser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<JuryAppraiser> listAppraisers(int idJury) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.listAppraisers(idJury);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public int save(JuryAppraiser appraiser) throws Exception{
		try {
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.save(appraiser);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean appraiserHasJury(int idJury, int idUser, Date date) throws Exception{
		Date startDate, endDate;
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, -1);
		startDate = cal.getTime();
		
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, 1);
		endDate = cal.getTime();
		
		try{
			JuryAppraiserDAO dao = new JuryAppraiserDAO();
			
			return dao.appraiserHasJury(idJury, idUser, startDate, endDate);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public StatementReport getStatementReport(int id) throws Exception {
		if(id == 0){
			throw new Exception("É preciso salvar a o membro antes de gerar a declaração.");
		}
		
		return this.loadStatement(this.findById(id));
	}
	
	public List<StatementReport> getStatementReportList(int idJury) throws Exception {
		List<JuryAppraiser> appraiserList = this.listAppraisers(idJury);
		List<StatementReport> list = new ArrayList<StatementReport>();
		
		for(JuryAppraiser appraiser : appraiserList){
			list.add(this.loadStatement(appraiser));
		}
		
		return list;
	}
	
	public List<StatementReport> getStatementReportListByThesis(int idThesis) throws Exception {
		ThesisBO bo = new ThesisBO();
		int idJury = bo.findIdJury(idThesis);
		
		return this.getStatementReportList(idJury);
	}
	
	public List<StatementReport> getStatementReportListByProject(int idProject) throws Exception {
		ProjectBO bo = new ProjectBO();
		int idJury = bo.findIdJury(idProject);
		
		return this.getStatementReportList(idJury);
	}
	
	private StatementReport loadStatement(JuryAppraiser appraiser){
		StatementReport statement = new StatementReport();
		
		statement.setDate(appraiser.getJury().getDate());
		statement.setEndTime(appraiser.getJury().getEndTime());
		statement.setName(appraiser.getAppraiser().getName());
		statement.setStartTime(appraiser.getJury().getStartTime());
		
		if((appraiser.getJury().getProject() != null) && (appraiser.getJury().getProject().getIdProject() != 0)){
			statement.setEvent("Trabalho de Conclusão de Curso 1");
			statement.setStudent(appraiser.getJury().getProject().getStudent().getName());
			statement.setTitle(appraiser.getJury().getProject().getTitle());
		}else{
			statement.setEvent("Trabalho de Conclusão de Curso 2");
			statement.setStudent(appraiser.getJury().getThesis().getStudent().getName());
			statement.setTitle(appraiser.getJury().getThesis().getTitle());
		}
		
		statement.setManagerName(Session.getUser().getName());
		
		return statement;
	}

}
