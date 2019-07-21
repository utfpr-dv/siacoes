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
import br.edu.utfpr.dv.siacoes.model.ActivityScore;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission.ActivityFeedback;
import br.edu.utfpr.dv.siacoes.model.ActivityValidationReport;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.StudentActivityStatusReport;
import br.edu.utfpr.dv.siacoes.model.User;

public class ActivitySubmissionDAO {
	
	public ActivityFeedback getFeedback(int idActivitySubmission) throws SQLException{
		if(idActivitySubmission == 0){
			return ActivityFeedback.NONE;
		}
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT activitysubmission.feedback FROM activitysubmission WHERE idactivitysubmission=" + idActivitySubmission);
			
			if(rs.next()){
				return ActivityFeedback.valueOf(rs.getInt("feedback"));
			}else{
				return ActivityFeedback.NONE;
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
	
	public byte[] getFile(int idActivitySubmission) throws SQLException{
		if(idActivitySubmission == 0){
			return null;
		}
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT activitysubmission.file FROM activitysubmission WHERE idactivitysubmission=" + idActivitySubmission);
			
			if(rs.next()){
				return rs.getBytes("file");
			}else{
				return null;
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
	
	public List<ActivitySubmission> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
					"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, activitygroup.idactivitygroup, " +
					"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
					"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
					"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
					"ORDER BY activitysubmission.year DESC, activitysubmission.semester DESC");
			
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
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
	
	public List<ActivitySubmission> listByStudent(int idStudent, int idDepartment, int feedback, boolean loadFiles) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
					"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, activitygroup.idactivitygroup, " +
					"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
					"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
					"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
					"WHERE activitysubmission.idDepartment=" + String.valueOf(idDepartment) + " AND activitysubmission.idStudent=" + String.valueOf(idStudent) +
					((feedback < 0) ? "" : " AND activitysubmission.feedback=" + String.valueOf(feedback)) +
					" ORDER BY activitysubmission.year DESC, activitysubmission.semester DESC");
			
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, loadFiles));
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
	
	public List<ActivitySubmission> listWithNoFeedback(int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
					"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, activitygroup.idactivitygroup, " +
					"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
					"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
					"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
					"WHERE NOT EXISTS(SELECT idfinalsubmission FROM finalsubmission WHERE activitysubmission.idStudent=finalsubmission.idStudent AND activitysubmission.idDepartment=finalsubmission.idDepartment) " +
					"AND activitysubmission.feedback=0 AND activitysubmission.idDepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY activitysubmission.year DESC, activitysubmission.semester DESC");
			
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
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
	
