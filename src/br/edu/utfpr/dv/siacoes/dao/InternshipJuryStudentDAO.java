package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
		PreparedStatement stmt = this.conn.prepareStatement(
				"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
				"internshipjury.idInternship, istudent.name AS internshipStudent " +
				"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
				"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
				"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
				"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
				"WHERE idInternshipJuryStudent = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public InternshipJuryStudent findByStudent(int idInternshipJury, int idStudent) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement(
				"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
				"internshipjury.idInternship, istudent.name AS internshipStudent " +
				"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
				"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
				"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
				"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
				"WHERE internshipjurystudent.idInternshipJury = ? AND internshipjurystudent.idStudent=?");
		
		stmt.setInt(1, idInternshipJury);
		stmt.setInt(2, idStudent);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<InternshipJuryStudent> listByJury(int idInternshipJury) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement(
				"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
				"internshipjury.idInternship, istudent.name AS internshipStudent " +
				"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
				"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
				"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
				"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
				"WHERE internshipjurystudent.idInternshipJury = ?");
		
		stmt.setInt(1, idInternshipJury);
		
		List<InternshipJuryStudent> list = new ArrayList<InternshipJuryStudent>();
		
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<InternshipJuryStudent> listByStudent(int idStudent) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement(
				"SELECT internshipjurystudent.*, student.name, student.studentCode, internshipjury.date, internshipjury.startTime, internshipjury.endTime, " +
				"internshipjury.idInternship, istudent.name AS internshipStudent " +
				"FROM internshipjurystudent INNER JOIN internshipjury ON internshipjury.idInternshipJury=internshipjurystudent.idInternshipJury " +
				"INNER JOIN \"user\" student ON student.idUser=internshipjurystudent.idStudent " +
				"INNER JOIN internship ON internship.idInternship=internshipjury.idInternship " + 
				"INNER JOIN \"user\" istudent ON istudent.idUser=internship.idStudent " +
				"WHERE internshipjurystudent.idStudent = ?");
		
		stmt.setInt(1, idStudent);
		
		List<InternshipJuryStudent> list = new ArrayList<InternshipJuryStudent>();
		
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(InternshipJuryStudent student) throws SQLException{
		boolean insert = (student.getIdInternshipJuryStudent() == 0);
		PreparedStatement stmt;
		
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
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				student.setIdInternshipJuryStudent(rs.getInt(1));
			}
		}
		
		return student.getIdInternshipJuryStudent();
	}
	
	public boolean delete(int id) throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		return stmt.execute("DELETE FROM internshipjurystudent WHERE idInternshipJuryStudent = " + String.valueOf(id));
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
		
		return student;
	}

}
