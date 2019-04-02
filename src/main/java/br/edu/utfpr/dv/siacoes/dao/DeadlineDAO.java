package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Deadline;

public class DeadlineDAO {

	public Deadline findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM deadline WHERE idDeadline = ?");
		
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
	
	public Deadline findBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM deadline WHERE idDepartment = ? AND semester = ? AND year = ?");
		
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
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
	
	public List<Deadline> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM deadline ORDER BY year DESC, semester DESC");
			List<Deadline> list = new ArrayList<Deadline>();
			
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
	
	public List<Deadline> listByDepartment(int idDepartment) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM deadline WHERE idDepartment=" + String.valueOf(idDepartment) + " ORDER BY year DESC, semester DESC");
			List<Deadline> list = new ArrayList<Deadline>();
			
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
	
	public int save(int idUser, Deadline deadline) throws SQLException{
		boolean insert = (deadline.getIdDeadline() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO deadline(idDepartment, semester, year, proposalDeadline, projectDeadline, thesisDeadline, projectfinaldocumentdeadline, thesisfinaldocumentdeadline) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE deadline SET idDepartment=?, semester=?, year=?, proposalDeadline=?, projectDeadline=?, thesisDeadline=?, projectfinaldocumentdeadline=?, thesisfinaldocumentdeadline=? WHERE idDeadline=?");
			}
			
			stmt.setInt(1, deadline.getDepartment().getIdDepartment());
			stmt.setInt(2, deadline.getSemester());
			stmt.setInt(3, deadline.getYear());
			stmt.setDate(4, new java.sql.Date(deadline.getProposalDeadline().getTime()));
			stmt.setDate(5, new java.sql.Date(deadline.getProjectDeadline().getTime()));
			stmt.setDate(6, new java.sql.Date(deadline.getThesisDeadline().getTime()));
			stmt.setDate(7, new java.sql.Date(deadline.getProjectFinalDocumentDeadline().getTime()));
			stmt.setDate(8, new java.sql.Date(deadline.getThesisFinalDocumentDeadline().getTime()));
			
			if(!insert){
				stmt.setInt(9, deadline.getIdDeadline());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					deadline.setIdDeadline(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, deadline);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, deadline);
			}
			
			return deadline.getIdDeadline();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Deadline loadObject(ResultSet rs) throws SQLException{
		Deadline d = new Deadline();
		
		d.setIdDeadline(rs.getInt("idDeadline"));
		d.setSemester(rs.getInt("semester"));
		d.setYear(rs.getInt("year"));
		d.setProposalDeadline(rs.getDate("proposalDeadline"));
		d.setProjectDeadline(rs.getDate("projectDeadline"));
		d.setThesisDeadline(rs.getDate("thesisDeadline"));
		d.setProjectFinalDocumentDeadline(rs.getDate("projectfinaldocumentdeadline"));
		d.setThesisFinalDocumentDeadline(rs.getDate("thesisfinaldocumentdeadline"));
		d.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		
		return d;
	}
	
}
