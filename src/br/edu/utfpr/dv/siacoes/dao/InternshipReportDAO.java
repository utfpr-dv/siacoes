package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportType;

public class InternshipReportDAO {
	
	public Connection conn;
	
	public InternshipReportDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public InternshipReportDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();
		}else{
			this.conn = conn;	
		}
	}
	
	public List<InternshipReport> listAll() throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM internshipreport ORDER BY date DESC");
		
		List<InternshipReport> list = new ArrayList<InternshipReport>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<InternshipReport> listByInternship(int idInternship) throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM internshipreport WHERE idinternship=" + String.valueOf(idInternship) + " ORDER BY date DESC");
		
		List<InternshipReport> list = new ArrayList<InternshipReport>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public InternshipReport findById(int id) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM internshipreport WHERE idinternshipreport=?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public boolean hasReport(int idInternship, ReportType type) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM internshipreport WHERE idinternship=? AND type=?");
		
		stmt.setInt(1, idInternship);
		stmt.setInt(2, type.getValue());
		
		ResultSet rs = stmt.executeQuery();
		
		return rs.next();
	}
	
	public int save(InternshipReport report) throws SQLException{
		boolean insert = (report.getIdInternshipReport() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = this.conn.prepareStatement("INSERT INTO internshipreport(idinternship, report, type, date) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = this.conn.prepareStatement("UPDATE internshipreport SET idinternship=?, report=?, type=?, date=? WHERE idinternshipreport=?");
		}
		
		stmt.setInt(1, report.getInternship().getIdInternship());
		stmt.setBytes(2, report.getReport());
		stmt.setInt(3, report.getType().getValue());
		stmt.setDate(4, new java.sql.Date(report.getDate().getTime()));
		
		if(!insert){
			stmt.setInt(5, report.getIdInternshipReport());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				report.setIdInternshipReport(rs.getInt(1));
			}
		}
		
		return report.getIdInternshipReport();
	}
	
	private InternshipReport loadObject(ResultSet rs) throws SQLException{
		InternshipReport report = new InternshipReport();
		
		report.setIdInternshipReport(rs.getInt("idinternshipreport"));
		report.getInternship().setIdInternship(rs.getInt("idinternship"));
		report.setReport(rs.getBytes("report"));
		report.setType(ReportType.valueOf(rs.getInt("type")));
		report.setDate(rs.getDate("date"));
		
		return report;
	}

}
