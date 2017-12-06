package br.edu.utfpr.dv.siacoes.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.InternshipReportDAO;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;

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
	
	public boolean hasReport(int idInternship, ReportType type) throws Exception{
		try{
			InternshipReportDAO dao = new InternshipReportDAO(this.conn);
			
			return dao.hasReport(idInternship, type);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int save(InternshipReport report) throws Exception{
		if((report.getInternship() == null) || (report.getInternship().getIdInternship() == 0)){
			throw new Exception("Informe o estágio do relatório.");
		}
		if(report.getReport() == null){
			throw new Exception("Faça o upload do relatório.");
		}
		
		try{
			InternshipReportDAO dao = new InternshipReportDAO(this.conn);
			
			return dao.save(report);
		}catch(SQLException e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}

}
