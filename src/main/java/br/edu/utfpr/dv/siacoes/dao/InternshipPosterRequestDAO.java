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
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;

public class InternshipPosterRequestDAO {
	
	public List<InternshipPosterRequest> listByDate(int idDepartment, Date initialDate, Date finalDate) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internshipposterrequest.* FROM internshipposterrequest " +
					"INNER JOIN internship ON internshipposterrequest.idinternship=internship.idinternship " +
					"WHERE internship.iddepartment=? AND internshipposterrequest.requestdate BETWEEN ? AND ?");
		
			stmt.setInt(1, idDepartment);
			stmt.setTimestamp(2, new java.sql.Timestamp(initialDate.getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(finalDate.getTime()));
			
			rs = stmt.executeQuery();
			
			List<InternshipPosterRequest> list = new ArrayList<InternshipPosterRequest>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<InternshipPosterRequest> listByAppraiser(int idUser, Date initialDate, Date finalDate) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internshipposterrequest.* FROM internshipposterrequest " +
					"INNER JOIN internshipposterappraiserrequest ON internshipposterappraiserrequest.idinternshipposterrequest=internshipposterrequest.idinternshipposterrequest " +
					"WHERE internshipposterappraiserrequest.idappraiser=? AND internshipposterrequest.requestdate BETWEEN ? AND ?");
		
			stmt.setInt(1, idUser);
			stmt.setTimestamp(2, new java.sql.Timestamp(initialDate.getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(finalDate.getTime()));
			
			rs = stmt.executeQuery();
			
			List<InternshipPosterRequest> list = new ArrayList<InternshipPosterRequest>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public InternshipPosterRequest findById(int id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM internshipposterrequest WHERE idinternshipposterrequest = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return null;
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
	
	public InternshipPosterRequest findByInternship(int idInternship) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM internshipposterrequest WHERE idinternship = ?");
		
			stmt.setInt(1, idInternship);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return null;
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
	
	public int findIdDepartment(int idInternshipPosterRequest) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internship.iddepartment FROM internshipposterrequest " +
					"INNER JOIN internship ON internship.idinternship=internshipposterrequest.idinternship " +
					"WHERE internshipposterrequest.idinternshipposterrequest=?");
		
			stmt.setInt(1, idInternshipPosterRequest);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("idDepartment");
			}else{
				return 0;
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
	
	public int save(int idUser, InternshipPosterRequest request) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (request.getIdInternshipPosterRequest() == 0);
			
			if(insert) {
				stmt = conn.prepareStatement("INSERT INTO internshipposterrequest(idinternship, idinternshipjury, requestdate) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			} else {
				stmt = conn.prepareStatement("UPDATE internshipposterrequest SET idinternship=?, idinternshipjury=?, requestdate=? WHERE idinternshipposterrequest=?");
			}
			
			stmt.setInt(1, request.getInternship().getIdInternship());
			if((request.getJury() == null) || (request.getJury().getIdInternshipJury() == 0)) {
				stmt.setNull(2, Types.INTEGER);
			} else {
				stmt.setInt(2, request.getJury().getIdInternshipJury());
			}
			stmt.setTimestamp(3, new java.sql.Timestamp(request.getRequestDate().getTime()));
			
			if(!insert){
				stmt.setInt(4, request.getIdInternshipPosterRequest());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					request.setIdInternshipPosterRequest(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, request);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, request);
			}
			
			if(request.getAppraisers() != null) {
				InternshipPosterAppraiserRequestDAO dao = new InternshipPosterAppraiserRequestDAO(conn);
				String ids = "";
				
				for(InternshipPosterAppraiserRequest ja : request.getAppraisers()){
					ja.setRequest(request);
					int paId = dao.save(idUser, ja);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM internshipposterappraiserrequest WHERE idinternshipposterrequest=" + String.valueOf(request.getIdInternshipPosterRequest()) + 
						(ids.isEmpty() ? "" : " AND idinternshipposterappraiserrequest NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
			}
			
			conn.commit();
			
			return request.getIdInternshipPosterRequest();
		} catch(SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean delete(int idUser, int id) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		InternshipPosterRequest request = this.findById(id);
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			stmt = conn.createStatement();
		
			stmt.execute("DELETE FROM internshipposterappraiserrequest WHERE idinternshipposterrequest = " + String.valueOf(id));
			boolean ret = stmt.execute("DELETE FROM internshipposterrequest WHERE idinternshipposterrequest = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, request);
			
			conn.commit();
			
			return ret;
		} catch (SQLException e) {
			conn.rollback();
			
			throw e;
		} finally {
			conn.setAutoCommit(true);
			
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private InternshipPosterRequest loadObject(ResultSet rs) throws SQLException {
		InternshipPosterRequest request = new InternshipPosterRequest();
		
		request.setIdInternshipPosterRequest(rs.getInt("idinternshipposterrequest"));
		request.setInternship(new InternshipDAO().findById(rs.getInt("idinternship")));
		request.setRequestDate(rs.getTimestamp("requestdate"));
		if(rs.getInt("idinternshipjury") > 0) {
			request.setJury(new InternshipJury());
			request.getJury().setIdInternshipJury(rs.getInt("idinternshipjury"));
		}
		
		return request;
	}

}
