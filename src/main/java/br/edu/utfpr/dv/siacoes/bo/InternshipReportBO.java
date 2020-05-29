package br.edu.utfpr.dv.siacoes.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipReportDAO;
import br.edu.utfpr.dv.siacoes.model.EmailMessageEntry;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportFeedback;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class InternshipReportBO {
	
	private Connection conn;
	
	public InternshipReportBO(){
		this.conn = null;
	}
	
	public InternshipReportBO(Connection conn){
		this.conn = conn;
	}
	
	public List<InternshipReport> listAll() throws Exception{
		try{
			InternshipReportDAO dao = new InternshipReportDAO(this.conn);
			
			return dao.listAll();
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public List<InternshipReport> listByInternship(int idInternship) throws Exception{
		try{
			InternshipReportDAO dao = new InternshipReportDAO(this.conn);
			
			return dao.listByInternship(idInternship);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public InternshipReport findById(int id) throws Exception{
		try{
			InternshipReportDAO dao = new InternshipReportDAO(this.conn);
			
			return dao.findById(id);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean hasReport(int idInternship, ReportType type, ReportFeedback feedback) throws Exception{
		try{
			InternshipReportDAO dao = new InternshipReportDAO(this.conn);
			
			return dao.hasReport(idInternship, type, feedback);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean hasReport(int idInternship, ReportType type) throws Exception{
		try{
			InternshipReportDAO dao = new InternshipReportDAO(this.conn);
			
			return dao.hasReport(idInternship, type);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public boolean validateReport(int idUser, InternshipReport report) throws Exception {
		report.setFeedbackDate(DateUtils.getNow().getTime());
		try{
			return new InternshipReportDAO(this.conn).validateReport(idUser, report);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(int idUser, InternshipReport report) throws Exception{
		int ret = 0;
		boolean isInsert = (report.getIdInternshipReport() == 0);
		
		if((report.getInternship() == null) || (report.getInternship().getIdInternship() == 0)){
			throw new Exception("Informe o estágio do relatório.");
		}
		if(report.getReport() == null){
			throw new Exception("Faça o upload do relatório.");
		}
		if(report.isFinalReport() && (report.getType() == ReportType.STUDENT)) {
			throw new Exception("O relatório final do acadêmico deve ser submetido diretamente no estágio.");
		}
		
		InternshipReportDAO dao = new InternshipReportDAO(this.conn);
		SigesConfig config = new SigesConfigBO().findByDepartment(new InternshipBO().findIdDepartment(report.getInternship().getIdInternship()));
		
		if((config.getMaxFileSize() > 0) && ((report.getIdInternshipReport() == 0) || !Arrays.equals(report.getReport(), dao.getReport(report.getIdInternshipReport()))) && (report.getReport().length > config.getMaxFileSize())) {
			throw new Exception("O arquivo deve ter um tamanho máximo de " + StringUtils.getFormattedBytes(config.getMaxFileSize()) + ".");
		}
		
		try{
			ret = dao.save(idUser, report);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
		
		try {
			if(isInsert && (report.getFeedback() == ReportFeedback.NONE)) {
				EmailMessageBO bo = new EmailMessageBO();
				List<EmailMessageEntry<String, String>> keys = new ArrayList<EmailMessageEntry<String, String>>();
				Internship internship = new InternshipBO().findById(report.getInternship().getIdInternship());
				User manager = new UserBO().findManager(internship.getDepartment().getIdDepartment(), SystemModule.SIGES);
				
				keys.add(new EmailMessageEntry<String, String>("student", internship.getStudent().getName()));
				keys.add(new EmailMessageEntry<String, String>("supervisor", internship.getSupervisor().getName()));
				keys.add(new EmailMessageEntry<String, String>("manager", manager.getName()));
				keys.add(new EmailMessageEntry<String, String>("company", internship.getCompany().getName()));
				keys.add(new EmailMessageEntry<String, String>("type", (report.isFinalReport() ? "final" : "parcial")));
				
				if(report.getType() == ReportType.STUDENT) {
					bo.sendEmail(internship.getSupervisor().getIdUser(), MessageType.INTERNSHIPSTUDENTREPORTSUBMITTED, keys);
				} else if(report.getType() == ReportType.SUPERVISOR) {
					bo.sendEmail(manager.getIdUser(), MessageType.INTERNSHIPSUPERVISORREPORTSUBMITTED, keys);
				} else if(report.getType() == ReportType.COMPANY) {
					bo.sendEmail(manager.getIdUser(), MessageType.INTERNSHIPCOMPANYSUPERVISORREPORTSUBMITTED, keys);
				}
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		return ret;
	}

}
