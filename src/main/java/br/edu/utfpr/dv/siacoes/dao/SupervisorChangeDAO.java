package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange.ChangeFeedback;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;

public class SupervisorChangeDAO {
	
	public int save(int idUser, SupervisorChange change) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (change.getIdSupervisorChange() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO supervisorchange(idProposal, idOldSupervisor, idNewSupervisor, idOldCosupervisor, idNewCosupervisor, date, comments, approved, approvalDate, approvalComments, supervisorRequest) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE supervisorchange SET idProposal=?, idOldSupervisor=?, idNewSupervisor=?, idOldCosupervisor=?, idNewCosupervisor=?, date=?, comments=?, approved=?, approvalDate=?, approvalComments=?, supervisorRequest=? WHERE idSupervisorChange=?");
			}
			
			stmt.setInt(1, change.getProposal().getIdProposal());
			stmt.setInt(2, change.getOldSupervisor().getIdUser());
			stmt.setInt(3, change.getNewSupervisor().getIdUser());
			if((change.getOldCosupervisor() == null) || (change.getOldCosupervisor().getIdUser() == 0)){
				stmt.setNull(4, Types.INTEGER);
			}else{
				stmt.setInt(4, change.getOldCosupervisor().getIdUser());
			}
			if((change.getNewCosupervisor() == null) || (change.getNewCosupervisor().getIdUser() == 0)){
				stmt.setNull(5, Types.INTEGER);
			}else{
				stmt.setInt(5, change.getNewCosupervisor().getIdUser());
			}
			stmt.setTimestamp(6, new java.sql.Timestamp(change.getDate().getTime()));
			stmt.setString(7, change.getComments());
			stmt.setInt(8, change.getApproved().getValue());
			
			if(change.getApproved() != ChangeFeedback.NONE){
				stmt.setTimestamp(9, new java.sql.Timestamp(change.getApprovalDate().getTime()));
				stmt.setString(10, change.getApprovalComments());
			}else{
				stmt.setNull(9, Types.TIMESTAMP);
				stmt.setString(10, "");
			}
			
			stmt.setInt(11, (change.isSupervisorRequest() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(12, change.getIdSupervisorChange());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					change.setIdSupervisorChange(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, change);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, change);
			}
			