	public List<ActivitySubmission> listWithNoFeedback2(int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
					"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, activitygroup.idactivitygroup, " +
					"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester, " +
					"CASE WHEN finaldocument.idProject IS NOT NULL THEN 2 WHEN proposal.idProposal IS NOT NULL THEN 1 ELSE 0 END AS stage " +
					"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
					"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
					"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
					"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
					"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
					"LEFT JOIN proposal ON (proposal.idStudent=activitysubmission.idStudent AND proposal.idDepartment=activitysubmission.idDepartment) " +
					"LEFT JOIN project ON project.idProposal=proposal.idProposal " +
					"LEFT JOIN finaldocument ON (finaldocument.idProject=project.idProject AND finaldocument.supervisorfeedback=" + String.valueOf(DocumentFeedback.APPROVED.getValue()) + ") " +
					"WHERE NOT EXISTS(SELECT idfinalsubmission FROM finalsubmission WHERE activitysubmission.idStudent=finalsubmission.idStudent AND activitysubmission.idDepartment=finalsubmission.idDepartment) " +
					"AND activitysubmission.feedback=0 AND activitysubmission.idDepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY stage DESC, activitygroup.sequence, activitysubmission.year DESC, activitysubmission.semester DESC");
			
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			
			while(rs.next()){
				ActivitySubmission a = this.loadObject(rs, false);
				
				a.setStage(rs.getInt("stage"));
				
				list.add(a);
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
	
	public ActivitySubmission findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT activitysubmission.*, \"user\".name AS studentName, feedbackUser.name AS feedbackUserName, " + 
				"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, activitygroup.idactivitygroup, " +
				"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
				"FROM activitysubmission INNER JOIN \"user\" ON \"user\".idUser=activitysubmission.idStudent " +
				"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"LEFT JOIN \"user\" feedbackUser ON feedbackUser.idUser=activitysubmission.idfeedbackuser " +
				"WHERE activitysubmission.idActivitySubmission=?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs, true);
			}else{
				return null;
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
	
	public int save(int idUser, ActivitySubmission submission) throws SQLException{
		boolean insert = (submission.getIdActivitySubmission() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO activitysubmission(idStudent, idDepartment, idActivity, semester, year, submissionDate, file, amount, feedback, feedbackDate, validatedAmount, idfeedbackuser, comments, description, feedbackreason) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE activitysubmission SET idStudent=?, idDepartment=?, idActivity=?, semester=?, year=?, submissionDate=?, file=?, amount=?, feedback=?, feedbackDate=?, validatedAmount=?, idfeedbackuser=?, comments=?, description=?, feedbackreason=? WHERE idActivitySubmission=?");
			}
	
			stmt.setInt(1, submission.getStudent().getIdUser());
			stmt.setInt(2, submission.getDepartment().getIdDepartment());
			stmt.setInt(3, submission.getActivity().getIdActivity());
			stmt.setInt(4, submission.getSemester());
			stmt.setInt(5, submission.getYear());
			stmt.setDate(6, new java.sql.Date(submission.getSubmissionDate().getTime()));
			stmt.setBytes(7, submission.getFile());
			stmt.setDouble(8, submission.getAmount());
			stmt.setInt(9, submission.getFeedback().getValue());
			if(submission.getFeedback() == ActivityFeedback.NONE){
				stmt.setNull(10, Types.DATE);
			}else{
				stmt.setDate(10, new java.sql.Date(submission.getFeedbackDate().getTime()));
			}
			stmt.setDouble(11, submission.getValidatedAmount());
			if((submission.getFeedbackUser() == null) || (submission.getFeedbackUser().getIdUser() == 0)){
				stmt.setNull(12, Types.INTEGER);
			}else{
				stmt.setInt(12, submission.getFeedbackUser().getIdUser());
			}
			stmt.setString(13, submission.getComments());
			stmt.setString(14, submission.getDescription());
			stmt.setString(15, submission.getFeedbackReason());
			
			if(!insert){
				stmt.setInt(16, submission.getIdActivitySubmission());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					submission.setIdActivitySubmission(rs.getInt(1));
				}
				
				new UpdateEvent(conn).registerInsert(idUser, submission);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, submission);
			}
			
			return submission.getIdActivitySubmission();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ActivitySubmission loadObject(ResultSet rs, boolean loadFile) throws SQLException{
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
		submission.setAmount(rs.getDouble("amount"));
		submission.setFeedback(ActivityFeedback.valueOf(rs.getInt("feedback")));
		submission.setFeedbackDate(rs.getDate("feedbackDate"));
		submission.setValidatedAmount(rs.getDouble("validatedAmount"));
		submission.setComments(rs.getString("comments"));
		submission.setFeedbackReason(rs.getString("feedbackreason"));
		submission.getActivity().setDescription(rs.getString("activityDescription"));
		submission.getActivity().getGroup().setIdActivityGroup(rs.getInt("idactivitygroup"));
		submission.getActivity().getGroup().setSequence(rs.getInt("groupSequence"));
		submission.getActivity().setScore(rs.getDouble("score"));
		submission.getActivity().getUnit().setFillAmount(rs.getInt("fillAmount") == 1);
		submission.getActivity().getUnit().setDescription(rs.getString("unit"));
		submission.getActivity().setMaximumInSemester(rs.getDouble("maximumInSemester"));
		
		if(loadFile) {
			submission.setFile(rs.getBytes("file"));
		}
		
		return submission;
	}
	
