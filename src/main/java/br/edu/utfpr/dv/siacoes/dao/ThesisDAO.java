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
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;

public class ThesisDAO {
	
	public byte[] getFile(int idThesis) throws SQLException {
		if(idThesis == 0){
			return null;
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT thesis.file FROM thesis WHERE idThesis = ?");
		
			stmt.setInt(1, idThesis);
			
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

	public Thesis findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM thesis INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE idThesis = ?");
		
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
	
	public Thesis findByProject(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM thesis INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE thesis.idProject = ?");
		
			stmt.setInt(1, idProject);
			
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
	
	public Thesis findByProposal(int idProposal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT thesis.*, student.name AS studentName, supervisor.name AS supervisorName, cosupervisor.name AS cosupervisorName " +
					"FROM thesis INNER JOIN project ON project.idProject=thesis.idProject " +
					"INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
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
	
	public int findIdJury(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idJury FROM jury WHERE idThesis=?");
		
			stmt.setInt(1, idThesis);
			
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
	
	public int findIdProject(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idProject FROM thesis WHERE idThesis=?");
		
			stmt.setInt(1, idThesis);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("idProject");
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
	
	public int findIdProposal(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT project.idProposal FROM thesis " +
					"INNER JOIN project ON project.idProject=thesis.idProject " +
					"WHERE thesis.idThesis=?");
		
			stmt.setInt(1, idThesis);
			
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
	
	public int findIdDepartment(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.idDepartment FROM thesis " +
					"INNER JOIN project ON project.idProject=thesis.idProject " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"WHERE thesis.idThesis=?");
		
			stmt.setInt(1, idThesis);
			
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
	
	public Thesis findCurrentThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM thesis INNER JOIN project ON project.idProject=thesis.idThesis " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " + 
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " + 
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE thesis.idStudent = ? AND proposal.idDepartment=? AND thesis.semester = ? AND thesis.year = ?");
		
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
	
	public Thesis findApprovedThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT * FROM (SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName, jury.minimumScore, " +
					"(SELECT AVG(score) FROM (SELECT juryAppraiser.idJury, SUM(juryAppraiserScore.score) AS score " +
					"FROM juryAppraiser INNER JOIN juryAppraiserScore ON juryAppraiserScore.idJuryAppraiser = juryAppraiser.idJuryAppraiser " +
					"INNER JOIN evaluationItem ON evaluationItem.idEvaluationItem = juryAppraiserScore.idEvaluationItem " +
					"WHERE juryAppraiser.idJury = jury.idJury GROUP BY juryAppraiser.idJuryAppraiser) AS tableScore) AS score " +
					"FROM thesis INNER JOIN project ON project.idProject=thesis.idThesis " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " + 
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
					"INNER JOIN jury ON jury.idThesis = thesis.idThesis " +
					"INNER JOIN finalDocument ON finalDocument.idThesis = thesis.idThesis " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE finalDocument.supervisorFeedback=1 AND thesis.idStudent = ? AND proposal.idDepartment=? AND (thesis.year < " + String.valueOf(year) + " OR (thesis.year = " + String.valueOf(year) + " AND thesis.semester < " + String.valueOf(semester) + "))) AS p WHERE p.score >= p.minimumScore ORDER BY year DESC, semester DESC");
		
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
	
	public Thesis findLastThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM thesis INNER JOIN project ON project.idProject=thesis.idThesis " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " + 
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " + 
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE thesis.idStudent = ? AND proposal.idDepartment=? AND (thesis.year < " + String.valueOf(year) + " OR (thesis.year = " + String.valueOf(year) + " AND thesis.semester < " + String.valueOf(semester) + ")) ORDER BY thesis.year DESC, thesis.semester DESC");
		
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
	
	public List<Thesis> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM thesis INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
					"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"ORDER BY thesis.year DESC, thesis.semester DESC, student.name");
			List<Thesis> list = new ArrayList<Thesis>();
			
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
	
	public List<Thesis> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM thesis INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"LEFT JOIN \"user\" cosupervisor on cosupervisor.idUser=thesis.idCosupervisor " +
				"WHERE proposal.idDepartment=? AND thesis.semester=? AND thesis.year=? " +
				"ORDER BY student.name");
			
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
			rs = stmt.executeQuery();
			List<Thesis> list = new ArrayList<Thesis>();
			
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
	
	public List<Thesis> listBySupervisor(int idSupervisor) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM thesis INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" student ON student.idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"LEFT JOIN \"user\" cosupervisor on cosupervisor.idUser=thesis.idCosupervisor " +
				"WHERE thesis.idSupervisor=? OR thesis.idCosupervisor=? ORDER BY thesis.year DESC, thesis.semester DESC, thesis.title");
			
			stmt.setInt(1, idSupervisor);
			stmt.setInt(2, idSupervisor);
			
			rs = stmt.executeQuery();
			List<Thesis> list = new ArrayList<Thesis>();
			
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
	
	public int save(int idUser, Thesis thesis) throws SQLException{
		boolean insert = (thesis.getIdThesis() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO thesis(idProject, semester, year, title, subarea, idStudent, idSupervisor, idCosupervisor, file, submissionDate, abstract) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE thesis SET idProject=?, semester=?, year=?, title=?, subarea=?, idStudent=?, idSupervisor=?, idCosupervisor=?, file=?, submissionDate=?, abstract=? WHERE idThesis=?");
			}
			
			stmt.setInt(1, thesis.getProject().getIdProject());
			stmt.setInt(2, thesis.getSemester());
			stmt.setInt(3, thesis.getYear());
			stmt.setString(4, thesis.getTitle());
			stmt.setString(5, thesis.getSubarea());
			stmt.setInt(6, thesis.getStudent().getIdUser());
			stmt.setInt(7, thesis.getSupervisor().getIdUser());
			if((thesis.getCosupervisor() == null) || (thesis.getCosupervisor().getIdUser() == 0)){
				stmt.setNull(8, Types.INTEGER);
			}else{
				stmt.setInt(8, thesis.getCosupervisor().getIdUser());
			}
			stmt.setBytes(9, thesis.getFile());
			stmt.setDate(10, new java.sql.Date(thesis.getSubmissionDate().getTime()));
			stmt.setString(11, thesis.getAbstract());
			
			if(!insert){
				stmt.setInt(12, thesis.getIdThesis());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					thesis.setIdThesis(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, thesis);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, thesis);
			}
			
			return thesis.getIdThesis();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Thesis loadObject(ResultSet rs, boolean loadFiles) throws SQLException{
		Thesis p = new Thesis();
		
		p.setIdThesis(rs.getInt("idThesis"));
		p.getProject().setIdProject(rs.getInt("idProject"));
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
