package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryStudent;

public class InternshipJuryStudentDAO {
	
	private Connection conn;
	
	public InternshipJuryStudentDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public InternshipJuryStudentDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public InternshipJuryStudent findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
					"internshipjury.idInternship, istudent.name AS internshipStudent, company.name AS companyName " +
					"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
					"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE idInternshipJuryStudent = ?");
			
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
	
	public InternshipJuryStudent findByStudent(int idInternshipJury, int idStudent) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
					"internshipjury.idInternship, istudent.name AS internshipStudent, company.name AS companyName " +
					"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
					"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjurystudent.idInternshipJury = ? AND internshipjurystudent.idStudent=?");
			
			stmt.setInt(1, idInternshipJury);
			stmt.setInt(2, idStudent);
			
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
	
	public List<InternshipJuryStudent> listByJury(int idInternshipJury) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
					"internshipjury.idInternship, istudent.name AS internshipStudent, company.name AS companyName " +
					"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
					"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjurystudent.idInternshipJury = ?");
			
			stmt.setInt(1, idInternshipJury);
			
			List<InternshipJuryStudent> list = new ArrayList<InternshipJuryStudent>();
			
			rs = stmt.executeQuery();
			
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
	
	public List<InternshipJuryStudent> listByStudent(int idStudent) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
					"internshipjury.idInternship, istudent.name AS internshipStudent, company.name AS companyName " +
					"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
					"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
					"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
					"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
					"INNER JOIN company ON company.idCompany=internship.idCompany " +
					"WHERE internshipjurystudent.idStudent = ?");
			
			stmt.setInt(1, idStudent);
			
			List<InternshipJuryStudent> list = new ArrayList<InternshipJuryStudent>();
			
			rs = stmt.executeQuery();
			
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
	
	public int save(int idUser, InternshipJuryStudent student) throws SQLException{
		boolean insert = (student.getIdInternshipJuryStudent() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO internshipjurystudent(idInternshipJury, idStudent) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE internshipjurystudent SET idInternshipJury=?, idStudent=? WHERE idInternshipJuryStudent=?");
			}
			
			stmt.setInt(1, student.getInternshipJury().getIdInternshipJury());
			stmt.setInt(2, student.getStudent().getIdUser());
			
			if(!insert){
				stmt.setInt(3, student.getIdInternshipJuryStudent());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					student.setIdInternshipJuryStudent(rs.getInt(1));
				}

				new UpdateEvent(this.conn).registerInsert(idUser, student);
			} else {
				new UpdateEvent(this.conn).registerUpdate(idUser, student);
			}
			
			return student.getIdInternshipJuryStudent();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public boolean delete(int idUser, int id) throws SQLException{
		Statement stmt = this.conn.createStatement();
		InternshipJuryStudent student = this.findById(id);
		
		boolean ret = stmt.execute("DELETE FROM internshipjurystudent WHERE idInternshipJuryStudent = " + String.valueOf(id));
		
		new UpdateEvent(this.conn).registerDelete(idUser, student);
		
		return ret;
	}
	
	private InternshipJuryStudent loadObject(ResultSet rs) throws SQLException{
		InternshipJuryStudent student = new InternshipJuryStudent();
		
		student.setIdInternshipJuryStudent(rs.getInt("idInternshipJuryStudent"));
		student.getInternshipJury().setIdInternshipJury(rs.getInt("idInternshipJury"));
		student.getInternshipJury().setDate(rs.getDate("date"));
		student.getInternshipJury().setStartTime(rs.getTime("startTime"));
		student.getInternshipJury().setEndTime(rs.getTime("endTime"));
		student.getInternshipJury().setInternship(new Internship());
		student.getInternshipJury().getInternship().setIdInternship(rs.getInt("idInternship"));
		student.getInternshipJury().getInternship().getStudent().setName(rs.getString("internshipStudent"));
		student.getStudent().setIdUser(rs.getInt("idStudent"));
		student.getStudent().setName(rs.getString("name"));
		student.getStudent().setStudentCode(rs.getString("studentCode"));
		student.getInternshipJury().getInternship().getCompany().setName(rs.getString("companyName"));
		
		return student;
	}

}
