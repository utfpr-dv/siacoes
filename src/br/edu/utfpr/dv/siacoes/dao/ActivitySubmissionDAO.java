package br.edu.utfpr.dv.siacoes.dao;

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
	
	public List<ActivitySubmission> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT activitysubmission.*, user.name AS studentName, " + 
				"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
				"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
				"FROM activitysubmission INNER JOIN user ON user.idUser=activitysubmission.idStudent " +
				"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"ORDER BY year DESC, semester DESC");
		
		List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<ActivitySubmission> listByStudent(int idStudent, int idDepartment) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT activitysubmission.*, user.name AS studentName, " + 
				"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
				"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
				"FROM activitysubmission INNER JOIN user ON user.idUser=activitysubmission.idStudent " +
				"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"WHERE activitysubmission.idDepartment=" + String.valueOf(idDepartment) + " AND idStudent=" + String.valueOf(idStudent) + " ORDER BY year DESC, semester DESC");
		
		List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<ActivitySubmission> listWithNoFeedback(int idDepartment) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT activitysubmission.*, user.name AS studentName, " + 
				"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
				"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
				"FROM activitysubmission INNER JOIN user ON user.idUser=activitysubmission.idStudent " +
				"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"WHERE feedback=0 AND activitysubmission.idDepartment=" + String.valueOf(idDepartment) + " ORDER BY year DESC, semester DESC");
		
		List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public ActivitySubmission findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT activitysubmission.*, user.name AS studentName, " + 
				"activity.description AS activityDescription, activitygroup.sequence AS groupSequence, " +
				"activity.score, activityunit.fillAmount, activityunit.description AS unit, activity.maximumInSemester " + 
				"FROM activitysubmission INNER JOIN user ON user.idUser=activitysubmission.idStudent " +
				"INNER JOIN activity ON activity.idActivity=activitysubmission.idActivity " + 
				"INNER JOIN activitygroup ON activitygroup.idActivityGroup=activity.idActivityGroup " +
				"INNER JOIN activityunit ON activityunit.idActivityUnit=activity.idActivityUnit " +
				"WHERE idActivitySubmission=?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public int save(ActivitySubmission submission) throws SQLException{
		boolean insert = (submission.getIdActivitySubmission() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO activitysubmission(idStudent, idDepartment, idActivity, semester, year, submissionDate, file, fileType, amount, feedback, feedbackDate, validatedAmount) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE activitysubmission SET idStudent=?, idDepartment=?, idActivity=?, semester=?, year=?, submissionDate=?, file=?, fileType=?, amount=?, feedback=?, feedbackDate=?, validatedAmount=? WHERE idActivitySubmission=?");
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
		
		if(!insert){
			stmt.setInt(13, submission.getIdActivitySubmission());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				submission.setIdActivitySubmission(rs.getInt(1));
			}
		}
		
		return submission.getIdActivitySubmission();
	}
	
	private ActivitySubmission loadObject(ResultSet rs) throws SQLException{
		ActivitySubmission submission = new ActivitySubmission();
		
		submission.setIdActivitySubmission(rs.getInt("idActivitySubmission"));
		submission.getStudent().setIdUser(rs.getInt("idStudent"));
		submission.getStudent().setName(rs.getString("studentName"));
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
		submission.getActivity().setDescription(rs.getString("activityDescription"));
		submission.getActivity().getGroup().setSequence(rs.getInt("groupSequence"));
		submission.getActivity().setScore(rs.getDouble("score"));
		submission.getActivity().getUnit().setFillAmount(rs.getInt("fillAmount") == 1);
		submission.getActivity().getUnit().setDescription(rs.getString("unit"));
		submission.getActivity().setMaximumInSemester(rs.getDouble("maximumInSemester"));
		
		return submission;
	}
	
	public boolean delete(int id) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		return stmt.execute("DELETE FROM activitysubmission WHERE idActivitySubmission = " + String.valueOf(id));
	}
	
}
