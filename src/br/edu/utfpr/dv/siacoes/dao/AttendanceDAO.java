package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Proposal;

public class AttendanceDAO {
	
	public Attendance findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM attendance WHERE idAttendance = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<Attendance> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM attendance ORDER BY date DESC, startTime DESC");
		List<Attendance> list = new ArrayList<Attendance>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<Attendance> listByStudent(int idStudent) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM attendance WHERE idStudent = " + String.valueOf(idStudent) + " ORDER BY date DESC, startTime DESC");
		List<Attendance> list = new ArrayList<Attendance>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<Attendance> listByStudent(int idStudent, int idSupervisor, int idProposal, int stage) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM attendance WHERE idStudent = " + String.valueOf(idStudent) + " AND idSupervisor = " + String.valueOf(idSupervisor) + " AND idProposal = " + String.valueOf(idProposal) + " ORDER BY date DESC, startTime DESC");
		List<Attendance> list = new ArrayList<Attendance>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public int save(Attendance attendance) throws SQLException{
		boolean insert = (attendance.getIdAttendance() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO attendance(idProposal, idSupervisor, idStudent, date, startTime, endTime, comments, nextMeeting, stage) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE attendance SET idProposal=?, idSupervisor=?, idStudent=?, date=?, startTime=?, endTime=?, comments=?, nextMeeting=?, stage=? WHERE idAttendance=?");
		}
		
		stmt.setInt(1, attendance.getProposal().getIdProposal());
		stmt.setInt(2, attendance.getSupervisor().getIdUser());
		stmt.setInt(3, attendance.getStudent().getIdUser());
		stmt.setDate(4, new java.sql.Date(attendance.getDate().getTime()));
		stmt.setTime(5, new java.sql.Time(attendance.getStartTime().getTime()));
		stmt.setTime(6, new java.sql.Time(attendance.getEndTime().getTime()));
		stmt.setString(7, attendance.getComments());
		stmt.setString(8, attendance.getNextMeeting());
		stmt.setInt(9, attendance.getStage());
		
		if(!insert){
			stmt.setInt(10, attendance.getIdAttendance());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				attendance.setIdAttendance(rs.getInt(1));
			}
		}
		
		return attendance.getIdAttendance();
	}
	
	public boolean delete(int id) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		return stmt.execute("DELETE FROM attendance WHERE idAttendance = " + String.valueOf(id));
	}
	
	public AttendanceReport getReport(int idStudent, int idProposal, int idSupervisor, int stage) throws SQLException{
		ProposalDAO dao = new ProposalDAO();
		Proposal proposal = dao.findById(idProposal);
		
		AttendanceReport report = new AttendanceReport();
		
		report.setStage(stage);
		report.setTitle(proposal.getTitle());
		report.setStudent(proposal.getStudent().getName());
		report.setSupervisor(proposal.getSupervisor().getName());
		
		if(stage == 1){
			
		}else{
			
		}
		
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM attendance WHERE idStudent = " + String.valueOf(idStudent) + " AND idProposal = " + String.valueOf(idProposal) + " AND idSupervisor = " + String.valueOf(idSupervisor) + " AND stage = " + String.valueOf(stage) + " ORDER BY date, startTime");
		
		while(rs.next()){
			report.getAttendances().add(this.loadObject(rs));
		}
		
		return report;
	}
	
	private Attendance loadObject(ResultSet rs) throws SQLException{
		Attendance a = new Attendance();
		
		a.setIdAttendance(rs.getInt("idAttendance"));
		a.getProposal().setIdProposal(rs.getInt("idProposal"));
		a.getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		a.getStudent().setIdUser(rs.getInt("idStudent"));
		a.setDate(rs.getDate("date"));
		a.setStartTime(rs.getTime("startTime"));
		a.setEndTime(rs.getTime("endTime"));
		a.setComments(rs.getString("comments"));
		a.setNextMeeting(rs.getString("nextMeeting"));
		a.setStage(rs.getInt("stage"));
		
		return a;
	}

}
