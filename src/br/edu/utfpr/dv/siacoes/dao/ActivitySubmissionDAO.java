package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class ActivitySubmissionDAO {
	
	public ActivityFeedback getFeedback(int idActivitySubmission) throws SQLException{
		if(idActivitySubmission == 0){
			return ActivityFeedback.NONE;
		}
		
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT activitysubmission.feedback FROM activitysubmission WHERE idactivitysubmission=" + idActivitySubmission);
			
			if(rs.next()){
				return ActivityFeedback.valueOf(rs.getInt("feedback"));
			}else{
				return ActivityFeedback.NONE;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<ActivitySubmission> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
					"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
					"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
					"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
					"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
					"ORDER BY activitysubmission.year DESC, activitysubmission.semester DESC");
			
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<ActivitySubmission> listByStudent(int idStudent, int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
					"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
					"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
					"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
					"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
					"WHERE activitysubmission.idDepartment=" + String.valueOf(idDepartment) + " AND activitysubmission.idStudent=" + String.valueOf(idStudent) + 
					" ORDER BY activitysubmission.year DESC, activitysubmission.semester DESC");
			
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<ActivitySubmission> listWithNoFeedback(int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
					"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
					"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
					"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
					"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
					"WHERE activitysubmission.feedback=0 AND activitysubmission.idDepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY activitysubmission.year DESC, activitysubmission.semester DESC");
			
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public ActivitySubmission findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
				"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
				"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
				"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
				"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
				"WHERE activitysubmission.idActivitySubmission=?");
		
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(ActivitySubmission submission) throws SQLException{
		boolean insert = (submission.getIdActivitySubmission() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO activitysubmission(idStudent, idDepartment, idActivity, semester, year, submissionDate, file, fileType, amount, feedback, feedbackDate, validatedAmount, idfeedbackuser, comments, description) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE activitysubmission SET idStudent=?, idDepartment=?, idActivity=?, semester=?, year=?, submissionDate=?, file=?, fileType=?, amount=?, feedback=?, feedbackDate=?, validatedAmount=?, idfeedbackuser=?, comments=?, description=? WHERE idActivitySubmission=?");
			}
	
			stmt.setInt(1, submission.getStudent().getIdUser());
			stmt.setInt(2, submission.getDepartment().getIdDepartment());
			stmt.setInt(3, submission.getActivity().getIdActivity());
			stmt.setInt(4, submission.getSemester());
			stmt.setInt(5, submission.getYear());
			stmt.setDate(6, new java.sql.Date(submission.getSubmissionDate().getTime()));
			stmt.setBytes(7, submission.getFile());
			stmt.setInt(8, submission.getFileType().getValue());
			stmt.setDouble(9, submission.getAmount());
			stmt.setInt(10, submission.getFeedback().getValue());
			if(submission.getFeedback() == ActivityFeedback.NONE){
				stmt.setNull(11, Types.DATE);
			}else{
				stmt.setDate(11, new java.sql.Date(submission.getFeedbackDate().getTime()));
			}
			stmt.setDouble(12, submission.getValidatedAmount());
			if((submission.getFeedbackUser() == null) || (submission.getFeedbackUser().getIdUser() == 0)){
				stmt.setNull(13, Types.INTEGER);
			}else{
				stmt.setInt(13, submission.getFeedbackUser().getIdUser());
			}
			stmt.setString(14, submission.getComments());
			stmt.setString(15, submission.getDescription());
			
			if(!insert){
				stmt.setInt(16, submission.getIdActivitySubmission());
			}
			
			stmt.execute();
			
			if(insert){
				ResultSet rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					submission.setIdActivitySubmission(rs.getInt(1));
				}
			}
			
			return submission.getIdActivitySubmission();
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ActivitySubmission loadObject(ResultSet rs) throws SQLException{
		ActivitySubmission submission = new ActivitySubmission();
		
		submission.setIdActivitySubmission(rs.getInt("idActivitySubmission"));
		submission.setDescription(rs.getString("description"));
		submission.getStudent().setIdUser(rs.getInt("idStudent"));
		submission.getStudent().setName(rs.getString("studentName"));
		submission.getFeedbackUser().setIdUser(rs.getInt("idFeedbackUser"));
		if(submission.getFeedbackUser().getIdUser() != 0){
			submission.getFeedbackUser().setName(rs.getString("feedbackUserName"));	
		}
		submission.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		submission.getActivity().setIdActivity(rs.getInt("idActivity"));
		submission.setSemester(rs.getInt("semester"));
		submission.setYear(rs.getInt("year"));
		submission.setSubmissionDate(rs.getDate("submissionDate"));
		submission.setFile(rs.getBytes("file"));
		submission.setFileType(DocumentType.valueOf(rs.getInt("fileType")));
		submission.setAmount(rs.getDouble("amount"));
		submission.setFeedback(ActivityFeedback.valueOf(rs.getInt("feedback")));
		submission.setFeedbackDate(rs.getDate("feedbackDate"));
		submission.setValidatedAmount(rs.getDouble("validatedAmount"));
		submission.setComments(rs.getString("comments"));
		submission.getActivity().setDescription(rs.getString("activityDescription"));
		submission.getActivity().getGroup().setSequence(rs.getInt("groupSequence"));
		submission.getActivity().setScore(rs.getDouble("score"));
		submission.getActivity().getUnit().setFillAmount(rs.getInt("fillAmount") == 1);
		submission.getActivity().getUnit().setDescription(rs.getString("unit"));
		submission.getActivity().setMaximumInSemester(rs.getDouble("maximumInSemester"));
		
		return submission;
	}
	
	public boolean delete(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			return stmt.execute("DELETE FROM activitysubmission WHERE idActivitySubmission = " + String.valueOf(id));
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
}
