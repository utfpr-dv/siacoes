package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;

public class InternshipFinalDocumentDAO {
	
	public DocumentFeedback findFeedback(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT supervisorFeedback FROM internshipfinaldocument WHERE idInternshipFinalDocument=" + String.valueOf(id));
			
			if(rs.next()) {
				return DocumentFeedback.valueOf(rs.getInt("supervisorFeedback"));
			} else {
				return DocumentFeedback.NONE;
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
	
	public byte[] getFile(int id) throws SQLException {
		if(id == 0){
			return null;
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT internshipfinaldocument.file FROM internshipfinaldocument WHERE idInternshipFinalDocument = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getBytes("file");
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
	
	public InternshipFinalDocument findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT internshipfinaldocument.*, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
				"s.name AS student, s2.name AS supervisor, c.name AS company " +
				"FROM internshipfinaldocument INNER JOIN internship ON internship.idInternship=internshipfinaldocument.idInternship " +
				"INNER JOIN \"user\" s ON s.idUser=internship.idStudent " +
				"INNER JOIN \"user\" s2 ON s2.idUser=internship.idSupervisor " +
				"INNER JOIN company c ON c.idCompany=internship.idCompany " +
				"WHERE internshipfinaldocument.idinternshipfinaldocument=?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs, true);
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
	
	public InternshipFinalDocument findByInternship(int idInternship) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT internshipfinaldocument.*, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
				"s.name AS student, s2.name AS supervisor, c.name AS company " +
				"FROM internshipfinaldocument INNER JOIN internship ON internship.idInternship=internshipfinaldocument.idInternship " +
				"INNER JOIN \"user\" s ON s.idUser=internship.idStudent " +
				"INNER JOIN \"user\" s2 ON s2.idUser=internship.idSupervisor " +
				"INNER JOIN company c ON c.idCompany=internship.idCompany " +
				"WHERE internshipfinaldocument.idinternship=?");
		
			stmt.setInt(1, idInternship);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs, true);
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
	
	public List<InternshipFinalDocument> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT internshipfinaldocument.*, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
					"s.name AS student, s2.name AS supervisor, c.name AS company " +
					"FROM internshipfinaldocument INNER JOIN internship ON internship.idInternship=internshipfinaldocument.idInternship " +
					"INNER JOIN \"user\" s ON s.idUser=internship.idStudent " +
					"INNER JOIN \"user\" s2 ON s2.idUser=internship.idSupervisor " +
					"INNER JOIN company c ON c.idCompany=internship.idCompany " +
					"ORDER BY internshipfinaldocument.submissionDate DESC, internshipfinaldocument.title");
			
			List<InternshipFinalDocument> list = new ArrayList<InternshipFinalDocument>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
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
	
	public List<InternshipFinalDocument> listByDepartment(int idDepartment, boolean includePrivate) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT internshipfinaldocument.*, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
					"s.name AS student, s2.name AS supervisor, c.name AS company " +
					"FROM internshipfinaldocument INNER JOIN internship ON internship.idInternship=internshipfinaldocument.idInternship " +
					"INNER JOIN \"user\" s ON s.idUser=internship.idStudent " +
					"INNER JOIN \"user\" s2 ON s2.idUser=internship.idSupervisor " +
					"INNER JOIN company c ON c.idCompany=internship.idCompany " +
					"WHERE internshipfinaldocument.supervisorFeedback=1 AND internship.idDepartment=? " + (includePrivate ? "" : " AND internshipfinaldocument.private=0 ") +
					"ORDER BY internshipfinaldocument.submissionDate DESC, internshipfinaldocument.title");
		
			stmt.setInt(1, idDepartment);
			
			rs = stmt.executeQuery();
			
			List<InternshipFinalDocument> list = new ArrayList<InternshipFinalDocument>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
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
	
