package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.User;

public class AttendanceDAO {
	
	public Attendance findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT attendance.*, student.name AS studentName, supervisor.name AS supervisorName, proposal.title AS proposalTitle " +
					"FROM attendance INNER JOIN proposal ON proposal.idProposal=attendance.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=attendance.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=attendance.idSupervisor " +
					"WHERE idAttendance = ?");
		
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Attendance> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT attendance.*, student.name AS studentName, supervisor.name AS supervisorName, proposal.title AS proposalTitle " +
					"FROM attendance INNER JOIN proposal ON proposal.idProposal=attendance.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=attendance.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=attendance.idSupervisor " +
					"ORDER BY attendance.date DESC, attendance.startTime DESC");
			List<Attendance> list = new ArrayList<Attendance>();
			
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
	
	public List<Attendance> listByStudent(int idStudent) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT attendance.*, student.name AS studentName, supervisor.name AS supervisorName, proposal.title AS proposalTitle " +
					"FROM attendance INNER JOIN proposal ON proposal.idProposal=attendance.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=attendance.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=attendance.idSupervisor " +
					"WHERE attendance.idStudent = " + String.valueOf(idStudent) + " ORDER BY attendance.date DESC, attendance.startTime DESC");
			List<Attendance> list = new ArrayList<Attendance>();
			
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
	
	public List<Attendance> listByStudent(int idStudent, int idSupervisor, int idProposal, int stage) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT attendance.*, student.name AS studentName, supervisor.name AS supervisorName, proposal.title AS proposalTitle " +
					"FROM attendance INNER JOIN proposal ON proposal.idProposal=attendance.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=attendance.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=attendance.idSupervisor " +
					"WHERE attendance.idStudent=" + String.valueOf(idStudent) + " AND attendance.idSupervisor=" + String.valueOf(idSupervisor) + 
					" AND attendance.idProposal=" + String.valueOf(idProposal) + ((stage == 0) ? "" : " AND attendance.stage=" + String.valueOf(stage)) + 
					" ORDER BY attendance.date DESC, attendance.startTime DESC");
			List<Attendance> list = new ArrayList<Attendance>();
			
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
	
	public int save(Attendance attendance) throws SQLException{
		boolean insert = (attendance.getIdAttendance() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO attendance(idProposal, idSupervisor, idStudent, date, startTime, endTime, comments, nextMeeting, stage) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE attendance SET idProposal=?, idSupervisor=?, idStudent=?, date=?, startTime=?, endTime=?, comments=?, nextMeeting=?, stage=? WHERE idAttendance=?");
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
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					attendance.setIdAttendance(rs.getInt(1));
				}
			}
			
			return attendance.getIdAttendance();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean delete(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			return stmt.execute("DELETE FROM attendance WHERE idAttendance = " + String.valueOf(id));
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public AttendanceReport getReport(int idStudent, int idProposal, int idSupervisor, int stage) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
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
			
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT attendance.*, student.name AS studentName, supervisor.name AS supervisorName, " +
					"COALESCE(thesis.title, project.title, proposal.title) AS proposalTitle " +
					"FROM attendance INNER JOIN proposal ON proposal.idProposal=attendance.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=attendance.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=attendance.idSupervisor " +
					"LEFT JOIN project ON project.idproposal=proposal.idproposal " +
					"LEFT JOIN thesis ON thesis.idproject=project.idproject " +
					"WHERE attendance.idStudent = " + String.valueOf(idStudent) + " AND attendance.idProposal = " + String.valueOf(idProposal) + " AND attendance.idSupervisor = " + String.valueOf(idSupervisor) + " AND attendance.stage = " + String.valueOf(stage) + 
					" ORDER BY attendance.date, attendance.startTime");
			
			while(rs.next()){
				report.getAttendances().add(this.loadObject(rs));
			}
			
			return report;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<AttendanceReport> getAttendanceReport(int idDepartment, int semester, int year, int stage) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			List<AttendanceReport> list = new ArrayList<AttendanceReport>();
			Semester sem = new SemesterDAO().findBySemester(new CampusDAO().findByDepartment(idDepartment).getIdCampus(), semester, year);
			
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT proposal.idproposal, proposal.idstudent, student.name AS studentName, " +
					"CASE WHEN thesis.idthesis IS NOT NULL THEN thesis.title WHEN project.idproject IS NOT NULL THEN project.title ELSE proposal.title END AS title " +
					"FROM proposal INNER JOIN \"user\" student ON student.iduser=proposal.idstudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.iduser=proposal.idsupervisor " + 
					"LEFT JOIN project ON project.idproposal=proposal.idproposal " + 
					"LEFT JOIN thesis ON thesis.idproject=project.idproject " +
					"WHERE proposal.invalidated=0 AND (proposal.semester=" + String.valueOf(semester) + " OR project.semester=" + String.valueOf(semester) + " OR thesis.semester=" + String.valueOf(semester) + 
						") AND (proposal.year=" + String.valueOf(year) + " OR project.year=" + String.valueOf(year) + " OR thesis.year=" + String.valueOf(year) + ") " +
					" ORDER BY student.name");
			
			while(rs.next()){
				ProposalDAO dao = new ProposalDAO();
				List<User> supervisors = dao.listSupervisors(rs.getInt("idproposal"));
				
				for(User s : supervisors){
					for(int i = 1; i <= 2; i++){
						if((stage == 0) || (stage == i)){
							AttendanceReport report = new AttendanceReport();
							
							report.setStudent(rs.getString("studentName"));
							report.setTitle(rs.getString("title"));
							report.setSupervisor(s.getName());
							report.setStage(i);
							
							AttendanceReport rep = this.getReport(rs.getInt("idstudent"), rs.getInt("idproposal"), s.getIdUser(), i);
							
							for(Attendance attendance : rep.getAttendances()){
								if((attendance.getDate().after(sem.getStartDate()) || attendance.getDate().equals(sem.getStartDate())) && (attendance.getDate().before(sem.getEndDate()) || attendance.getDate().equals(sem.getEndDate()))){
									report.getAttendances().add(attendance);
								}
							}
							
							list.add(report);
						}
					}
				}
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
	
	private Attendance loadObject(ResultSet rs) throws SQLException{
		Attendance a = new Attendance();
		
		a.setIdAttendance(rs.getInt("idAttendance"));
		a.getProposal().setIdProposal(rs.getInt("idProposal"));
		a.getProposal().setTitle(rs.getString("proposalTitle"));
		a.getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		a.getSupervisor().setName(rs.getString("supervisorName"));
		a.getStudent().setIdUser(rs.getInt("idStudent"));
		a.getStudent().setName(rs.getString("studentName"));
		a.setDate(rs.getDate("date"));
		a.setStartTime(rs.getTime("startTime"));
		a.setEndTime(rs.getTime("endTime"));
		a.setComments(rs.getString("comments"));
		a.setNextMeeting(rs.getString("nextMeeting"));
		a.setStage(rs.getInt("stage"));
		
		return a;
	}

}
