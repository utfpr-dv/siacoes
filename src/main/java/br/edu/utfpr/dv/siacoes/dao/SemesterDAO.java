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
import br.edu.utfpr.dv.siacoes.model.Semester;

public class SemesterDAO {
	
	public List<Semester> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT semester.*, campus.name AS campusName " +
					"FROM semester INNER JOIN campus ON campus.idcampus=semester.idcampus " +
					"ORDER BY semester.year DESC, semester.semester DESC, campus.name");
			
			List<Semester> list = new ArrayList<Semester>();
			
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
	
	public List<Semester> listByCampus(int idCampus) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT semester.*, campus.name AS campusName " +
					"FROM semester INNER JOIN campus ON campus.idcampus=semester.idcampus " +
					"WHERE semester.idcampus=" + String.valueOf(idCampus) +
					" ORDER BY semester.year DESC, semester.semester DESC");
			
			List<Semester> list = new ArrayList<Semester>();
			
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
	
	public Semester findBySemester(int idCampus, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT semester.*, campus.name AS campusName " +
					"FROM semester INNER JOIN campus ON campus.idcampus=semester.idcampus " +
					"WHERE semester.idcampus=? AND semester.semester=? AND semester.year=? " +
					"ORDER BY semester.year DESC, semester.semester DESC");
		
			stmt.setInt(1, idCampus);
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
	
	public Semester findByDate(int idCampus, Date date) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT semester.*, campus.name AS campusName " +
					"FROM semester INNER JOIN campus ON campus.idcampus=semester.idcampus " +
					"WHERE semester.idcampus=? AND semester.startDate <= ? AND semester.endDate >= ? " +
					"ORDER BY semester.year DESC, semester.semester DESC");
		
			stmt.setInt(1, idCampus);
			stmt.setDate(2, new java.sql.Date(date.getTime()));
			stmt.setDate(3, new java.sql.Date(date.getTime()));
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return new Semester(idCampus);
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
	
	public int save(int idUser, Semester semester) throws SQLException{
		boolean insert = (this.findBySemester(semester.getCampus().getIdCampus(), semester.getSemester(), semester.getYear()) == null);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO semester(startDate, endDate, idcampus, semester, year) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE semester SET startDate=?, endDate=? WHERE idcampus=? AND semester=? AND year=?");
			}
			
			stmt.setDate(1, new java.sql.Date(semester.getStartDate().getTime()));
			stmt.setDate(2, new java.sql.Date(semester.getEndDate().getTime()));
			stmt.setInt(3, semester.getCampus().getIdCampus());
			stmt.setInt(4, semester.getSemester());
			stmt.setInt(5, semester.getYear());
			
			stmt.execute();
			
			if(insert) {
				new UpdateEvent(conn).registerInsert(idUser, semester);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, semester);
			}
			
			return semester.getSemester();
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Semester loadObject(ResultSet rs) throws SQLException{
		Semester semester = new Semester();
		
		semester.getCampus().setIdCampus(rs.getInt("idcampus"));
		semester.getCampus().setName(rs.getString("campusName"));
		semester.setSemester(rs.getInt("semester"));
		semester.setYear(rs.getInt("year"));
		semester.setStartDate(rs.getDate("startDate"));
		semester.setEndDate(rs.getDate("endDate"));
		
		return semester;
	}

}