	public boolean delete(int idUser, int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ActivitySubmission submission = this.findById(id);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			boolean ret = stmt.execute("DELETE FROM activitysubmission WHERE idActivitySubmission = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, submission);
			
			return ret;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<User> listFeedbackUsers(int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<User> list = new ArrayList<User>();
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT DISTINCT \"user\".iduser, \"user\".name " +
					"FROM \"user\" INNER JOIN activitysubmission ON activitysubmission.idfeedbackuser=\"user\".iduser " +
					"WHERE activitysubmission.iddepartment=" + String.valueOf(idDepartment) +
					" ORDER BY \"user\".name");
			
			while(rs.next()){
				User user = new User();
				
				user.setIdUser(rs.getInt("iduser"));
				user.setName(rs.getString("name"));
				
				list.add(user);
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
	
	public List<ActivityValidationReport> getActivityValidationReport(int idDepartment, int idFeedbackUser) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<ActivityValidationReport> list = new ArrayList<ActivityValidationReport>();
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT activitysubmission.idactivity, activity.description, activitygroup.sequence AS group, COUNT(*) AS submitted, " +
					"SUM(CASE WHEN activitysubmission.feedback=1 THEN 1 ELSE 0 END) AS validated " +
					"FROM activitysubmission INNER JOIN activity ON activity.idactivity=activitysubmission.idactivity " +
					"INNER JOIN activitygroup ON activitygroup.idactivitygroup=activity.idactivitygroup " +
					"WHERE activitysubmission.feedback <> 0 AND activitysubmission.iddepartment=" + String.valueOf(idDepartment) + 
					(idFeedbackUser > 0 ? " AND activitysubmission.idfeedbackuser=" + String.valueOf(idFeedbackUser) : "") +
					" GROUP BY activitysubmission.idactivity, activity.description, activitygroup.sequence " +
					"ORDER BY submitted DESC, validated DESC");
			
			while(rs.next()){
				ActivityValidationReport report = new ActivityValidationReport();
				
				report.setIdActivity(rs.getInt("idactivity"));
				report.setDescription(rs.getString("description"));
				report.setGroup(rs.getInt("group"));
				report.setSubmitted(rs.getInt("submitted"));
				report.setValidated(rs.getInt("validated"));
				
				list.add(report);
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
	
	public List<StudentActivityStatusReport> getStudentActivityStatusReport(int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT \"user\".iduser, \"user\".name AS studentName, userDepartment.registerSemester, userDepartment.registerYear, \"user\".studentCode, " +
					"CASE WHEN finaldocument.idProject IS NOT NULL THEN 2 WHEN proposal.idProposal IS NOT NULL THEN 1 ELSE 0 END AS stage " +
					"FROM \"user\" INNER JOIN userDepartment ON \"user\".idUser=userDepartment.idUser " +
					"LEFT JOIN proposal ON (proposal.idStudent=\"user\".idUser AND proposal.idDepartment=userDepartment.idDepartment AND proposal.invalidated=0) " +
					"LEFT JOIN project ON project.idProposal=proposal.idProposal " +
					"LEFT JOIN finaldocument ON (finaldocument.idProject=project.idProject AND finaldocument.supervisorfeedback=" + String.valueOf(DocumentFeedback.APPROVED.getValue()) + ") " +
					"WHERE userDepartment.idDepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY stage DESC, \"user\".name");
			
			List<StudentActivityStatusReport> list = new ArrayList<StudentActivityStatusReport>();
			
			while(rs.next()){
				StudentActivityStatusReport a = new StudentActivityStatusReport();
				
				a.setIdUser(rs.getInt("iduser"));
				a.setStudentName(rs.getString("studentName"));
				a.setRegisterSemester(rs.getInt("registerSemester"));
				a.setRegisterYear(rs.getInt("registerYear"));
				a.setStudentCode(rs.getString("studentCode"));
				a.setStage(rs.getInt("stage"));
				
				list.add(a);
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
	
	public List<ActivityScore> getActivityScore(int idDepartment, int initialYear, int finalYear) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT SUM(activitysubmission.validatedamount) AS score, activity.description, activitysubmission.idactivity FROM activitysubmission " + 
					"INNER JOIN activity ON activity.idactivity=activitysubmission.idactivity " + 
					"WHERE activitysubmission.iddepartment=" + String.valueOf(idDepartment) + " AND activitysubmission.feedback=" + String.valueOf(DocumentFeedback.APPROVED.getValue()) + 
					" AND YEAR(activitysubmission.submissiondate) BETWEEN " + String.valueOf(initialYear) + " AND " + String.valueOf(finalYear) +
					" GROUP BY activitysubmission.idactivity, activity.description " + 
					"ORDER BY score DESC");
			
			List<ActivityScore> list = new ArrayList<ActivityScore>();
			
			while(rs.next()){
				ActivityScore a = new ActivityScore();
				
				a.setIdActivity(rs.getInt("idactivity"));
				a.setActivity(rs.getString("description"));
				a.setScore(rs.getDouble("score"));
				
				list.add(a);
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
	
	public long getTotalSubmissions() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(idactivitysubmission) AS total FROM activitysubmission");
			
			if(rs.next()) {
				return rs.getLong("total");
			} else {
				return 0;
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
	
}
