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
	
	public byte[] getFile(int id) throws SQLException {
		if(id == 0){
			return null;
		}
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			this.conn = ConnectionDAO.getInstance().getConnection();
			stmt = this.conn.prepareStatement("SELECT internshipjuryappraiser.file FROM internshipjuryappraiser WHERE idInternshipJuryAppraiser = ?");
		
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
			stmt = this.conn.prepareStatement("SELECT internshipjuryappraiser.additionalFile FROM internshipjuryappraiser WHERE idInternshipJuryAppraiser = ?");
		
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
	
	public InternshipJuryAppraiser findChair(int idInternshipJury) throws SQLException{
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
					"WHERE internshipjuryappraiser.idInternshipJury=? AND internshipjuryappraiser.chair=1 AND internshipjuryappraiser.substitute=0");
			
			stmt.setInt(1, idInternshipJury);
			
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
					" ORDER BY internshipjuryappraiser.chair DESC, internshipjuryappraiser.substitute, appraiser.name");
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
	
	public int findIdDepartment(int idJuryAppraiser) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internship.idDepartment FROM internshipjuryappraiser INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjuryappraiser.idInternshipJury " + 
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"WHERE internshipjuryappraiser.idInternshipJuryAppraiser=?");
		
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
	
	public int save(int idUser, InternshipJuryAppraiser appraiser) throws SQLException{
		boolean insert = (appraiser.getIdInternshipJuryAppraiser() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO internshipjuryappraiser(idInternshipJury, idAppraiser, chair, substitute, file, additionalFile, comments) VALUES(?, ?, ?, ?, NULL, NULL, '')", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE internshipjuryappraiser SET idInternshipJury=?, idAppraiser=?, chair=?, substitute=?, file=?, additionalFile=?, comments=? WHERE idInternshipJuryAppraiser=?");
			}
			
			stmt.setInt(1, appraiser.getInternshipJury().getIdInternshipJury());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			stmt.setInt(3, (appraiser.isChair() ? 1 : 0));
			stmt.setInt(4, (appraiser.isSubstitute() ? 1 : 0));
			
			if(!insert){
				stmt.setBytes(5, appraiser.getFile());
				stmt.setBytes(6, appraiser.getAdditionalFile());
				stmt.setString(7, appraiser.getComments());
				stmt.setInt(8, appraiser.getIdInternshipJuryAppraiser());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					appraiser.setIdInternshipJuryAppraiser(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, appraiser);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, appraiser);
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
		p.setAdditionalFile(rs.getBytes("additionalFile"));
		p.setComments(rs.getString("comments"));
		p.setSubstitute(rs.getInt("substitute") == 1);
		p.setChair(rs.getInt("chair") == 1);
		p.getInternshipJury().setInternship(new Internship());
		p.getInternshipJury().getInternship().setIdInternship(rs.getInt("idInternship"));
		p.getInternshipJury().getInternship().getStudent().setName(rs.getString("studentName"));
		p.getInternshipJury().getInternship().getCompany().setName(rs.getString("companyName"));
		
		return p;
	}
	
	public boolean appraiserHasJury(int idInternshipJury, int idUser, Date juryDate) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT SUM(total) AS total FROM (" +
					"SELECT COUNT(*) AS total FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"INNER JOIN project ON project.idproject=jury.idproject " +
					"WHERE juryappraiser.idAppraiser = ? AND jury.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage1 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"WHERE proposal.idproposal=project.idproposal) AND ?::TIMESTAMP + INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage1 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"WHERE proposal.idproposal=project.idproposal) " +
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
					"INNER JOIN thesis ON thesis.idthesis=jury.idthesis " +
					"WHERE juryappraiser.idAppraiser = ? AND jury.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage2 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"INNER JOIN project ON project.idproposal=proposal.idproposal " +
					"WHERE project.idproject=thesis.idproject) AND ?::TIMESTAMP + INTERVAL '1 minute' * (" +
					"SELECT sigetconfig.jurytimestage2 - 1 FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " + 
					"INNER JOIN project ON project.idproposal=proposal.idproposal " +
					"WHERE project.idproject=thesis.idproject) " +
					" UNION ALL " +
					"SELECT COUNT(*) AS total FROM internshipjury INNER JOIN internshipjuryappraiser ON internshipjuryappraiser.idInternshipJury=internshipjury.idInternshipJury " +
					"WHERE internshipjury.juryformat=0 AND internshipjury.idInternshipJury <> ? AND internshipjuryappraiser.idAppraiser = ? AND internshipjury.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * (" +
					"SELECT sigesconfig.jurytime - 1 FROM sigesconfig INNER JOIN internship ON internship.iddepartment=sigesconfig.iddepartment " +
					"WHERE internship.idinternship=internshipjury.idinternship) AND ?::TIMESTAMP + INTERVAL '1 minute' * (" +
					"SELECT sigesconfig.jurytime - 1 FROM sigesconfig INNER JOIN internship ON internship.iddepartment=sigesconfig.iddepartment " + 
					"WHERE internship.idinternship=internshipjury.idinternship)) AS teste");
			
			stmt.setInt(1, idUser);
			stmt.setTimestamp(2, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setInt(4, idUser);
			stmt.setTimestamp(5, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(6, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setInt(7, idInternshipJury);
			stmt.setInt(8, idUser);
			stmt.setTimestamp(9, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(10, new java.sql.Timestamp(juryDate.getTime()));
			
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
