package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Deadline;

public class DeadlineDAO {

	public Deadline findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM deadline WHERE idDeadline = ?");
		
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Deadline findBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM deadline WHERE idDepartment = ? AND semester = ? AND year = ?");
		
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Deadline> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM deadline ORDER BY year DESC, semester DESC");
			List<Deadline> list = new ArrayList<Deadline>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(Deadline deadline) throws SQLException{
		boolean insert = (deadline.getIdDeadline() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO deadline(idDepartment, semester, year, proposalDeadline, projectDeadline, thesisDeadline) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE deadline SET idDepartment=?, semester=?, year=?, proposalDeadline=?, projectDeadline=?, thesisDeadline=? WHERE idDeadline=?");
			}
			
			stmt.setInt(1, deadline.getDepartment().getIdDepartment());
			stmt.setInt(2, deadline.getSemester());
			stmt.setInt(3, deadline.getYear());
			stmt.setDate(4, new java.sql.Date(deadline.getProposalDeadline().getTime()));
			stmt.setDate(5, new java.sql.Date(deadline.getProjectDeadline().getTime()));
			stmt.setDate(6, new java.sql.Date(deadline.getThesisDeadline().getTime()));
			
			if(!insert){
				stmt.setInt(7, deadline.getIdDeadline());
			}
			
			stmt.execute();
			
			if(insert){
				ResultSet rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					deadline.setIdDeadline(rs.getInt(1));
				}
			}
			
			return deadline.getIdDeadline();
		}finally{
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
		d.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		
		return d;
	}
	
}
