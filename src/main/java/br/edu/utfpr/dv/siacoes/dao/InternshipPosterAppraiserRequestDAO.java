package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;

public class InternshipPosterAppraiserRequestDAO {

private Connection conn;
	
	public InternshipPosterAppraiserRequestDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public InternshipPosterAppraiserRequestDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public InternshipPosterAppraiserRequest findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipposterappraiserrequest.*, appraiser.name as appraiserName, " +
					"internshipposterrequest.idinternship, internship.idStudent, student.name AS studentName " +
					"FROM internshipposterappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipposterappraiserrequest.idAppraiser " +
					"INNER JOIN internshipposterrequest ON internshipposterrequest.idinternshipposterrequest=internshipposterappraiserrequest.idinternshipposterrequest " +
					"INNER JOIN internship ON internship.idinternship=internshipposterrequest.idinternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"WHERE internshipposterappraiserrequest.idinternshipposterappraiserrequest=?");
			
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
	
	public InternshipPosterAppraiserRequest findByAppraiser(int idJuryRequest, int idUser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipposterappraiserrequest.*, appraiser.name as appraiserName, " +
					"internshipposterrequest.idinternship, internship.idStudent, student.name AS studentName " +
					"FROM internshipposterappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipposterappraiserrequest.idAppraiser " +
					"INNER JOIN internshipposterrequest ON internshipposterrequest.idinternshipposterrequest=internshipposterappraiserrequest.idinternshipposterrequest " +
					"INNER JOIN internship ON internship.idinternship=internshipposterrequest.idinternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"WHERE internshipposterrequest.idinternshipposterrequest=? AND internshipposterappraiserrequest.idAppraiser=?");
			
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
	
	public List<InternshipPosterAppraiserRequest> listAppraisers(int idJuryRequest) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT internshipposterappraiserrequest.*, appraiser.name as appraiserName, " +
					"internshipposterrequest.idinternship, internship.idStudent, student.name AS studentName " +
					"FROM internshipposterappraiserrequest INNER JOIN \"user\" appraiser ON appraiser.idUser=internshipposterappraiserrequest.idAppraiser " +
					"INNER JOIN internshipposterrequest ON internshipposterrequest.idinternshipposterrequest=internshipposterappraiserrequest.idinternshipposterrequest " +
					"INNER JOIN internship ON internship.idinternship=internshipposterrequest.idinternship " + 
					"INNER JOIN \"user\" student ON student.idUser=internship.idStudent " +
					"WHERE internshipposterappraiserrequest.idinternshipposterrequest = " + String.valueOf(idJuryRequest) + 
					" ORDER BY internshipposterappraiserrequest.substitute, appraiser.name");
			List<InternshipPosterAppraiserRequest> list = new ArrayList<InternshipPosterAppraiserRequest>();
			
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
	
	public int save(int idUser, InternshipPosterAppraiserRequest appraiser) throws SQLException{
		boolean insert = (appraiser.getIdInternshipPosterAppraiserRequest() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO internshipposterappraiserrequest(idinternshipposterrequest, idAppraiser, substitute) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE internshipposterappraiserrequest SET idinternshipposterrequest=?, idAppraiser=?, substitute=? WHERE idinternshipposterappraiserrequest=?");
			}
			
			stmt.setInt(1, appraiser.getRequest().getIdInternshipPosterRequest());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			stmt.setInt(3, (appraiser.isSubstitute() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(4, appraiser.getIdInternshipPosterAppraiserRequest());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					appraiser.setIdInternshipPosterAppraiserRequest(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, appraiser);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, appraiser);
			}
			
			return appraiser.getIdInternshipPosterAppraiserRequest();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private InternshipPosterAppraiserRequest loadObject(ResultSet rs) throws SQLException{
		InternshipPosterAppraiserRequest p = new InternshipPosterAppraiserRequest();
		
		p.setIdInternshipPosterAppraiserRequest(rs.getInt("idinternshipposterappraiserrequest"));
		p.getRequest().setIdInternshipPosterRequest(rs.getInt("idinternshipposterrequest"));
		p.getRequest().getInternship().setIdInternship(rs.getInt("idinternship"));
		p.getRequest().getInternship().getStudent().setName(rs.getString("studentName"));
		p.getRequest().getInternship().getStudent().setIdUser(rs.getInt("idStudent"));
		p.getAppraiser().setIdUser(rs.getInt("idAppraiser"));
		p.getAppraiser().setName(rs.getString("appraiserName"));
		p.setSubstitute(rs.getInt("substitute") == 1);
		
		return p;
	}
	
}
