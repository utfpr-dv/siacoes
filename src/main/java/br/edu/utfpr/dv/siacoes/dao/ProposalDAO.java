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
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.Semester;

public class ProposalDAO {
	
	public byte[] getFile(int id) throws SQLException {
		if(id == 0){
			return null;
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.file FROM proposal WHERE idProposal = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
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
	
	public List<User> listSupervisors(int idProposal, boolean includeCosupervisor) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			List<User> list = new ArrayList<User>();
			
			rs = stmt.executeQuery("SELECT \"user\".idUser, \"user\".name " +
					"FROM \"user\" INNER JOIN proposal ON \"user\".idUser=proposal.idSupervisor " +
					"WHERE proposal.idProposal=" + String.valueOf(idProposal) +
					" UNION " +
					"SELECT \"user\".idUser, \"user\".name " + 
					"FROM \"user\" INNER JOIN supervisorchange ON \"user\".idUser=supervisorchange.idNewSupervisor " +
					"WHERE supervisorchange.idProposal=" + String.valueOf(idProposal) +
					(includeCosupervisor ? " UNION " +
					"SELECT \"user\".idUser, \"user\".name " + 
					"FROM \"user\" INNER JOIN proposal ON \"user\".idUser=proposal.idCosupervisor " +
					"WHERE proposal.idProposal=" + String.valueOf(idProposal) +
					" UNION " +
					"SELECT \"user\".idUser, \"user\".name " + 
					"FROM \"user\" INNER JOIN supervisorchange ON \"user\".idUser=supervisorchange.idNewCosupervisor " +
					"WHERE supervisorchange.idProposal=" + String.valueOf(idProposal) : "") +
					" ORDER BY name");
			
			while(rs.next()){
				User user = new User();
					
				user.setIdUser(rs.getInt("idUser"));
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
	
	public String getStudentName(int id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT \"user\".name FROM proposal " +
					"INNER JOIN \"user\" ON \"user\".idUser=proposal.idStudent " +
					"WHERE proposal.idProposal = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getString("name");
			}else{
				return "";
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
	
	public Proposal findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName, department.name AS departmentName, department.idCampus " +
					"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=proposal.idCosupervisor " +
					"WHERE idProposal = ?");
		
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
	
	public byte[] findProposalFile(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT file FROM proposal WHERE idProposal = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
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
	
	public Proposal findByProject(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idProposal FROM project WHERE idProject=?");
		
			stmt.setInt(1, idProject);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.findById(rs.getInt("idProposal"));
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
	
	public Proposal findByThesis(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT project.idProposal FROM project " +
					"INNER JOIN thesis ON thesis.idProject=project.idProject " +
					"WHERE thesis.idThesis=?");
		
			stmt.setInt(1, idThesis);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.findById(rs.getInt("idProposal"));
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
	
	public Proposal findCurrentProposal(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			ThesisDAO tdao = new ThesisDAO();
			Thesis thesis = tdao.findCurrentThesis(idStudent, idDepartment, semester, year);
			
			ProjectDAO pdao = new ProjectDAO();
			Project project;
			
			if(thesis == null){
				project = pdao.findCurrentProject(idStudent, idDepartment, semester, year);
				
				if(project == null) {
					project = pdao.findApprovedProject(idStudent, idDepartment, semester, year, new SigetConfigDAO().findByDepartment(idDepartment).isRequestFinalDocumentStage1());
				}
			}else{
				project = pdao.findById(thesis.getProject().getIdProject());	
			}
			
			if(project == null){
				stmt = conn.prepareStatement("SELECT proposal.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName, department.name AS departmentName, department.idCampus " +
						"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
						"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
						"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
						"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=proposal.idCosupervisor " +
						"WHERE proposal.invalidated=0 AND proposal.idStudent = ? AND proposal.idDepartment=? AND proposal.semester = ? AND proposal.year = ?");
				
				stmt.setInt(1, idStudent);
				stmt.setInt(2, idDepartment);
				stmt.setInt(3, semester);
				stmt.setInt(4, year);
				
				rs = stmt.executeQuery();
				
				if(rs.next()){
					return this.loadObject(rs, true);
				}else{
					return null;
				}	
			}else{
				return this.findById(project.getProposal().getIdProposal());
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
	
	public Proposal findLastProposal(int idStudent, int idDepartment) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName, department.name AS departmentName, department.idCampus " +
					"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor " +
					"WHERE proposal.invalidated=0 AND proposal.idStudent = ? AND proposal.idDepartment=? " +
					"ORDER BY proposal.year DESC, proposal.semester DESC");
		
			stmt.setInt(1, idStudent);
			stmt.setInt(2, idDepartment);
			
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
	
	public List<Proposal> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName, department.name AS departmentName, department.idCampus " +
				"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
				"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
				"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=proposal.idCosupervisor " +
				"WHERE proposal.invalidated=0 AND proposal.idDepartment=? AND proposal.semester = ? AND proposal.year = ? " +
				"ORDER BY student.name");
		
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
			rs = stmt.executeQuery();
			List<Proposal> list = new ArrayList<Proposal>();
			
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
	
	public List<Proposal> listByAppraiser(int idAppraiser, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName, department.name AS departmentName, department.idCampus " +
					"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
					"INNER JOIN proposalappraiser appraiser ON appraiser.idProposal=proposal.idProposal " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=proposal.idCosupervisor " +
					"WHERE proposal.invalidated=0 AND appraiser.idAppraiser = ? AND semester = ? AND year = ? " +
					"ORDER BY student.name");
		
			stmt.setInt(1, idAppraiser);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
			rs = stmt.executeQuery();
			List<Proposal> list = new ArrayList<Proposal>();
			
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
	
	public List<Proposal> listByStudent(int idStudent, int idDepartment) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName, department.name AS departmentName, department.idCampus " +
					"FROM proposal inner join \"user\" student on student.idUser=proposal.idStudent " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=proposal.idCosupervisor " +
					"WHERE proposal.invalidated=0 AND proposal.idStudent = ? AND proposal.idDepartment = ? " +
					"ORDER BY proposal.year DESC, proposal.semester DESC, student.name");
		
			stmt.setInt(1, idStudent);
			stmt.setInt(2, idDepartment);
			
			rs = stmt.executeQuery();
			List<Proposal> list = new ArrayList<Proposal>();
			
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
	
	public List<Proposal> listBySupervisor(int idSupervisor) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName, department.name AS departmentName, department.idCampus " +
					"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor " +
					"WHERE proposal.invalidated=0 AND proposal.idSupervisor = ? OR proposal.idCosupervisor = ? " +
					"ORDER BY proposal.year DESC, proposal.semester DESC, student.name");
		
			stmt.setInt(1, idSupervisor);
			stmt.setInt(2, idSupervisor);
			
			rs = stmt.executeQuery();
			List<Proposal> list = new ArrayList<Proposal>();
			
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
	
	public int getProposalStage(int idProposal) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT idProject, semester, year FROM project WHERE idProposal=" + String.valueOf(idProposal));
			
			if(rs.next()) {
				int idProject = rs.getInt("idProject");
				int semester = rs.getInt("semester");
				int year = rs.getInt("year");
				
				rs.close();
				rs = stmt.executeQuery("SELECT idThesis FROM thesis WHERE idProject=" + String.valueOf(idProject));
				
				if(rs.next()) {
					return 2;
				} else {
					rs.close();
					rs = stmt.executeQuery("SELECT department.idCampus FROM proposal INNER JOIN department ON department.idDepartment=proposal.idDepartment WHERE proposal.idProposal=" + String.valueOf(idProposal));
					int idCampus = 0;
					if(rs.next()) {
						idCampus = rs.getInt("idCampus");
					}
					Semester s = new SemesterDAO().findByDate(idCampus, DateUtils.getNow().getTime());
					
					if((year < s.getYear()) || (semester < s.getSemester())) {
						return 2;
					} else {
						return 1;
					}
				}
			} else {
				return 1;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Proposal> listAll(boolean includeInvalidated) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT proposal.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName, department.name AS departmentName, department.idCampus " +
					"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " +
					"INNER JOIN \"user\" supervisor on supervisor.idUser=proposal.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor " +
					(includeInvalidated ? "" : "WHERE proposal.invalidated=0 ") +
					"ORDER BY proposal.year DESC, proposal.semester DESC, student.name");
			List<Proposal> list = new ArrayList<Proposal>();
			
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
	
	public boolean invalidated(int idOldProposal, int idNewProposal) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			
			stmt.execute("UPDATE attendance SET idProposal=" + String.valueOf(idNewProposal) + " WHERE idProposal=" + String.valueOf(idOldProposal));
			
			boolean ret = stmt.execute("UPDATE proposal SET invalidated=1 WHERE idProposal=" + String.valueOf(idOldProposal));
			
			conn.commit();
			
			return ret;
		}catch(SQLException e){
			conn.rollback();
			
			conn.setAutoCommit(true);
			stmt.close();
			stmt = conn.createStatement();
			stmt.execute("DELETE FROM proposal WHERE idProposal=" + String.valueOf(idNewProposal));
			
			throw e;
		}finally{
			conn.setAutoCommit(true);
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(int idUser, Proposal proposal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (proposal.getIdProposal() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO proposal(idDepartment, semester, year, title, subarea, idStudent, idSupervisor, idCosupervisor, file, submissionDate, invalidated, supervisorFeedback, supervisorFeedbackDate, supervisorComments) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, NULL, '')", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE proposal SET idDepartment=?, semester=?, year=?, title=?, subarea=?, idStudent=?, idSupervisor=?, idCosupervisor=?, file=?, submissionDate=? WHERE idProposal=?");
			}
			
			stmt.setInt(1, proposal.getDepartment().getIdDepartment());
			stmt.setInt(2, proposal.getSemester());
			stmt.setInt(3, proposal.getYear());
			stmt.setString(4, proposal.getTitle());
			stmt.setString(5, proposal.getSubarea());
			stmt.setInt(6, proposal.getStudent().getIdUser());
			stmt.setInt(7, proposal.getSupervisor().getIdUser());
			if((proposal.getCosupervisor() == null) || (proposal.getCosupervisor().getIdUser() == 0)){
				stmt.setNull(8, Types.INTEGER);
			}else{
				stmt.setInt(8, proposal.getCosupervisor().getIdUser());
			}
			if(proposal.getFile() == null){
				stmt.setNull(9, Types.BINARY);
			}else{
				stmt.setBytes(9, proposal.getFile());
			}
			stmt.setDate(10, new java.sql.Date(proposal.getSubmissionDate().getTime()));
			
			if(!insert){
				stmt.setInt(11, proposal.getIdProposal());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					proposal.setIdProposal(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, proposal);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, proposal);
			}
			
			if(proposal.getAppraisers() != null){
				ProposalAppraiserDAO dao = new ProposalAppraiserDAO(conn);
				String ids = "";
				
				for(ProposalAppraiser pa : proposal.getAppraisers()){
					pa.setProposal(proposal);
					int paId = dao.save(idUser, pa);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM proposalappraiser WHERE idProposal=" + String.valueOf(proposal.getIdProposal()) + 
						(!ids.isEmpty() ? " AND idProposalAppraiser NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")" : "") +
						" AND idProposalAppraiser NOT IN(SELECT signdocument.idRegister FROM signdocument " +
							"INNER JOIN signature ON signature.iddocument=signdocument.iddocument " +
							"WHERE signature.signature IS NOT NULL AND signature.revoked=0 AND signdocument.type=" + String.valueOf(DocumentType.APPRAISERFEEDBACK.getValue()) + ")");
				st.close();
			}
			
			conn.commit();
			
			return proposal.getIdProposal();
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
	
	public int saveSupervisorFeedback(Proposal proposal) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			
			stmt = conn.prepareStatement("UPDATE proposal SET supervisorFeedback=?, supervisorFeedbackDate=?, supervisorComments=? WHERE idProposal=?");
			
			stmt.setInt(1, proposal.getSupervisorFeedback().getValue());
			stmt.setDate(2, new java.sql.Date(proposal.getSupervisorFeedbackDate().getTime()));
			stmt.setString(3, proposal.getSupervisorComments());
			stmt.setInt(4, proposal.getIdProposal());
			
			stmt.execute();
			
			return proposal.getIdProposal();
		} finally {
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Proposal loadObject(ResultSet rs, boolean loadFiles) throws SQLException{
		Proposal p = new Proposal();
		
		p.setIdProposal(rs.getInt("idProposal"));
		p.setTitle(rs.getString("title"));
		p.setSubarea(rs.getString("subarea"));
		p.getStudent().setIdUser(rs.getInt("idStudent"));
		p.getStudent().setName(rs.getString("studentName"));
		p.setSubmissionDate(rs.getDate("submissionDate"));
		p.getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		p.getSupervisor().setName(rs.getString("supervisorName"));
		p.setCosupervisor(new User());
		p.getCosupervisor().setIdUser(rs.getInt("idCosupervisor"));
		if(p.getCosupervisor().getIdUser() != 0){
			p.getCosupervisor().setName(rs.getString("cosupervisorname"));
		}
		p.setSemester(rs.getInt("semester"));
		p.setYear(rs.getInt("year"));
		p.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		p.getDepartment().setName(rs.getString("departmentName"));
		p.getDepartment().getCampus().setIdCampus(rs.getInt("idCampus"));
		p.setInvalidated(rs.getInt("invalidated") == 1);
		p.setSupervisorFeedback(ProposalFeedback.valueOf(rs.getInt("supervisorFeedback")));
		p.setSupervisorFeedbackDate(rs.getDate("supervisorFeedbackDate"));
		p.setSupervisorComments(rs.getString("supervisorComments"));
		p.setFileUploaded(rs.getBytes("file") != null);
		
		if(loadFiles) {
			p.setFile(rs.getBytes("file"));
		}
		
		return p;
	}
	
	public long getCurrentProposals(int semester, int year) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(DISTINCT proposal.idstudent) AS total FROM proposal WHERE proposal.invalidated = 0 " + 
					"AND NOT EXISTS(SELECT idfinaldocument FROM finaldocument " + 
						"INNER JOIN thesis ON thesis.idthesis=finaldocument.idfinaldocument " + 
						"INNER JOIN project ON project.idproject=thesis.idproject " + 
						"WHERE project.idproposal=proposal.idproposal) " +
					"AND ((proposal.semester = " + String.valueOf(semester) + " AND proposal.year = " + String.valueOf(year) + ") OR EXISTS(SELECT idproject FROM project WHERE project.idproposal=proposal.idproposal))");
			
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
	
	public int getCountTutored(int idProposal, int idDepartment, int idSupervisor, int semester, int year) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(idProposal) AS total FROM proposal " +
					"WHERE idProposal != " + String.valueOf(idProposal) + 
					" AND idDepartment = " + String.valueOf(idDepartment) +
					" AND idSupervisor = " + String.valueOf(idSupervisor) + 
					" AND semester = " + String.valueOf(semester) + " AND year = " + String.valueOf(year));
			
			if(rs.next()) {
				return rs.getInt("total");
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
	
	public int findIdCampus(int idProposal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT department.idCampus FROM department " +
					"INNER JOIN proposal ON proposal.idDepartment=department.idDepartment " +
					"WHERE proposal.idProposal=?");
		
			stmt.setInt(1, idProposal);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("idCampus");
			}else{
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
	
	public int findIdDepartment(int idProposal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idDepartment FROM proposal WHERE proposal.idProposal=?");
		
			stmt.setInt(1, idProposal);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("idDepartment");
			}else{
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
