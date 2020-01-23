package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Thesis;

public class JuryAppraiserDAO {
	
	private Connection conn;
	
	public JuryAppraiserDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public JuryAppraiserDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public byte[] getFile(int id) throws SQLException {
		if(id == 0){
			return null;
		}
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			this.conn = ConnectionDAO.getInstance().getConnection();
			stmt = this.conn.prepareStatement("SELECT juryappraiser.file FROM juryappraiser WHERE idJuryAppraiser = ?");
		
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
		}
	}
	
	public byte[] getAdditionalFile(int id) throws SQLException {
		if(id == 0){
			return null;
		}
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			this.conn = ConnectionDAO.getInstance().getConnection();
			stmt = this.conn.prepareStatement("SELECT juryappraiser.additionalFile FROM juryappraiser WHERE idJuryAppraiser = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getBytes("additionalFile");
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public JuryAppraiser findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT juryappraiser.*, appraiser.name as appraiserName, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM juryappraiser INNER JOIN \"user\" appraiser ON appraiser.idUser=juryappraiser.idAppraiser " +
					"INNER JOIN jury ON jury.idJury=juryappraiser.idJury " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE juryappraiser.idJuryAppraiser=?");
			
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
		}
	}
	
	public JuryAppraiser findByAppraiser(int idJury, int idUser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT juryappraiser.*, appraiser.name as appraiserName, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM juryappraiser INNER JOIN \"user\" appraiser ON appraiser.idUser=juryappraiser.idAppraiser " +
					"INNER JOIN jury ON jury.idJury=juryappraiser.idJury " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE juryappraiser.idJury=? AND juryappraiser.idAppraiser=?");
			
			stmt.setInt(1, idJury);
			stmt.setInt(2, idUser);
			
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
		}
	}
	
	public JuryAppraiser findChair(int idJury) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT juryappraiser.*, appraiser.name as appraiserName, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM juryappraiser INNER JOIN \"user\" appraiser ON appraiser.idUser=juryappraiser.idAppraiser " +
					"INNER JOIN jury ON jury.idJury=juryappraiser.idJury " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE juryappraiser.idJury=? AND juryappraiser.chair=1 AND juryappraiser.substitute=0");
			
			stmt.setInt(1, idJury);
			
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
		}
	}
	
	public List<JuryAppraiser> listAppraisers(int idJury) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT juryappraiser.*, appraiser.name as appraiserName, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM juryappraiser INNER JOIN \"user\" appraiser ON appraiser.idUser=juryappraiser.idAppraiser " +
					"INNER JOIN jury ON jury.idJury=juryappraiser.idJury " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE juryappraiser.idJury = " + String.valueOf(idJury) + 
					" ORDER BY juryappraiser.chair DESC, juryappraiser.substitute, appraiser.name");
			List<JuryAppraiser> list = new ArrayList<JuryAppraiser>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public int findIdDepartment(int idJuryAppraiser) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.idDepartment FROM juryappraiser INNER JOIN jury ON jury.idJury=juryappraiser.idJury " + 
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON (project.idProject=thesis.idProject OR project.idProject=jury.idProject) " + 
					"LEFT JOIN proposal ON proposal.idProposal=project.idProposal " + 
					"WHERE juryappraiser.idJuryAppraiser=?");
		
			stmt.setInt(1, idJuryAppraiser);
			
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
	
	public int save(int idUser, JuryAppraiser appraiser) throws SQLException{
		boolean insert = (appraiser.getIdJuryAppraiser() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO juryappraiser(idJury, idAppraiser, chair, substitute, file, additionalFile, comments) VALUES(?, ?, ?, ?, NULL, NULL, '')", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE juryappraiser SET idJury=?, idAppraiser=?, chair=?, substitute=?, file=?, additionalFile=?, comments=? WHERE idJuryAppraiser=?");
			}
			
			stmt.setInt(1, appraiser.getJury().getIdJury());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			stmt.setInt(3, (appraiser.isChair() ? 1 : 0));
			stmt.setInt(4, (appraiser.isSubstitute() ? 1 : 0));
			
			if(!insert){
				stmt.setBytes(5, appraiser.getFile());
				stmt.setBytes(6, appraiser.getAdditionalFile());
				stmt.setString(7, appraiser.getComments());
				stmt.setInt(8, appraiser.getIdJuryAppraiser());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					appraiser.setIdJuryAppraiser(rs.getInt(1));
				}

				new UpdateEvent(this.conn).registerInsert(idUser, appraiser);
			} else {
				new UpdateEvent(this.conn).registerUpdate(idUser, appraiser);
			}
			
			return appraiser.getIdJuryAppraiser();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private JuryAppraiser loadObject(ResultSet rs) throws SQLException{
		JuryAppraiser p = new JuryAppraiser();
		
		p.setIdJuryAppraiser(rs.getInt("idJuryAppraiser"));
		p.getJury().setIdJury(rs.getInt("idJury"));
		p.getJury().setDate(rs.getDate("date"));
		p.getJury().setStartTime(rs.getTime("startTime"));
		p.getJury().setEndTime(rs.getTime("endTime"));
		p.getAppraiser().setIdUser(rs.getInt("idAppraiser"));
		p.getAppraiser().setName(rs.getString("appraiserName"));
		p.setFile(rs.getBytes("file"));
		p.setAdditionalFile(rs.getBytes("additionalFile"));
		p.setComments(rs.getString("comments"));
		p.setSubstitute(rs.getInt("substitute") == 1);
		p.setChair(rs.getInt("chair") == 1);
		
		if(rs.getInt("idThesis") != 0){
			p.getJury().setThesis(new Thesis());
			p.getJury().getThesis().setIdThesis(rs.getInt("idThesis"));
			p.getJury().getThesis().setTitle(rs.getString("thesisTitle"));
			p.getJury().getThesis().getStudent().setName(rs.getString("thesisStudent"));
		}else{
			p.getJury().setProject(new Project());
			p.getJury().getProject().setIdProject(rs.getInt("idProject"));
			p.getJury().getProject().setTitle(rs.getString("projectTitle"));
			p.getJury().getProject().getStudent().setName(rs.getString("projectStudent"));
		}
		
		return p;
	}
	
	public boolean appraiserHasJury(int idJury, int idUser, Date juryDate) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT SUM(total) AS total FROM (" +
					"SELECT COUNT(*) AS total FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"INNER JOIN project ON project.idproject=jury.idproject " +
					"WHERE jury.idjury <> ? AND juryappraiser.idAppraiser = ? AND jury.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage1 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"WHERE proposal.idproposal=project.idproposal) AND ?::TIMESTAMP + INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage1 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"WHERE proposal.idproposal=project.idproposal) " +
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"INNER JOIN thesis ON thesis.idthesis=jury.idthesis " +
					"WHERE jury.idjury <> ? AND juryappraiser.idAppraiser = ? AND jury.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage2 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"INNER JOIN project ON project.idproposal=proposal.idproposal " +
					"WHERE project.idproject=thesis.idproject) AND ?::TIMESTAMP + INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage2 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"INNER JOIN project ON project.idproposal=proposal.idproposal " +
					"WHERE project.idproject=thesis.idproject) " +
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM internshipjury INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJury=internshipjury.idInternshipJury " +
					"WHERE internshipjury.juryformat=0 AND internshipjuryappraiser.idAppraiser = ? AND internshipjury.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * (" +
					"SELECT sigesconfig.jurytime - 1 FROM sigesconfig INNER JOIN internship ON internship.iddepartment=sigesconfig.iddepartment " +
					"WHERE internship.idinternship=internshipjury.idinternship) AND ?::TIMESTAMP + INTERVAL '1 minute' * (" +
					"SELECT sigesconfig.jurytime - 1 FROM sigesconfig INNER JOIN internship ON internship.iddepartment=sigesconfig.iddepartment " + 
					"WHERE internship.idinternship=internshipjury.idinternship)) AS teste");
			
			stmt.setInt(1, idJury);
			stmt.setInt(2, idUser);
			stmt.setTimestamp(3, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(4, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setInt(5, idJury);
			stmt.setInt(6, idUser);
			stmt.setTimestamp(7, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(8, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setInt(9, idUser);
			stmt.setTimestamp(10, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(11, new java.sql.Timestamp(juryDate.getTime()));
			
			System.out.println(stmt);
			
			rs = stmt.executeQuery();
			rs.next();
			
			return (rs.getInt("total") > 0);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}

}
