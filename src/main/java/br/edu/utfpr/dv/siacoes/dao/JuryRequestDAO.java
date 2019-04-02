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
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;

public class JuryRequestDAO {
	
	public JuryRequest findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM juryrequest WHERE idJuryRequest = ?");
		
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public JuryRequest findByStage(int idProposal, int stage) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM juryrequest WHERE idProposal = ? AND stage = ?");
		
			stmt.setInt(1, idProposal);
			stmt.setInt(2, stage);
			
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public JuryRequest findByProject(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT juryrequest.* FROM juryrequest " +
					"INNER JOIN project ON project.idProposal=juryrequest.idProposal " +
					"WHERE project.idProject = ?");
		
			stmt.setInt(1, idProject);
			
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public JuryRequest findByThesis(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT juryrequest.* FROM juryrequest " +
					"INNER JOIN project ON project.idProposal=juryrequest.idProposal " +
					"INNER JOIN thesis ON thesis.idProject=project.idProject " +
					"WHERE thesis.idThesis = ?");
		
			stmt.setInt(1, idThesis);
			
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int findIdDepartment(int idJuryRequest) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.iddepartment FROM juryrequest " +
					"INNER JOIN proposal ON proposal.idproposal=juryrequest.idproposal " +
					"WHERE juryrequest.idjuryrequest=?");
		
			stmt.setInt(1, idJuryRequest);
			
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
	
	public List<JuryRequest> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT juryrequest.* FROM juryrequest " +
					"INNER JOIN proposal ON proposal.idProposal=juryrequest.idProposal " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " + 
					"INNER JOIN semester ON (semester.idCampus=department.idCampus AND semester.year=" + String.valueOf(year) + " AND semester.semester=" + String.valueOf(semester) + ") " + 
					"LEFT JOIN project ON project.idProposal=proposal.idProposal " + 
					"LEFT JOIN thesis ON thesis.idProject=project.idProject " + 
					"WHERE juryrequest.date BETWEEN semester.startDate AND semester.endDate AND proposal.idDepartment=" + String.valueOf(idDepartment) + 
					" ORDER BY date");
			
			List<JuryRequest> list = new ArrayList<JuryRequest>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
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
	
	public List<JuryRequest> listByAppraiser(int idUser, int semester, int year) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT juryrequest.* FROM juryrequest " +
					"INNER JOIN juryappraiserrequest ON juryappraiserrequest.idJuryRequest=juryrequest.idJuryRequest " +
					"INNER JOIN proposal ON proposal.idProposal=juryrequest.idProposal " +
					"INNER JOIN department ON department.idDepartment=proposal.idDepartment " + 
					"INNER JOIN semester ON (semester.idCampus=department.idCampus AND semester.year=" + String.valueOf(year) + " AND semester.semester=" + String.valueOf(semester) + ") " + 
					"LEFT JOIN project ON project.idProposal=proposal.idProposal " + 
					"LEFT JOIN thesis ON thesis.idProject=project.idProject " + 
					"WHERE juryrequest.date BETWEEN semester.startDate AND semester.endDate AND juryappraiserrequest.idAppraiser=" + String.valueOf(idUser) + 
					" ORDER BY date");
			
			List<JuryRequest> list = new ArrayList<JuryRequest>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
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
	
	public int save(int idUser, JuryRequest jury) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (jury.getIdJuryRequest() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO juryrequest(date, local, idProposal, stage, comments, supervisorAbsenceReason, idJury) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE juryrequest SET date=?, local=?, idProposal=?, stage=?, comments=?, supervisorAbsenceReason=?, idJury=? WHERE idJuryRequest=?");
			}
			
			stmt.setTimestamp(1, new java.sql.Timestamp(jury.getDate().getTime()));
			stmt.setString(2, jury.getLocal());
			stmt.setInt(3, jury.getProposal().getIdProposal());
			stmt.setInt(4, jury.getStage());
			stmt.setString(5, jury.getComments());
			stmt.setString(6, jury.getSupervisorAbsenceReason());
			if((jury.getJury() == null) || (jury.getJury().getIdJury() == 0)){
				stmt.setNull(7, Types.INTEGER);
			}else{
				stmt.setInt(7, jury.getJury().getIdJury());
			}
			
			if(!insert){
				stmt.setInt(8, jury.getIdJuryRequest());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					jury.setIdJuryRequest(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, jury);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, jury);
			}
			
			if(jury.getAppraisers() != null) {
				JuryAppraiserRequestDAO dao = new JuryAppraiserRequestDAO(conn);
				String ids = "";
				
				for(JuryAppraiserRequest ja : jury.getAppraisers()){
					ja.setJuryRequest(jury);
					int paId = dao.save(idUser, ja);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				Statement st = conn.createStatement();
				st.execute("DELETE FROM juryappraiserrequest WHERE idJuryRequest=" + String.valueOf(jury.getIdJuryRequest()) + 
						(ids.isEmpty() ? "" : " AND idJuryAppraiserRequest NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")"));
			}
			
			conn.commit();
			
			return jury.getIdJuryRequest();
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
	
	public boolean delete(int idUser, int id) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		JuryRequest jury = this.findById(id);
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			stmt = conn.createStatement();
		
			stmt.execute("DELETE FROM juryappraiserrequest WHERE idJuryRequest = " + String.valueOf(id));
			boolean ret = stmt.execute("DELETE FROM juryrequest WHERE idJuryRequest = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, jury);
			
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
	
	private JuryRequest loadObject(ResultSet rs) throws SQLException{
		JuryRequest jury = new JuryRequest();
		
		jury.setIdJuryRequest(rs.getInt("idJuryRequest"));
		jury.setDate(rs.getTimestamp("date"));
		jury.setLocal(rs.getString("local"));
		jury.setComments(rs.getString("comments"));
		jury.setSupervisorAbsenceReason(rs.getString("supervisorAbsenceReason"));
		jury.setStage(rs.getInt("stage"));
		jury.getProposal().setIdProposal(rs.getInt("idProposal"));
		if(rs.getInt("idJury") > 0) {
			jury.setJury(new Jury());
			jury.getJury().setIdJury(rs.getInt("idJury"));
		}
		
		return jury;
	}

}
