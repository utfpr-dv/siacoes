package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;

public class InternshipJuryAppraiserDAO {
	
	private Connection conn;
	
	public InternshipJuryAppraiserDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public InternshipJuryAppraiserDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public InternshipJuryAppraiser findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null; 
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjuryappraiser.*, appraiser.name as appraiserName, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
					"internshipjury.idInternship, student.name AS studentName, company.name AS companyName " +
					"FROM internshipjuryappraiser INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipjuryappraiser.idAppraiser " +
					"INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjuryappraiser.idInternshipJury " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjuryappraiser.idInternshipJuryAppraiser=?");
			
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
	
	public InternshipJuryAppraiser findByAppraiser(int idInternshipJury, int idUser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjuryappraiser.*, appraiser.name as appraiserName, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
					"internshipjury.idInternship, student.name AS studentName, company.name AS companyName " +
					"FROM internshipjuryappraiser INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipjuryappraiser.idAppraiser " +
					"INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjuryappraiser.idInternshipJury " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjuryappraiser.idInternshipJury=? AND internshipjuryappraiser.idAppraiser=?");
			
			stmt.setInt(1, idInternshipJury);
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
	
	public List<InternshipJuryAppraiser> listAppraisers(int idInternshipJury) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internshipjuryappraiser.*, appraiser.name as appraiserName, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
					"internshipjury.idInternship, student.name AS studentName, company.name AS companyName " +
					"FROM internshipjuryappraiser INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipjuryappraiser.idAppraiser " +
					"INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjuryappraiser.idInternshipJury " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjuryappraiser.idInternshipJury = " + String.valueOf(idInternshipJury) +
					" ORDER BY appraiser.name");
			List<InternshipJuryAppraiser> list = new ArrayList<InternshipJuryAppraiser>();
			
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
	
	public int save(InternshipJuryAppraiser appraiser) throws SQLException{
		boolean insert = (appraiser.getIdInternshipJuryAppraiser() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO internshipjuryappraiser(idInternshipJury, idAppraiser, file, fileType, comments) VALUES(?, ?, NULL, 0, '')", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE internshipjuryappraiser SET idInternshipJury=?, idAppraiser=?, file=?, fileType=?, comments=? WHERE idInternshipJuryAppraiser=?");
			}
			
			stmt.setInt(1, appraiser.getInternshipJury().getIdInternshipJury());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			
			if(!insert){
				stmt.setBytes(3, appraiser.getFile());
				stmt.setInt(4, appraiser.getFileType().getValue());
				stmt.setString(5, appraiser.getComments());
				stmt.setInt(6, appraiser.getIdInternshipJuryAppraiser());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					appraiser.setIdInternshipJuryAppraiser(rs.getInt(1));
				}
			}
			
			return appraiser.getIdInternshipJuryAppraiser();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private InternshipJuryAppraiser loadObject(ResultSet rs) throws SQLException{
		InternshipJuryAppraiser p = new InternshipJuryAppraiser();
		
		p.setIdInternshipJuryAppraiser(rs.getInt("idInternshipJuryAppraiser"));
		p.getInternshipJury().setIdInternshipJury(rs.getInt("idInternshipJury"));
		p.getInternshipJury().setDate(rs.getDate("date"));
		p.getInternshipJury().setStartTime(rs.getTime("startTime"));
		p.getInternshipJury().setEndTime(rs.getTime("endTime"));
		p.getAppraiser().setIdUser(rs.getInt("idAppraiser"));
		p.getAppraiser().setName(rs.getString("appraiserName"));
		p.setFile(rs.getBytes("file"));
		p.setComments(rs.getString("comments"));
		p.setFileType(DocumentType.valueOf(rs.getInt("fileType")));
		p.getInternshipJury().setInternship(new Internship());
		p.getInternshipJury().getInternship().setIdInternship(rs.getInt("idInternship"));
		p.getInternshipJury().getInternship().getStudent().setName(rs.getString("studentName"));
		p.getInternshipJury().getInternship().getCompany().setName(rs.getString("companyName"));
		
		return p;
	}
	
	public boolean appraiserHasJury(int idInternshipJury, int idUser, Date startDate, Date endDate) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT SUM(total) AS total FROM (" +
					"SELECT COUNT(*) AS total FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"WHERE juryappraiser.idAppraiser = ? AND jury.date BETWEEN ? AND ? " +
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM internshipjury INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJury=internshipjury.idInternshipJury " +
					"WHERE internshipjury.idInternshipJury <> ? AND internshipjuryappraiser.idAppraiser = ? AND internshipjury.date BETWEEN ? AND ? ) AS teste");
			
			stmt.setInt(1, idUser);
			stmt.setTimestamp(2, new java.sql.Timestamp(startDate.getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(endDate.getTime()));
			stmt.setInt(4, idInternshipJury);
			stmt.setInt(5, idUser);
			stmt.setTimestamp(6, new java.sql.Timestamp(startDate.getTime()));
			stmt.setTimestamp(7, new java.sql.Timestamp(endDate.getTime()));
			
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
