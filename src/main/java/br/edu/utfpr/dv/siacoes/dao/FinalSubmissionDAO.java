package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.FinalSubmission;

public class FinalSubmissionDAO {
	
	public List<FinalSubmission> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT finalsubmission.*, student.name AS studentName, feedbackUser.name AS feedbackUserName " +
					"FROM finalsubmission INNER JOIN \"user\" student ON student.idUser=finalsubmission.idstudent " +
					"INNER JOIN \"user\" feedbackUser ON feedbackUser.idUser=finalsubmission.idFeedbackUser " +
					"ORDER BY student.name");
			
			List<FinalSubmission> list = new ArrayList<FinalSubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<FinalSubmission> listByDepartment(int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT finalsubmission.*, student.name AS studentName, feedbackUser.name AS feedbackUserName " +
					"FROM finalsubmission INNER JOIN \"user\" student ON student.idUser=finalsubmission.idstudent " +
					"INNER JOIN \"user\" feedbackUser ON feedbackUser.idUser=finalsubmission.idFeedbackUser " +
					"WHERE finalsubmission.iddepartment=" + String.valueOf(idDepartment) + " ORDER BY finalsubmission.date DESC");
			
			List<FinalSubmission> list = new ArrayList<FinalSubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public FinalSubmission findById(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT finalsubmission.*, student.name AS studentName, feedbackUser.name AS feedbackUserName " +
					"FROM finalsubmission INNER JOIN \"user\" student ON student.idUser=finalsubmission.idstudent " +
					"INNER JOIN \"user\" feedbackUser ON feedbackUser.idUser=finalsubmission.idFeedbackUser " +
					"WHERE finalsubmission.idfinalsubmission=" + String.valueOf(id));
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return new FinalSubmission();
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public FinalSubmission findByStudent(int idStudent, int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT finalsubmission.*, student.name AS studentName, feedbackUser.name AS feedbackUserName " +
					"FROM finalsubmission INNER JOIN \"user\" student ON student.idUser=finalsubmission.idstudent " +
					"INNER JOIN \"user\" feedbackUser ON feedbackUser.idUser=finalsubmission.idFeedbackUser " +
					"WHERE finalsubmission.idStudent=" + String.valueOf(idStudent) + " AND finalsubmission.iddepartment=" + String.valueOf(idDepartment));
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return new FinalSubmission();
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean studentHasSubmission(int idStudent, int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT finalsubmission.idfinalsubmission FROM finalsubmission " +
					"WHERE finalsubmission.idStudent=" + String.valueOf(idStudent) + " AND finalsubmission.iddepartment=" + String.valueOf(idDepartment));
			
			return rs.next();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(int idUser, FinalSubmission submission) throws SQLException{
		boolean insert = (submission.getIdFinalSubmission() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO finalsubmission(idDepartment, idStudent, idFeedbackUser, finalScore, date, report) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE finalsubmission SET idDepartment=?, idStudent=?, idFeedbackUser=?, finalScore=?, date=?, report=? WHERE idFinalSubmission=?");
			}
			
			stmt.setInt(1, submission.getDepartment().getIdDepartment());
			stmt.setInt(2, submission.getStudent().getIdUser());
			stmt.setInt(3, submission.getFeedbackUser().getIdUser());
			stmt.setDouble(4, submission.getFinalScore());
			stmt.setDate(5, new java.sql.Date(submission.getDate().getTime()));
			stmt.setBytes(6, submission.getReport());
			
			if(!insert){
				stmt.setInt(7, submission.getIdFinalSubmission());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					submission.setIdFinalSubmission(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, submission);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, submission);
			}
			
			return submission.getIdFinalSubmission();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private FinalSubmission loadObject(ResultSet rs) throws SQLException{
		FinalSubmission submission = new FinalSubmission();

		submission.setIdFinalSubmission(rs.getInt("idFinalSubmission"));
		submission.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		submission.getStudent().setIdUser(rs.getInt("idStudent"));
		submission.getStudent().setName(rs.getString("studentName"));
		submission.getFeedbackUser().setIdUser(rs.getInt("idFeedbackUser"));
		submission.getFeedbackUser().setName(rs.getString("feedbackUserName"));
		submission.setFinalScore(rs.getDouble("finalScore"));
		submission.setDate(rs.getDate("date"));
		submission.setReport(rs.getBytes("report"));
		
		return submission;
	}

}
