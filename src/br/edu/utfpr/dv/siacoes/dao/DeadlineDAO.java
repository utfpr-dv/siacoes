package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Deadline;

public class DeadlineDAO {

	public Deadline findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM deadline WHERE idDeadline = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public Deadline findBySemester(int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM deadline WHERE idDepartment = ? AND semester = ? AND year = ?");
		
		stmt.setInt(1, idDepartment);
		stmt.setInt(2, semester);
		stmt.setInt(3, year);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<Deadline> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM deadline ORDER BY year DESC, semester DESC");
		List<Deadline> list = new ArrayList<Deadline>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public int save(Deadline deadline) throws SQLException{
		boolean insert = (deadline.getIdDeadline() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO deadline(idDepartment, semester, year, proposalDeadline, projectDeadline, thesisDeadline) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE deadline SET idDepartment=?, semester=?, year=?, proposalDeadline=?, projectDeadline=?, thesisDeadline=? WHERE idDeadline=?");
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
