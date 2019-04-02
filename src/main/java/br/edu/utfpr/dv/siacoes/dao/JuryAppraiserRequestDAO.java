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
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;

public class JuryAppraiserRequestDAO {
	
	private Connection conn;
	
	public JuryAppraiserRequestDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public JuryAppraiserRequestDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public JuryAppraiserRequest findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT juryappraiserrequest.*, appraiser.name as appraiserName, juryrequest.date, " +
					"juryrequest.idProposal, juryrequest.stage, proposal.idStudent, student.name AS studentName " +
					"FROM juryappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=juryappraiserrequest.idAppraiser " +
					"INNER JOIN juryrequest ON juryrequest.idJuryRequest=juryappraiserrequest.idJuryRequest " +
					"INNER JOIN proposal ON proposal.idProposal=juryrequest.idProposal " + 
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"WHERE juryappraiserrequest.idJuryAppraiserRequest=?");
			
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
	
	public JuryAppraiserRequest findByAppraiser(int idJuryRequest, int idUser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT juryappraiserrequest.*, appraiser.name as appraiserName, juryrequest.date, " +
					"juryrequest.idProposal, juryrequest.stage, proposal.idStudent, student.name AS studentName " +
					"FROM juryappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=juryappraiserrequest.idAppraiser " +
					"INNER JOIN juryrequest ON juryrequest.idJuryRequest=juryappraiserrequest.idJuryRequest " +
					"INNER JOIN proposal ON proposal.idProposal=juryrequest.idProposal " + 
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"WHERE juryappraiserrequest.idJuryRequest=? AND juryappraiserrequest.idAppraiser=?");
			
			stmt.setInt(1, idJuryRequest);
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
	
	public List<JuryAppraiserRequest> listAppraisers(int idJuryRequest) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT juryappraiserrequest.*, appraiser.name as appraiserName, juryrequest.date, " +
					"juryrequest.idProposal, juryrequest.stage, proposal.idStudent, student.name AS studentName " +
					"FROM juryappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=juryappraiserrequest.idAppraiser " +
					"INNER JOIN juryrequest ON juryrequest.idJuryRequest=juryappraiserrequest.idJuryRequest " +
					"INNER JOIN proposal ON proposal.idProposal=juryrequest.idProposal " + 
					"INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
					"WHERE juryappraiserrequest.idJuryRequest = " + String.valueOf(idJuryRequest) + 
					" ORDER BY juryappraiserrequest.chair DESC, juryappraiserrequest.substitute, appraiser.name");
			List<JuryAppraiserRequest> list = new ArrayList<JuryAppraiserRequest>();
			
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
	
	public int save(int idUser, JuryAppraiserRequest appraiser) throws SQLException{
		boolean insert = (appraiser.getIdJuryAppraiserRequest() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO juryappraiserrequest(idJuryRequest, idAppraiser, chair, substitute) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE juryappraiserrequest SET idJuryRequest=?, idAppraiser=?, chair=?, substitute=? WHERE idJuryAppraiserRequest=?");
			}
			
			stmt.setInt(1, appraiser.getJuryRequest().getIdJuryRequest());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			stmt.setInt(3, (appraiser.isChair() ? 1 : 0));
			stmt.setInt(4, (appraiser.isSubstitute() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(5, appraiser.getIdJuryAppraiserRequest());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					appraiser.setIdJuryAppraiserRequest(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, appraiser);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, appraiser);
			}
			
			return appraiser.getIdJuryAppraiserRequest();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private JuryAppraiserRequest loadObject(ResultSet rs) throws SQLException{
		JuryAppraiserRequest p = new JuryAppraiserRequest();
		
		p.setIdJuryAppraiserRequest(rs.getInt("idJuryAppraiserRequest"));
		p.getJuryRequest().setIdJuryRequest(rs.getInt("idJuryRequest"));
		p.getJuryRequest().setDate(rs.getDate("date"));
		p.getJuryRequest().setStage(rs.getInt("stage"));
		p.getJuryRequest().getProposal().setIdProposal(rs.getInt("idProposal"));
		p.getJuryRequest().getProposal().getStudent().setName(rs.getString("studentName"));
		p.getJuryRequest().getProposal().getStudent().setIdUser(rs.getInt("idStudent"));
		p.getAppraiser().setIdUser(rs.getInt("idAppraiser"));
		p.getAppraiser().setName(rs.getString("appraiserName"));
		p.setSubstitute(rs.getInt("substitute") == 1);
		p.setChair(rs.getInt("chair") == 1);
		
		return p;
	}
	
	public boolean appraiserHasJuryRequest(int idJuryRequest, int idUser, Date juryDate) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT COUNT(*) AS total FROM juryrequest INNER JOIN juryappraiserrequest ON juryappraiserrequest.idJuryRequest=juryrequest.idJuryRequest " +
					"WHERE juryrequest.idJuryRequest <> ? AND juryappraiserrequest.idAppraiser = ? " +
					"AND juryrequest.date BETWEEN ?::TIMESTAMP - INTERVAL '1 minute' * ( " +
					"SELECT CASE WHEN juryrequest.stage = 2 THEN sigetconfig.jurytimestage2 ELSE sigetconfig.jurytimestage1 END - 1 " +
					"FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " +
					"WHERE proposal.idproposal=juryrequest.idproposal) AND ?::TIMESTAMP + INTERVAL '1 minute' * ( " +
					"SELECT CASE WHEN juryrequest.stage = 2 THEN sigetconfig.jurytimestage2 ELSE sigetconfig.jurytimestage1 END - 1 " +
					"FROM sigetconfig INNER JOIN proposal ON proposal.iddepartment=sigetconfig.iddepartment " +
					"WHERE proposal.idproposal=juryrequest.idproposal)");
			
			stmt.setInt(1, idJuryRequest);
			stmt.setInt(2, idUser);
			stmt.setTimestamp(3, new java.sql.Timestamp(juryDate.getTime()));
			stmt.setTimestamp(4, new java.sql.Timestamp(juryDate.getTime()));
			
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
