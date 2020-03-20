package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.AttendanceReport;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

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
	
	public List<Attendance> listByGroup(int idGroup) throws SQLException{
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
					"WHERE attendance.idGroup = " + String.valueOf(idGroup) + " ORDER BY attendance.date DESC, attendance.startTime DESC");
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
	
	public int save(int idUser, Attendance attendance) throws SQLException{
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
				
				new UpdateEvent(conn).registerInsert(idUser, attendance);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, attendance);
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
	
	public boolean delete(int idUser, int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		Attendance attendance = this.findById(id);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			boolean ret = stmt.execute("DELETE FROM attendance WHERE idAttendance = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, attendance);
			
			return ret;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int createGroup(int idStudent, int idProposal, int idSupervisor, int stage) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			int idGroup = 0;
			
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO attendancegroup(idProposal, idSupervisor, stage) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			stmt.setInt(1, idProposal);
			stmt.setInt(2, idSupervisor);
			stmt.setInt(3, stage);
			
			stmt.execute();
			
			rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				idGroup = rs.getInt(1);
			}
			
			rs.close();
			stmt.close();
			
			stmt = conn.prepareStatement("UPDATE attendance SET idGroup=? WHERE idProposal=? AND idSupervisor=? AND stage=?");
			
			stmt.setInt(1, idGroup);
			stmt.setInt(2, idProposal);
			stmt.setInt(3, idSupervisor);
			stmt.setInt(4, stage);
			
			stmt.execute();
			
			/*stmt.close();
			
			stmt = conn.prepareStatement("DELETE FROM attendancegroup WHERE idGroup <> ? AND idProposal=? AND idSupervisor=? AND stage=?");
			
			stmt.setInt(1, idGroup);
			stmt.setInt(2, idProposal);
			stmt.setInt(3, idSupervisor);
			stmt.setInt(4, stage);
			
			stmt.execute();*/
			
			conn.commit();
			
			return idGroup;
		} catch (SQLException e) {
			conn.rollback();
			
			throw e;
		} finally {
			conn.setAutoCommit(true);
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public AttendanceReport getReport(int idGroup) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			AttendanceReport report = new AttendanceReport();
			int idProposal = 0;
			
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM attendancegroup WHERE idgroup=" + String.valueOf(idGroup));
			
			if(rs.next()) {
				report.setStage(rs.getInt("stage"));
				idProposal = rs.getInt("idProposal");
				report.setIdSupervisor(rs.getInt("idSupervisor"));
			}
			
			Thesis thesis = new ThesisDAO().findByProposal(idProposal);
			
			if(thesis != null) {
				report.setTitle(thesis.getTitle());
				report.setIdStudent(thesis.getStudent().getIdUser());
			} else {
				Project project = new ProjectDAO().findByProposal(idProposal);
				
				if(project != null) {
					report.setTitle(project.getTitle());
					report.setIdStudent(project.getStudent().getIdUser());
				} else {
					Proposal proposal = new ProposalDAO().findById(idProposal);
					
					if(proposal != null) {
						report.setTitle(proposal.getTitle());
						report.setIdStudent(proposal.getStudent().getIdUser());
					} else {
						report.setTitle("");
						report.setIdStudent(0);
						report.setStudent("");
					}
				}
			}
			
			report.setIdGroup(idGroup);
			report.setStudent(new UserDAO().findById(report.getIdStudent()).getName());
			report.setSupervisor(new UserDAO().findById(report.getIdSupervisor()).getName());
			
			rs.close();
			stmt.close();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT attendance.*, student.name AS studentName, supervisor.name AS supervisorName, " +
					"COALESCE(thesis.title, project.title, proposal.title) AS proposalTitle " +
					"FROM attendance INNER JOIN proposal ON proposal.idProposal=attendance.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=attendance.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=attendance.idSupervisor " +
					"LEFT JOIN project ON project.idproposal=proposal.idproposal " +
					"LEFT JOIN thesis ON thesis.idproject=project.idproject " +
					"WHERE attendance.idgroup = " + String.valueOf(idGroup) +  
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
	
	public List<AttendanceReport> getAttendanceReport(int idDepartment, int semester, int year, int stage, boolean includeCosupervisor) throws SQLException{
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
					"WHERE (proposal.invalidated=0 AND proposal.semester=" + String.valueOf(semester) + " AND proposal.year=" + String.valueOf(year) + ") " +
						"OR (project.semester=" + String.valueOf(semester) + " AND project.year=" + String.valueOf(year) + ") " +
						"OR (thesis.semester=" + String.valueOf(semester) + " AND thesis.year=" + String.valueOf(year) + ") " + 
					" ORDER BY student.name");
			
			while(rs.next()){
				ProposalDAO dao = new ProposalDAO();
				List<User> supervisors = dao.listSupervisors(rs.getInt("idproposal"), includeCosupervisor);
				
				for(User s : supervisors){
					for(int i = 1; i <= 2; i++){
						if((stage == 0) || (stage == i)){
							AttendanceReport report = new AttendanceReport();
							
							report.setStudent(rs.getString("studentName"));
							report.setTitle(rs.getString("title"));
							report.setSupervisor(s.getName());
							report.setStage(i);
							
							AttendanceReport rep = null;
							int idGroup = new AttendanceDAO().findIdGroup(rs.getInt("idproposal"), i, s.getIdUser());
							if((idGroup != 0) && Document.hasSignature(DocumentType.ATTENDANCE, idGroup)) {
								rep = this.getReport(idGroup);
							} else {
								idGroup = new AttendanceDAO().createGroup(rs.getInt("idstudent"), rs.getInt("idproposal"), s.getIdUser(), i);
								
								rep = this.getReport(idGroup);
							}
							
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
	
	public int findIdGroup(int idAttendance) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT idgroup FROM attendance WHERE idAttendance=" + String.valueOf(idAttendance));
			
			if(rs.next()) {
				return rs.getInt("idgroup");
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
	
	public int findIdGroup(int idProposal, int stage, int idSupervisor) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT idgroup FROM attendancegroup WHERE idProposal=" + String.valueOf(idProposal) + " AND idSupervisor=" + String.valueOf(idSupervisor) + " AND stage=" + String.valueOf(stage));
			
			if(rs.next()) {
				return rs.getInt("idgroup");
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
	
	public List<Integer> listIdGroup(int idProposal, int stage) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT idgroup FROM attendancegroup WHERE idProposal=" + String.valueOf(idProposal) + " AND stage=" + String.valueOf(stage));
			
			List<Integer> list = new ArrayList<Integer>();
			
			while(rs.next()) {
				list.add(rs.getInt("idgroup"));
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