			if(change.getApproved() == ChangeFeedback.APPROVED){
				ProposalDAO pdao = new ProposalDAO();
				Proposal proposal = pdao.findById(change.getProposal().getIdProposal());
				Semester semester = new SemesterDAO().findByDate(proposal.getDepartment().getCampus().getIdCampus(), change.getApprovalDate());
				
				ThesisDAO tdao = new ThesisDAO();
				Thesis thesis = tdao.findCurrentThesis(proposal.getStudent().getIdUser(), proposal.getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
				
				if(thesis == null){
					ProjectDAO pdao2 = new ProjectDAO();
					Project project = pdao2.findCurrentProject(proposal.getStudent().getIdUser(), proposal.getDepartment().getIdDepartment(), semester.getSemester(), semester.getYear());
					
					if(project != null){
						stmt.execute("UPDATE project SET idSupervisor = " + String.valueOf(change.getNewSupervisor().getIdUser()) + ", idCosupervisor = " + ((change.getNewCosupervisor() == null) || (change.getNewCosupervisor().getIdUser() == 0) ? "NULL" : String.valueOf(change.getNewCosupervisor().getIdUser())) + " WHERE idProject = " + String.valueOf(project.getIdProject()));
						
						project.getSupervisor().setIdUser(change.getNewSupervisor().getIdUser());
						project.getCosupervisor().setIdUser(change.getNewCosupervisor().getIdUser());
						
						new UpdateEvent(conn).registerUpdate(idUser, project);
					}
				}else{
					stmt.execute("UPDATE thesis SET idSupervisor = " + String.valueOf(change.getNewSupervisor().getIdUser()) + ", idCosupervisor = " + ((change.getNewCosupervisor() == null) || (change.getNewCosupervisor().getIdUser() == 0) ? "NULL" : String.valueOf(change.getNewCosupervisor().getIdUser())) + " WHERE idThesis = " + String.valueOf(thesis.getIdThesis()));
					
					thesis.getSupervisor().setIdUser(change.getNewSupervisor().getIdUser());
					thesis.getCosupervisor().setIdUser(change.getNewCosupervisor().getIdUser());
					
					new UpdateEvent(conn).registerUpdate(idUser, thesis);
				}
			}
			
			conn.commit();
			
			return change.getIdSupervisorChange();	
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<SupervisorChange> list(int idDepartment, int semester, int year, boolean onlyPending) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			Semester sem = new SemesterDAO().findBySemester(new CampusDAO().findByDepartment(idDepartment).getIdCampus(), semester, year);
		
			Date initialDate = sem.getStartDate();
			Date finalDate = sem.getEndDate();
			
			stmt = conn.prepareStatement(
					"SELECT supervisorchange.*, proposal.title, proposal.iddepartment, proposal.idstudent, student.name AS studentName, student.studentCode, " +
					"oldsupervisor.name AS oldSupervisorName, oldcosupervisor.name AS oldCosupervisorName, newsupervisor.name AS newSupervisorName " +
					"FROM supervisorchange INNER JOIN proposal ON proposal.idProposal=supervisorchange.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"LEFT JOIN \"user\" oldsupervisor ON oldsupervisor.idUser=supervisorchange.idOldSupervisor " +
					"LEFT JOIN \"user\" oldcosupervisor ON oldcosupervisor.idUser=supervisorchange.idOldCosupervisor " +
					"LEFT JOIN \"user\" newsupervisor ON newsupervisor.idUser=supervisorchange.idNewSupervisor " +
					"WHERE proposal.idDepartment=? AND supervisorchange.date BETWEEN ? AND ? " + (onlyPending ? " AND supervisorchange.approved=0" : ""));
			
			stmt.setInt(1, idDepartment);
			stmt.setTimestamp(2, new java.sql.Timestamp(initialDate.getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(finalDate.getTime()));
			
			rs = stmt.executeQuery();
			
			List<SupervisorChange> list = new ArrayList<SupervisorChange>();
			
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
	
	public List<SupervisorChange> list(int idSupervisor) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			stmt = conn.prepareStatement(
					"SELECT supervisorchange.*, proposal.title, proposal.iddepartment, proposal.idstudent, student.name AS studentName, student.studentCode, " +
					"oldsupervisor.name AS oldSupervisorName, oldcosupervisor.name AS oldCosupervisorName, newsupervisor.name AS newSupervisorName " +
					"FROM supervisorchange INNER JOIN proposal ON proposal.idProposal=supervisorchange.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"LEFT JOIN \"user\" oldsupervisor ON oldsupervisor.idUser=supervisorchange.idOldSupervisor " +
					"LEFT JOIN \"user\" oldcosupervisor ON oldcosupervisor.idUser=supervisorchange.idOldCosupervisor " +
					"LEFT JOIN \"user\" newsupervisor ON newsupervisor.idUser=supervisorchange.idNewSupervisor " +
					"WHERE supervisorchange.approved=" + String.valueOf(ChangeFeedback.APPROVED.getValue()) + 
					" AND (supervisorchange.idNewSupervisor=? OR supervisorchange.idNewCosupervisor=?)");
			
			stmt.setInt(1, idSupervisor);
			stmt.setInt(2, idSupervisor);
			
			rs = stmt.executeQuery();
			
			List<SupervisorChange> list = new ArrayList<SupervisorChange>();
			
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
	
	public List<SupervisorChange> listByProposal(int idProposal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			stmt = conn.prepareStatement(
					"SELECT supervisorchange.*, proposal.title, proposal.iddepartment, proposal.idstudent, student.name AS studentName, student.studentCode, " +
					"oldsupervisor.name AS oldSupervisorName, oldcosupervisor.name AS oldCosupervisorName, newsupervisor.name AS newSupervisorName " +
					"FROM supervisorchange INNER JOIN proposal ON proposal.idProposal=supervisorchange.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"LEFT JOIN \"user\" oldsupervisor ON oldsupervisor.idUser=supervisorchange.idOldSupervisor " +
					"LEFT JOIN \"user\" oldcosupervisor ON oldcosupervisor.idUser=supervisorchange.idOldCosupervisor " +
					"LEFT JOIN \"user\" newsupervisor ON newsupervisor.idUser=supervisorchange.idNewSupervisor " +
					"WHERE supervisorchange.idproposal=?");
			
			stmt.setInt(1, idProposal);
			
			rs = stmt.executeQuery();
			
			List<SupervisorChange> list = new ArrayList<SupervisorChange>();
			
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
	
	public SupervisorChange findById(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT supervisorchange.*, proposal.title, proposal.iddepartment, proposal.idstudent, student.name AS studentName, student.studentCode, " +
					"oldSupervisor.name AS oldSupervisorName, oldcosupervisor.name AS oldCosupervisorName, newsupervisor.name AS newSupervisorName " +
					"FROM supervisorchange INNER JOIN proposal ON proposal.idProposal=supervisorchange.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"LEFT JOIN \"user\" oldsupervisor ON oldsupervisor.idUser=supervisorchange.idOldSupervisor " +
					"LEFT JOIN \"user\" oldcosupervisor ON oldcosupervisor.idUser=supervisorchange.idOldCosupervisor " +
					"LEFT JOIN \"user\" newsupervisor ON newsupervisor.idUser=supervisorchange.idNewSupervisor " +
					"WHERE supervisorchange.idSupervisorChange=" + String.valueOf(id));
			
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
	
	public SupervisorChange findPendingChange(int idProposal) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT supervisorchange.*, proposal.title, proposal.iddepartment, proposal.idstudent, student.name AS studentName, student.studentCode, " +
					"oldSupervisor.name AS oldSupervisorName, oldcosupervisor.name AS oldCosupervisorName, newsupervisor.name AS newSupervisorName " +
					"FROM supervisorchange INNER JOIN proposal ON proposal.idProposal=supervisorchange.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"LEFT JOIN \"user\" oldsupervisor ON oldsupervisor.idUser=supervisorchange.idOldSupervisor " +
					"LEFT JOIN \"user\" oldcosupervisor ON oldcosupervisor.idUser=supervisorchange.idOldCosupervisor " +
					"LEFT JOIN \"user\" newsupervisor ON newsupervisor.idUser=supervisorchange.idNewSupervisor " +
					"WHERE supervisorchange.approved=0 AND supervisorchange.idProposal=" + String.valueOf(idProposal));
			
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
	
	public User findCurrentSupervisor(int idProposal) throws SQLException{
		int idUser = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT idNewSupervisor FROM SupervisorChange WHERE approved=1 AND idProposal = " + String.valueOf(idProposal) + " ORDER BY date DESC");
			
			if(rs.next()){
				idUser = rs.getInt("idNewSupervisor");
			}else{
				ProjectDAO pdao = new ProjectDAO();
				Project project = pdao.findByProposal(idProposal);
				
				if(project == null){
					ProposalDAO dao = new ProposalDAO();
					Proposal proposal = dao.findById(idProposal);
					
					idUser = proposal.getSupervisor().getIdUser();
				}else{
					ThesisDAO tdao = new ThesisDAO();
					Thesis thesis = tdao.findByProject(project.getIdProject());
					
					if(thesis == null){
						idUser = project.getSupervisor().getIdUser();
					}else{
						idUser = thesis.getSupervisor().getIdUser();
					}
				}
			}
			
			UserDAO dao = new UserDAO();
			return dao.findById(idUser);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public User findCurrentCosupervisor(int idProposal) throws SQLException{
		int idUser = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT idNewCosupervisor FROM SupervisorChange WHERE approved=1 AND idProposal = " + String.valueOf(idProposal) + " ORDER BY date DESC");
			
			if(rs.next()){
				idUser = rs.getInt("idNewCosupervisor");
			}else{
				ProjectDAO pdao = new ProjectDAO();
				Project project = pdao.findByProposal(idProposal);
				
				if(project == null){
					ProposalDAO dao = new ProposalDAO();
					Proposal proposal = dao.findById(idProposal);
					
					idUser = proposal.getCosupervisor().getIdUser();
				}else{
					ThesisDAO tdao = new ThesisDAO();
					Thesis thesis = tdao.findByProject(project.getIdProject());
					
					if(thesis == null){
						idUser = project.getCosupervisor().getIdUser();
					}else{
						idUser = thesis.getCosupervisor().getIdUser();
					}
				}
			}
			
			UserDAO dao = new UserDAO();
			return dao.findById(idUser);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private SupervisorChange loadObject(ResultSet rs) throws SQLException{
		SupervisorChange change = new SupervisorChange();
		
		change.setIdSupervisorChange(rs.getInt("idSupervisorChange"));
		change.setProposal(new Proposal());
		change.getProposal().setIdProposal(rs.getInt("idProposal"));
		change.getProposal().getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		change.getProposal().setTitle(rs.getString("title"));
		change.getProposal().getStudent().setIdUser(rs.getInt("idStudent"));
		change.getProposal().getStudent().setName(rs.getString("studentName"));
		change.getProposal().getStudent().setStudentCode(rs.getString("studentCode"));
		change.setOldSupervisor(new User());
		change.getOldSupervisor().setIdUser(rs.getInt("idOldSupervisor"));
		change.getOldSupervisor().setName(rs.getString("oldSupervisorName"));
		if(rs.getInt("idNewSupervisor") != 0){
			change.setNewSupervisor(new User());
			change.getNewSupervisor().setIdUser(rs.getInt("idNewSupervisor"));
			change.getNewSupervisor().setName(rs.getString("newSupervisorName"));
		}
		if(rs.getInt("idOldCosupervisor") != 0){
			change.setOldCosupervisor(new User());
			change.getOldCosupervisor().setIdUser(rs.getInt("idOldCosupervisor"));
			change.getOldCosupervisor().setName(rs.getString("oldCosupervisorName"));
		}
		if(rs.getInt("idNewCosupervisor") != 0){
			change.setNewCosupervisor(new User());
			change.getNewCosupervisor().setIdUser(rs.getInt("idNewCosupervisor"));
		}
		change.setDate(rs.getTimestamp("date"));
		change.setComments(rs.getString("comments"));
		change.setApproved(ChangeFeedback.valueOf(rs.getInt("approved")));
		change.setApprovalDate(rs.getTimestamp("approvalDate"));
		change.setApprovalComments(rs.getString("approvalComments"));
		change.setSupervisorRequest(rs.getInt("supervisorRequest") == 1);
		
		return change;
	}

}
