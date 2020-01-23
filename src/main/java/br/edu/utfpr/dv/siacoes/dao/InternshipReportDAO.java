package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.InternshipReport;
import br.edu.utfpr.dv.siacoes.model.InternshipReport.ReportFeedback;
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
	
	public byte[] getReport(int id) throws SQLException{
		if(id == 0){
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internshipreport.report FROM internshipreport WHERE idinternshipreport=" + id);
			
			if(rs.next()){
				return rs.getBytes("report");
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<InternshipReport> listAll() throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM internshipreport ORDER BY date DESC");
			
			List<InternshipReport> list = new ArrayList<InternshipReport>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<InternshipReport> listByInternship(int idInternship) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM internshipreport WHERE idinternship=" + String.valueOf(idInternship) + " ORDER BY date");
			
			List<InternshipReport> list = new ArrayList<InternshipReport>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public InternshipReport findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT * FROM internshipreport WHERE idinternshipreport=?");
			
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public boolean hasReport(int idInternship, ReportType type, ReportFeedback feedback) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT * FROM internshipreport WHERE idinternship=? AND type=? AND feedback=?");
			
			stmt.setInt(1, idInternship);
			stmt.setInt(2, type.getValue());
			stmt.setInt(3, feedback.getValue());
			
			rs = stmt.executeQuery();
			
			return rs.next();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public boolean hasReport(int idInternship, ReportType type) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT * FROM internshipreport WHERE idinternship=? AND type=?");
			
			stmt.setInt(1, idInternship);
			stmt.setInt(2, type.getValue());
			
			rs = stmt.executeQuery();
			
			return rs.next();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public boolean validateReport(int idUser, InternshipReport report) throws SQLException {
		PreparedStatement stmt = this.conn.prepareStatement("UPDATE internshipreport SET feedback=?, feedbackdate=?, idfeedbackuser=? WHERE idinternshipreport=?");
		
		try {
			stmt.setInt(1, report.getFeedback().getValue());
			
			if(report.getFeedback() == ReportFeedback.NONE) {
				stmt.setNull(2, Types.TIMESTAMP);
				stmt.setNull(3, Types.INTEGER);
			} else {
				stmt.setTimestamp(2, new java.sql.Timestamp(report.getFeedbackDate().getTime()));
				stmt.setInt(3, report.getFeedbackUser().getIdUser());
			}
			
			stmt.setInt(4, report.getIdInternshipReport());
			
			boolean ret = stmt.execute();
			
			new UpdateEvent(this.conn).registerUpdate(idUser, report);
			
			return ret;
		} finally {
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public int save(int idUser, InternshipReport report) throws SQLException{
		boolean insert = (report.getIdInternshipReport() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO internshipreport(idinternship, report, type, date, finalreport, feedback, feedbackdate, idfeedbackuser) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE internshipreport SET idinternship=?, report=?, type=?, date=?, finalreport=? WHERE idinternshipreport=?");
			}
			
			stmt.setInt(1, report.getInternship().getIdInternship());
			stmt.setBytes(2, report.getReport());
			stmt.setInt(3, report.getType().getValue());
			stmt.setDate(4, new java.sql.Date(report.getDate().getTime()));
			stmt.setInt(5, (report.isFinalReport() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(6, report.getIdInternshipReport());
			}else {
				stmt.setInt(6, report.getFeedback().getValue());
				
				if(report.getFeedback() == ReportFeedback.NONE) {
					stmt.setNull(7, Types.TIMESTAMP);
					stmt.setNull(8, Types.INTEGER);
				} else {
					stmt.setTimestamp(7, new java.sql.Timestamp(report.getFeedbackDate().getTime()));
					stmt.setInt(8, report.getFeedbackUser().getIdUser());
				}
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					report.setIdInternshipReport(rs.getInt(1));
				}

				new UpdateEvent(this.conn).registerInsert(idUser, report);
			} else {
				new UpdateEvent(this.conn).registerUpdate(idUser, report);
			}
			
			return report.getIdInternshipReport();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private InternshipReport loadObject(ResultSet rs) throws SQLException{
		InternshipReport report = new InternshipReport();
		
		report.setIdInternshipReport(rs.getInt("idinternshipreport"));
		report.getInternship().setIdInternship(rs.getInt("idinternship"));
		report.setReport(rs.getBytes("report"));
		report.setType(ReportType.valueOf(rs.getInt("type")));
		report.setDate(rs.getDate("date"));
		report.setFinalReport(rs.getInt("finalreport") == 1);
		report.setFeedback(ReportFeedback.valueOf(rs.getInt("feedback")));
		report.setFeedbackDate(rs.getTimestamp("feedbackdate"));
		report.getFeedbackUser().setIdUser(rs.getInt("idfeedbackuser"));
		
		return report;
	}

}
