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
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.FinalDocument.DocumentFeedback;
import br.edu.utfpr.dv.siacoes.model.LibraryReport;
import br.edu.utfpr.dv.siacoes.model.User;

public class FinalDocumentDAO {
	
	public DocumentFeedback findFeedback(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT supervisorFeedback FROM finaldocument WHERE idFinalDocument=" + String.valueOf(id));
			
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
			stmt = conn.prepareStatement("SELECT finaldocument.file FROM finaldocument WHERE idFinalDocument = ?");
		
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
	
	public FinalDocument findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT thesis.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, finaldocument.abstract, finaldocument.abstract2, " +
			    "finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, NULL AS idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"WHERE finaldocument.idfinaldocument=?" + 
				" UNION ALL " +
				"SELECT project.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN \"user\" ON \"user\".idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"WHERE finaldocument.idfinaldocument=?");
		
			stmt.setInt(1, id);
			stmt.setInt(2, id);
			
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
	
	public FinalDocument findByProject(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT project.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN \"user\" ON \"user\".idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"WHERE finaldocument.idproject=?");
		
			stmt.setInt(1, idProject);
			
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
	
	public FinalDocument findByThesis(int idThesis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT thesis.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, finaldocument.abstract, finaldocument.abstract2, " +
			    "finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, 0 AS idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"WHERE finaldocument.idThesis=?");
		
			stmt.setInt(1, idThesis);
			
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
	
	public FinalDocument findCurrentThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT thesis.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"WHERE thesis.idStudent=? AND proposal.idDepartment=? AND thesis.semester=? AND thesis.year=?");
		
			stmt.setInt(1, idStudent);
			stmt.setInt(2, idDepartment);
			stmt.setInt(3, semester);
			stmt.setInt(4, year);
			
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
	
	public FinalDocument findCurrentProject(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT project.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"WHERE project.idStudent=? AND proposal.idDepartment=? AND project.semester=? AND project.year=?");
		
			stmt.setInt(1, idStudent);
			stmt.setInt(2, idDepartment);
			stmt.setInt(3, semester);
			stmt.setInt(4, year);
			
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
	
	public List<FinalDocument> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT thesis.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
					"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
					"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
					"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
					"INNER JOIN project ON project.idProject=thesis.idProject " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
					" UNION ALL " + 
					"SELECT project.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
					"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
					"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
					"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN \"user\" ON \"user\".idUser=project.idStudent " +
					"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
					"ORDER BY year DESC, semester DESC, student");
			
			List<FinalDocument> list = new ArrayList<FinalDocument>();
			
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
	
	public List<FinalDocument> listByDepartment(int idDepartment, boolean showThesis, boolean showProjects) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				(showThesis ? "SELECT thesis.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"WHERE finaldocument.supervisorFeedback=1 AND proposal.idDepartment=? " : "") +
				(showThesis && showProjects ? " UNION ALL " : "") +
				(showProjects ? "SELECT project.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"WHERE finaldocument.supervisorFeedback=1 AND proposal.idDepartment=? " : "") +
				"ORDER BY year DESC, semester DESC, student");
		
			if(showThesis) {
				stmt.setInt(1, idDepartment);	
			}
			if(showProjects) {
				stmt.setInt((showThesis ? 2 : 1), idDepartment);	
			}
			
			rs = stmt.executeQuery();
			
			List<FinalDocument> list = new ArrayList<FinalDocument>();
			
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
	
	public List<FinalDocument> listBySemester(int idDepartment, int semester, int year, boolean includePrivate) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT thesis.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"WHERE thesis.semester=? AND thesis.year=? AND proposal.idDepartment=? " + (includePrivate ? "" : " AND finaldocument.private=0 ") +
				" UNION ALL " +
				"SELECT project.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"WHERE project.semester=? AND project.year=? AND proposal.idDepartment=? " + (includePrivate ? "" : " AND finaldocument.private=0 ") +
				"ORDER BY student");
		
			stmt.setInt(1, semester);
			stmt.setInt(2, year);
			stmt.setInt(3, idDepartment);
			
			stmt.setInt(4, semester);
			stmt.setInt(5, year);
			stmt.setInt(6, idDepartment);
			
			rs = stmt.executeQuery();
			
			List<FinalDocument> list = new ArrayList<FinalDocument>();
			
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
	
	public List<FinalDocument> listBySupervisor(int idSupervisor, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT thesis.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"WHERE thesis.semester=? AND thesis.year=? AND thesis.idsupervisor=? " +
				" UNION ALL " +
				"SELECT project.idProject, finaldocument.idfinaldocument, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.abstract, finaldocument.abstract2, " +
				"finaldocument.private, finaldocument.supervisorFeedbackDate, finaldocument.supervisorFeedback, finaldocument.comments, finaldocument.companyInfo, finaldocument.patent, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, \"user\".name AS student, supervisor.name AS supervisorName " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=project.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=project.idSupervisor " +
				"WHERE project.semester=? AND project.year=? AND project.idsupervisor=? " +
				"ORDER BY student");
		
			stmt.setInt(1, semester);
			stmt.setInt(2, year);
			stmt.setInt(3, idSupervisor);
			
			stmt.setInt(4, semester);
			stmt.setInt(5, year);
			stmt.setInt(6, idSupervisor);
			
			rs = stmt.executeQuery();
			
			List<FinalDocument> list = new ArrayList<FinalDocument>();
			
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
	
	public int save(int idUser, FinalDocument thesis) throws SQLException{
		boolean insert = (thesis.getIdFinalDocument() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO finaldocument(idProject, idThesis, title, file, submissionDate, private, supervisorFeedback, supervisorFeedbackDate, comments, companyInfo, patent, abstract, abstract2) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE finaldocument SET idProject=?, idThesis=?, title=?, file=?, submissionDate=?, private=?, supervisorFeedback=?, supervisorFeedbackDate=?, comments=?, companyInfo=?, patent=?, abstract=?, abstract2=? WHERE idfinaldocument=?");
			}
			
			if((thesis.getProject() == null) || (thesis.getProject().getIdProject() == 0)){
				stmt.setNull(1, Types.INTEGER);
			}else{
				stmt.setInt(1, thesis.getProject().getIdProject());	
			}
			if((thesis.getThesis() == null) || (thesis.getThesis().getIdThesis() == 0)){
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setInt(2, thesis.getThesis().getIdThesis());	
			}
			stmt.setString(3, thesis.getTitle());
			stmt.setBytes(4, thesis.getFile());
			stmt.setDate(5, new java.sql.Date(thesis.getSubmissionDate().getTime()));
			stmt.setInt(6, (thesis.isPrivate() ? 1 : 0));
			stmt.setInt(7, thesis.getSupervisorFeedback().getValue());
			if(thesis.getSupervisorFeedbackDate() == null){
				stmt.setNull(8, Types.DATE);
			}else{
				stmt.setDate(8, new java.sql.Date(thesis.getSupervisorFeedbackDate().getTime()));	
			}
			stmt.setString(9, thesis.getComments());
			stmt.setInt(10, thesis.isCompanyInfo() ? 1 : 0);
			stmt.setInt(11, thesis.isPatent() ? 1 : 0);
			stmt.setString(12, thesis.getNativeAbstract());
			stmt.setString(13, thesis.getEnglishAbstract());
			
			if(!insert){
				stmt.setInt(14, thesis.getIdFinalDocument());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					thesis.setIdFinalDocument(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, thesis);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, thesis);
			}
			
			return thesis.getIdFinalDocument();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private FinalDocument loadObject(ResultSet rs, boolean loadFiles) throws SQLException{
		FinalDocument ft = new FinalDocument();
		
		ft.setIdFinalDocument(rs.getInt("idfinaldocument"));
		if(rs.getInt("idThesis") != 0){
			ft.getThesis().setIdThesis(rs.getInt("idThesis"));
			ft.getThesis().setSemester(rs.getInt("semester"));
			ft.getThesis().setYear(rs.getInt("year"));
			ft.getThesis().getStudent().setIdUser(rs.getInt("idStudent"));
			ft.getThesis().getSupervisor().setIdUser(rs.getInt("idSupervisor"));
			ft.getThesis().setCosupervisor(new User());
			ft.getThesis().getCosupervisor().setIdUser(rs.getInt("idCosupervisor"));
			ft.getThesis().setSubarea(rs.getString("subarea"));
			ft.getThesis().getProject().setIdProject(rs.getInt("idProject"));
			ft.getThesis().getStudent().setName(rs.getString("student"));
			ft.getThesis().getSupervisor().setName(rs.getString("supervisorName"));
		}else{
			ft.getProject().setIdProject(rs.getInt("idProject"));
			ft.getProject().setSemester(rs.getInt("semester"));
			ft.getProject().setYear(rs.getInt("year"));
			ft.getProject().getStudent().setIdUser(rs.getInt("idStudent"));
			ft.getProject().getSupervisor().setIdUser(rs.getInt("idSupervisor"));
			ft.getProject().setCosupervisor(new User());
			ft.getProject().getCosupervisor().setIdUser(rs.getInt("idCosupervisor"));
			ft.getProject().setSubarea(rs.getString("subarea"));
			ft.getProject().getProposal().setIdProposal(rs.getInt("idProposal"));
			ft.getProject().getStudent().setName(rs.getString("student"));
			ft.getProject().getSupervisor().setName(rs.getString("supervisorName"));
		}
		ft.setTitle(rs.getString("title"));
		ft.setSubmissionDate(rs.getDate("submissionDate"));
		ft.setComments(rs.getString("comments"));
		ft.setSupervisorFeedbackDate(rs.getDate("supervisorFeedbackDate"));
		ft.setPrivate(rs.getInt("private") == 1);
		ft.setCompanyInfo(rs.getInt("companyInfo") == 1);
		ft.setPatent(rs.getInt("patent") == 1);
		ft.setSupervisorFeedback(DocumentFeedback.valueOf(rs.getInt("supervisorFeedback")));
		ft.setNativeAbstract(rs.getString("abstract"));
		ft.setEnglishAbstract(rs.getString("abstract2"));
		
		if(loadFiles) {
			ft.setFile(rs.getBytes("file"));
		}
		
		return ft;
	}
	
	public List<LibraryReport> getLibraryReport(int idDepartment, int year, int semester) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT finaldocument.title, finaldocument.file, finaldocument.private, finaldocument.companyInfo, finaldocument.patent, \"user\".name AS student " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN \"user\" ON \"user\".idUser=thesis.idStudent " +
				"WHERE finaldocument.supervisorFeedback=1 AND thesis.semester=? AND thesis.year=? AND proposal.idDepartment=? " +
				"ORDER BY \"user\".name");
		
			stmt.setInt(1, semester);
			stmt.setInt(2, year);
			stmt.setInt(3, idDepartment);
			
			rs = stmt.executeQuery();
			
			List<LibraryReport> list = new ArrayList<LibraryReport>();
			int seq = 1;
			
			while(rs.next()){
				LibraryReport item = new LibraryReport();
				
				item.setSequence(seq);
				item.setTitle(rs.getString("title"));
				item.setStudent(rs.getString("student"));
				item.setCompanyInfo(rs.getInt("companyInfo") == 1);
				item.setPatent(rs.getInt("patent") == 1);
				item.setFile(rs.getBytes("file"));
				
				list.add(item);
				seq++;
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
	
	public long getTotalFinalThesis() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(idfinaldocument) AS total FROM finaldocument WHERE supervisorFeedback=1 AND idthesis IS NOT NULL");
			
			if(rs.next()) {
				return rs.getLong("total");
			} else {
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

}
