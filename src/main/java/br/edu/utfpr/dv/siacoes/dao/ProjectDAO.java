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
import br.edu.utfpr.dv.siacoes.model.User;

public class ProjectDAO {
	
	public byte[] getFile(int idProject) throws SQLException {
		if(idProject == 0){
			return null;
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT project.file FROM project WHERE idProject = ?");
		
			stmt.setInt(1, idProject);
			
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
	
	public Project findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM project INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
					"WHERE project.idProject = ?");
		
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
	
	public Project findByProposal(int idProposal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM project INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
					"WHERE project.idProposal = ?");
		
			stmt.setInt(1, idProposal);
			
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
	
	public int findIdJury(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idJury FROM jury WHERE idProject=?");
		
			stmt.setInt(1, idProject);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("idJury");
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
	
	public int findIdCampus(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT campus.idCampus FROM campus " +
					"INNER JOIN department ON department.idCampus=campus.idCampus " +
					"INNER JOIN proposal ON proposal.idDepartment=department.idDepartment " +
					"INNER JOIN project ON project.idProposal=proposal.idProposal " +
					"WHERE project.idProject=?");
		
			stmt.setInt(1, idProject);
			
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
	
	public int findIdDepartment(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.idDepartment FROM proposal " +
					"INNER JOIN project ON project.idProposal=proposal.idProposal " +
					"WHERE project.idProject=?");
		
			stmt.setInt(1, idProject);
			
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
	
	public int findIdProposal(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idProposal FROM project WHERE idProject=?");
		
			stmt.setInt(1, idProject);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("idProposal");
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
	
	public Project findCurrentProject(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProject " + 
					"INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " + 
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
					"WHERE project.idStudent = ? AND proposal.idDepartment=? AND project.semester = ? AND project.year = ?");
		
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
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Project findApprovedProject(int idStudent, int idDepartment, int semester, int year, boolean requestFinalDocumentStage1) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT * FROM (SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName, jury.minimumScore, " +
				"(SELECT AVG(score) FROM (SELECT juryAppraiser.idJury, SUM(juryAppraiserScore.score) AS score " +
				"FROM juryAppraiser INNER JOIN juryAppraiserScore ON juryAppraiserScore.idJuryAppraiser = juryAppraiser.idJuryAppraiser " +
				"INNER JOIN evaluationItem ON evaluationItem.idEvaluationItem = juryAppraiserScore.idEvaluationItem " +
				"WHERE juryAppraiser.substitute=0 AND juryAppraiser.idJury = jury.idJury GROUP BY juryAppraiser.idJuryAppraiser) AS tableScore) AS score " +
				"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProposal " + 
				"INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"INNER JOIN jury ON jury.idProject = project.idProject " +
				(requestFinalDocumentStage1 ? "INNER JOIN finalDocument ON finalDocument.idProject = project.idProject " : "") +
				"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
				"WHERE " + (requestFinalDocumentStage1 ? "finalDocument.supervisorFeedback=1 AND " : "") + 
					"project.idStudent = ? AND proposal.idDepartment = ? AND (project.year < " + String.valueOf(year) + 
					" OR (project.year = " + String.valueOf(year) + " AND project.semester < " + String.valueOf(semester) + 
					"))) AS p WHERE p.score >= p.minimumScore ORDER BY year DESC, semester DESC");
		
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
	
	public Project findLastProject(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProposal " + 
				"INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
				"WHERE project.idStudent = ? AND proposal.idDepartment = ? AND (project.year < " + String.valueOf(year) + " OR (project.year = " + String.valueOf(year) + " AND project.semester < " + String.valueOf(semester) + ")) ORDER BY year DESC, semester DESC");
		
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
	
	public List<Project> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM project INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
					"ORDER BY project.year DESC, project.semester DESC, student.name");
			List<Project> list = new ArrayList<Project>();
			
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
	
	public List<Project> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProject " +
				"INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"LEFT JOIN \"user\" cosupervisor on cosupervisor.idUser=project.idCosupervisor " +
				"WHERE proposal.idDepartment=? AND project.semester=? AND project.year=? " +
				"ORDER BY student.name");
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
			rs = stmt.executeQuery();
			List<Project> list = new ArrayList<Project>();
			
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
	
	public List<Project> listBySupervisor(int idSupervisor) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProject " +
				"INNER JOIN \"user\" student ON student.idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"LEFT JOIN \"user\" cosupervisor on cosupervisor.idUser=project.idCosupervisor " +
				"WHERE project.idSupervisor=? OR project.idCosupervisor=? ORDER BY project.year DESC, project.semester DESC, project.title");
			
			stmt.setInt(1, idSupervisor);
			stmt.setInt(2, idSupervisor);
			
			rs = stmt.executeQuery();
			List<Project> list = new ArrayList<Project>();
			
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
	
	public int save(int idUser, Project project) throws SQLException{
		boolean insert = (project.getIdProject() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO project(idProposal, semester, year, title, subarea, idStudent, idSupervisor, idCosupervisor, file, submissionDate, abstract) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE project SET idProposal=?, semester=?, year=?, title=?, subarea=?, idStudent=?, idSupervisor=?, idCosupervisor=?, file=?, submissionDate=?, abstract=? WHERE idProject=?");
			}
			
			stmt.setInt(1, project.getProposal().getIdProposal());
			stmt.setInt(2, project.getSemester());
			stmt.setInt(3, project.getYear());
			stmt.setString(4, project.getTitle());
			stmt.setString(5, project.getSubarea());
			stmt.setInt(6, project.getStudent().getIdUser());
			stmt.setInt(7, project.getSupervisor().getIdUser());
			if((project.getCosupervisor() == null) || (project.getCosupervisor().getIdUser() == 0)){
				stmt.setNull(8, Types.INTEGER);
			}else{
				stmt.setInt(8, project.getCosupervisor().getIdUser());
			}
			stmt.setBytes(9, project.getFile());
			stmt.setDate(10, new java.sql.Date(project.getSubmissionDate().getTime()));
			stmt.setString(11, project.getAbstract());
			
			if(!insert){
				stmt.setInt(12, project.getIdProject());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					project.setIdProject(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, project);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, project);
			}
			
			return project.getIdProject();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Project loadObject(ResultSet rs, boolean loadFiles) throws SQLException{
		Project p = new Project();
		
		p.setIdProject(rs.getInt("idProject"));
		p.getProposal().setIdProposal(rs.getInt("idProposal"));
		p.setTitle(rs.getString("title"));
		p.setSubarea(rs.getString("subarea"));
		p.getStudent().setIdUser(rs.getInt("idStudent"));
		p.getStudent().setName(rs.getString("studentname"));
		p.getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		p.getSupervisor().setName(rs.getString("supervisorname"));
		p.setCosupervisor(new User());
		p.getCosupervisor().setIdUser(rs.getInt("idCosupervisor"));
		if(p.getCosupervisor().getIdUser() != 0){
			p.getCosupervisor().setName(rs.getString("cosupervisorname"));
		}
		p.setSemester(rs.getInt("semester"));
		p.setYear(rs.getInt("year"));
		p.setSubmissionDate(rs.getDate("submissionDate"));
		p.setAbstract(rs.getString("abstract"));
		
		if(loadFiles) {
			p.setFile(rs.getBytes("file"));	
		}
		
		return p;
	}

}
