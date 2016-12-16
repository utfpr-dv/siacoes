package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.User;

public class FinalDocumentDAO {
	
	public FinalDocument findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT thesis.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, NULL AS idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN user ON user.idUser=thesis.idStudent " +
				"WHERE finaldocument.idFinalDocument=?" + 
				" UNION ALL " +
				"SELECT project.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN user ON user.idUser=project.idStudent " +
				"WHERE finaldocument.idFinalDocument=?");
		
		stmt.setInt(1, id);
		stmt.setInt(2, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public FinalDocument findCurrentThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT thesis.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, NULL AS idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=thesis.idStudent " +
				"WHERE thesis.idStudent=? AND proposal.idDepartment=? AND thesis.semester=? AND thesis.year=?");
		
		stmt.setInt(1, idStudent);
		stmt.setInt(2, idDepartment);
		stmt.setInt(3, semester);
		stmt.setInt(4, year);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public FinalDocument findCurrentProject(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT project.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=project.idStudent " +
				"WHERE project.idStudent=? AND proposal.idDepartment=? AND project.semester=? AND project.year=?");
		
		stmt.setInt(1, idStudent);
		stmt.setInt(2, idDepartment);
		stmt.setInt(3, semester);
		stmt.setInt(4, year);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<FinalDocument> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT thesis.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, NULL AS idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=thesis.idStudent " +
				" UNION ALL " + 
				"SELECT project.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=project.idStudent " +
				"ORDER BY year DESC, semester DESC, title");
		
		List<FinalDocument> list = new ArrayList<FinalDocument>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<FinalDocument> listByDepartment(int idDepartment) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT thesis.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, NULL AS idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=thesis.idStudent " +
				"WHERE proposal.idDepartment=? " +
				" UNION ALL " +
				"SELECT project.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=project.idStudent " +
				"WHERE proposal.idDepartment=? " +
				"ORDER BY year DESC, semester DESC, title");
		
		stmt.setInt(1, idDepartment);
		stmt.setInt(2, idDepartment);
		
		ResultSet rs = stmt.executeQuery();
		
		List<FinalDocument> list = new ArrayList<FinalDocument>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<FinalDocument> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT thesis.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"thesis.semester, thesis.year, thesis.idStudent, thesis.idSupervisor, thesis.idCosupervisor, thesis.subarea, NULL AS idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN thesis ON thesis.idThesis=finaldocument.idThesis " +
				"INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=thesis.idStudent " +
				"WHERE thesis.semester=? AND thesis.year=? AND proposal.idDepartment=? " +
				" UNION ALL " +
				"SELECT project.idProject, finaldocument.idThesis, finaldocument.title, finaldocument.submissionDate, finaldocument.file, " +
				"project.semester, project.year, project.idStudent, project.idSupervisor, project.idCosupervisor, project.subarea, project.idProposal, user.name AS student " +
				"FROM finaldocument INNER JOIN project ON project.idProject=finaldocument.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user ON user.idUser=project.idStudent " +
				"WHERE project.semester=? AND project.year=? AND proposal.idDepartment=? " +
				"ORDER BY title");
		
		stmt.setInt(1, semester);
		stmt.setInt(2, year);
		stmt.setInt(3, idDepartment);
		
		stmt.setInt(4, semester);
		stmt.setInt(5, year);
		stmt.setInt(6, idDepartment);
		
		ResultSet rs = stmt.executeQuery();
		
		List<FinalDocument> list = new ArrayList<FinalDocument>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(FinalDocument thesis) throws SQLException{
		boolean insert = (thesis.getIdFinalDocument() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO finaldocument(idProject, idThesis, title, file, submissionDate) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE finaldocument SET idProject=?, idThesis=?, title=?, file=?, submissionDate=? WHERE idFinalDocument=?");
		}
		
		stmt.setInt(1, thesis.getProject().getIdProject());
		stmt.setInt(2, thesis.getThesis().getIdThesis());
		stmt.setString(3, thesis.getTitle());
		stmt.setBytes(4, thesis.getFile());
		stmt.setDate(5, new java.sql.Date(thesis.getSubmissionDate().getTime()));
		
		if(!insert){
			stmt.setInt(6, thesis.getIdFinalDocument());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				thesis.setIdFinalDocument(rs.getInt(1));
			}
		}
		
		return thesis.getIdFinalDocument();
	}
	
	private FinalDocument loadObject(ResultSet rs) throws SQLException{
		FinalDocument ft = new FinalDocument();
		
		ft.setIdFinalDocument(rs.getInt("idFinalDocument"));
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
		}
		ft.setTitle(rs.getString("title"));
		ft.setSubmissionDate(rs.getDate("submissionDate"));
		ft.setFile(rs.getBytes("file"));
		
		return ft;
	}

}
