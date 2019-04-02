package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.JuryStudent;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Thesis;

public class JuryStudentDAO {
	
	private Connection conn;
	
	public JuryStudentDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public JuryStudentDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public JuryStudent findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT jurystudent.*, student.name, student.studentCode, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM jurystudent INNER JOIN jury ON jury.idJury=jurystudent.idJury " +
					"INNER JOIN \"user\" student ON student.idUser=jurystudent.idStudent " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE idJuryStudent = ?");
			
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
	
	public JuryStudent findByStudent(int idJury, int idStudent) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT jurystudent.*, student.name, student.studentCode, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM jurystudent INNER JOIN jury ON jury.idJury=jurystudent.idJury " +
					"INNER JOIN \"user\" student ON student.idUser=jurystudent.idStudent " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE jurystudent.idJury = ? AND jurystudent.idStudent=?");
			
			stmt.setInt(1, idJury);
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
	
	public List<JuryStudent> listByJury(int idJury) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT jurystudent.*, student.name, student.studentCode, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM jurystudent INNER JOIN jury ON jury.idJury=jurystudent.idJury " +
					"INNER JOIN \"user\" student ON student.idUser=jurystudent.idStudent " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE jurystudent.idJury = ?");
			
			stmt.setInt(1, idJury);
			
			List<JuryStudent> list = new ArrayList<JuryStudent>();
			
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
	
	public List<JuryStudent> listByStudent(int idStudent) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement(
					"SELECT jurystudent.*, student.name, student.studentCode, jury.date, jury.startTime, jury.endTime, " +
					"jury.idThesis, jury.idProject, thesis.title AS thesisTitle, project.title AS projectTitle, tstudent.name AS thesisStudent, pstudent.name AS projectStudent " +
					"FROM jurystudent INNER JOIN jury ON jury.idJury=jurystudent.idJury " +
					"INNER JOIN \"user\" student ON student.idUser=jurystudent.idStudent " +
					"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " + 
					"LEFT JOIN project ON project.idProject=jury.idProject " +
					"LEFT JOIN \"user\" tstudent ON tstudent.idUser=thesis.idStudent " +
					"LEFT JOIN \"user\" pstudent ON pstudent.idUser=project.idStudent " +
					"WHERE jurystudent.idStudent = ?");
			
			stmt.setInt(1, idStudent);
			
			List<JuryStudent> list = new ArrayList<JuryStudent>();
			
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
	
	public int save(int idUser, JuryStudent student) throws SQLException{
		boolean insert = (student.getIdJuryStudent() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO jurystudent(idJury, idStudent) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE jurystudent SET idJury=?, idStudent=? WHERE idJuryStudent=?");
			}
			
			stmt.setInt(1, student.getJury().getIdJury());
			stmt.setInt(2, student.getStudent().getIdUser());
			
			if(!insert){
				stmt.setInt(3, student.getIdJuryStudent());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					student.setIdJuryStudent(rs.getInt(1));
				}

				new UpdateEvent(this.conn).registerInsert(idUser, student);
			} else {
				new UpdateEvent(this.conn).registerUpdate(idUser, student);
			}
			
			return student.getIdJuryStudent();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public boolean delete(int idUser, int id) throws SQLException{
		Statement stmt = null;
		JuryStudent student = this.findById(id);
		
		try{
			stmt = this.conn.createStatement();
			
			boolean ret = stmt.execute("DELETE FROM jurystudent WHERE idJuryStudent = " + String.valueOf(id));
			
			new UpdateEvent(this.conn).registerDelete(idUser, student);
			
			return ret;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private JuryStudent loadObject(ResultSet rs) throws SQLException{
		JuryStudent student = new JuryStudent();
		
		student.setIdJuryStudent(rs.getInt("idJuryStudent"));
		student.getJury().setIdJury(rs.getInt("idJury"));
		student.getJury().setDate(rs.getDate("date"));
		student.getJury().setStartTime(rs.getTime("startTime"));
		student.getJury().setEndTime(rs.getTime("endTime"));
		
		if(rs.getInt("idThesis") != 0){
			student.getJury().setThesis(new Thesis());
			student.getJury().getThesis().setIdThesis(rs.getInt("idThesis"));
			student.getJury().getThesis().setTitle(rs.getString("thesisTitle"));
			student.getJury().getThesis().getStudent().setName(rs.getString("thesisStudent"));
		}else{
			student.getJury().setProject(new Project());
			student.getJury().getProject().setIdProject(rs.getInt("idProject"));
			student.getJury().getProject().setTitle(rs.getString("projectTitle"));
			student.getJury().getProject().getStudent().setName(rs.getString("projectStudent"));
		}
		
		student.getStudent().setIdUser(rs.getInt("idStudent"));
		student.getStudent().setName(rs.getString("name"));
		student.getStudent().setStudentCode(rs.getString("studentCode"));
		
		return student;
	}

}
