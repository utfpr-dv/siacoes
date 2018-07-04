package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

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
	
	public int save(JuryAppraiser appraiser) throws SQLException{
		boolean insert = (appraiser.getIdJuryAppraiser() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO juryappraiser(idJury, idAppraiser, chair, substitute, file, fileType, comments) VALUES(?, ?, ?, ?, NULL, 0, '')", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE juryappraiser SET idJury=?, idAppraiser=?, chair=?, substitute=?, file=?, fileType=?, comments=? WHERE idJuryAppraiser=?");
			}
			
			stmt.setInt(1, appraiser.getJury().getIdJury());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			stmt.setInt(3, (appraiser.isChair() ? 1 : 0));
			stmt.setInt(4, (appraiser.isSubstitute() ? 1 : 0));
			
			if(!insert){
				stmt.setBytes(5, appraiser.getFile());
				stmt.setInt(6, appraiser.getFileType().getValue());
				stmt.setString(7, appraiser.getComments());
				stmt.setInt(8, appraiser.getIdJuryAppraiser());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					appraiser.setIdJuryAppraiser(rs.getInt(1));
				}
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
		p.setComments(rs.getString("comments"));
		p.setFileType(DocumentType.valueOf(rs.getInt("fileType")));
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
	
	public boolean appraiserHasJury(int idJury, int idUser, Date startDate, Date endDate) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT SUM(total) AS total FROM (" +
					"SELECT COUNT(*) AS total FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"INNER JOIN project ON project.idproject=jury.idproject " +
					"WHERE jury.idJury <> ? AND juryappraiser.idAppraiser = ? AND jury.date BETWEEN ? AND ? " +
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"INNER JOIN thesis ON thesis.idthesis=jury.idthesis " +
					"WHERE jury.idJury <> ? AND juryappraiser.idAppraiser = ? AND jury.date BETWEEN ? AND ? " +
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM internshipjury INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJury=internshipjury.idInternshipJury " +
					"WHERE internshipjuryappraiser.idAppraiser = ? AND internshipjury.date BETWEEN ? AND ? ) AS teste");
			
			stmt.setInt(1, idJury);
			stmt.setInt(2, idUser);
			stmt.setTimestamp(3, new java.sql.Timestamp(startDate.getTime()));
			stmt.setTimestamp(4, new java.sql.Timestamp(endDate.getTime()));
			stmt.setInt(5, idJury);
			stmt.setInt(6, idUser);
			stmt.setTimestamp(7, new java.sql.Timestamp(DateUtils.addMinute(startDate, -30).getTime()));
			stmt.setTimestamp(8, new java.sql.Timestamp(DateUtils.addMinute(endDate, 30).getTime()));
			stmt.setInt(9, idUser);
			stmt.setTimestamp(10, new java.sql.Timestamp(startDate.getTime()));
			stmt.setTimestamp(11, new java.sql.Timestamp(endDate.getTime()));
			
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