	public List<InternshipFinalDocument> listBySemester(int idDepartment, int semester, int year, boolean includePrivate) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT internshipfinaldocument.*, internship.idStudent, internship.idSupervisor, internship.idCompany, " +
					"s.name AS student, s2.name AS supervisor, c.name AS company " +
					"FROM internshipfinaldocument INNER JOIN internship ON internship.idInternship=internshipfinaldocument.idInternship " +
					"INNER JOIN internshipjury ON internshipjury.idinternship=internship.idinternship " +
					"INNER JOIN \"user\" s ON s.idUser=internship.idStudent " +
					"INNER JOIN \"user\" s2 ON s2.idUser=internship.idSupervisor " +
					"INNER JOIN company c ON c.idCompany=internship.idCompany " +
					"WHERE MONTH(internshipjury.date) " + (semester == 1 ? "<= 7" : "> 7") + " AND YEAR(internshipjury.date)=? AND internship.idDepartment=? " + (includePrivate ? "" : " AND internshipfinaldocument.private=0 ") +
					"ORDER BY internshipfinaldocument.submissionDate DESC, internshipfinaldocument.title");
		
			stmt.setInt(1, year);
			stmt.setInt(2, idDepartment);
			
			rs = stmt.executeQuery();
			
			List<InternshipFinalDocument> list = new ArrayList<InternshipFinalDocument>();
			
			while(rs.next()){
				list.add(this.loadObject(rs, false));
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
	
	public int save(int idUser, InternshipFinalDocument thesis) throws SQLException{
		boolean insert = (thesis.getIdInternshipFinalDocument() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO internshipfinaldocument(idInternship, title, file, submissionDate, private, supervisorFeedback, supervisorFeedbackDate, comments) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE internshipfinaldocument SET idInternship=?, title=?, file=?, submissionDate=?, private=?, supervisorFeedback=?, supervisorFeedbackDate=?, comments=? WHERE idinternshipfinaldocument=?");
			}
			
			stmt.setInt(1, thesis.getInternship().getIdInternship());
			stmt.setString(2, thesis.getTitle());
			stmt.setBytes(3, thesis.getFile());
			stmt.setDate(4, new java.sql.Date(thesis.getSubmissionDate().getTime()));
			stmt.setInt(5, (thesis.isPrivate() ? 1 : 0));
			stmt.setInt(6, thesis.getSupervisorFeedback().getValue());
			if(thesis.getSupervisorFeedbackDate() == null){
				stmt.setNull(7, Types.DATE);
			}else{
				stmt.setDate(7, new java.sql.Date(thesis.getSupervisorFeedbackDate().getTime()));	
			}
			stmt.setString(8, thesis.getComments());
			
			if(!insert){
				stmt.setInt(9, thesis.getIdInternshipFinalDocument());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					thesis.setIdInternshipFinalDocument(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, thesis);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, thesis);
			}
			
			return thesis.getIdInternshipFinalDocument();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private InternshipFinalDocument loadObject(ResultSet rs, boolean loadFiles) throws SQLException{
		InternshipFinalDocument ft = new InternshipFinalDocument();
		
		ft.setIdInternshipFinalDocument(rs.getInt("idinternshipfinaldocument"));
		ft.getInternship().setIdInternship(rs.getInt("idinternship"));
		ft.getInternship().getStudent().setIdUser(rs.getInt("idStudent"));
		ft.getInternship().getStudent().setName(rs.getString("student"));
		ft.getInternship().getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		ft.getInternship().getSupervisor().setName(rs.getString("supervisor"));
		ft.getInternship().getCompany().setIdCompany(rs.getInt("idCompany"));
		ft.getInternship().getCompany().setName(rs.getString("company"));
		ft.setTitle(rs.getString("title"));
		ft.setSubmissionDate(rs.getDate("submissionDate"));
		ft.setComments(rs.getString("comments"));
		ft.setSupervisorFeedbackDate(rs.getDate("supervisorFeedbackDate"));
		ft.setPrivate(rs.getInt("private") == 1);
		ft.setSupervisorFeedback(DocumentFeedback.valueOf(rs.getInt("supervisorFeedback")));
		
		if(loadFiles) {
			ft.setFile(rs.getBytes("file"));
		}
		
		return ft;
	}

}
