package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class ThesisDAO {

	public Thesis findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM thesis inner join user student on student.idUser=thesis.idStudent inner join user supervisor on supervisor.idUser=thesis.idSupervisor left join user cosupervisor on cosupervisor.idUser=thesis.idCosupervisor WHERE idThesis = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public Thesis findByProject(int idProject) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM thesis inner join user student on student.idUser=thesis.idStudent inner join user supervisor on supervisor.idUser=thesis.idSupervisor left join user cosupervisor on cosupervisor.idUser=thesis.idCosupervisor WHERE idProject = ?");
		
		stmt.setInt(1, idProject);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public int findIdJury(int idThesis) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idJury FROM jury WHERE idThesis=?");
		
		stmt.setInt(1, idThesis);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return rs.getInt("idJury");
		}else{
			return 0;
		}
	}
	
	public Thesis findCurrentThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
					"SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM thesis INNER JOIN project ON project.idProject=thesis.idThesis " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN user student ON student.idUser=thesis.idStudent " + 
					"INNER JOIN user supervisor ON supervisor.idUser=thesis.idSupervisor " + 
					"LEFT JOIN user cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE thesis.idStudent = ? AND proposal.idDepartment=? AND thesis.semester = ? AND thesis.year = ?");
		
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
	
	public Thesis findApprovedThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
					"SELECT * FROM (SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName, jury.minimumScore, " +
					"AVG((SELECT (SUM(juryAppraiserScore.score * evaluationItem.ponderosity) / SUM(evaluationItem.ponderosity)) " +
					"FROM juryAppraiser INNER JOIN juryAppraiserScore ON juryAppraiserScore.idJuryAppraiser = juryAppraiser.idJuryAppraiser " +
					"INNER JOIN evaluationItem ON evaluationItem.idEvaluationItem = juryAppraiserScore.idEvaluationItem " +
					"WHERE juryAppraiser.idJury = jury.idJury GROUP BY juryAppraiser.idJuryAppraiser)) AS score " +
					"FROM thesis INNER JOIN project ON project.idProject=thesis.idThesis " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN user student ON student.idUser=thesis.idStudent " + 
					"INNER JOIN user supervisor ON supervisor.idUser=thesis.idSupervisor " +
					"INNER JOIN jury ON jury.idThesis = thesis.idThesis " +
					"LEFT JOIN user cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE thesis.idStudent = ? AND proposal.idDepartment=? AND (thesis.year < " + String.valueOf(year) + " OR (thesis.year = " + String.valueOf(year) + " AND thesis.semester < " + String.valueOf(semester) + "))) AS p WHERE p.score >= p.minimumScore ORDER BY year DESC, semester DESC");
		
		stmt.setInt(1, idStudent);
		stmt.setInt(2, idDepartment);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public Thesis findLastThesis(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
					"SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM thesis INNER JOIN project ON project.idProject=thesis.idThesis " +
					"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
					"INNER JOIN user student ON student.idUser=thesis.idStudent " + 
					"INNER JOIN user supervisor ON supervisor.idUser=thesis.idSupervisor " + 
					"LEFT JOIN user cosupervisor ON cosupervisor.idUser=thesis.idCosupervisor " +
					"WHERE thesis.idStudent = ? AND proposal.idDepartment=? AND (thesis.year < " + String.valueOf(year) + " OR (thesis.year = " + String.valueOf(year) + " AND thesis.semester < " + String.valueOf(semester) + ")) ORDER BY thesis.year DESC, thesis.semester DESC");
		
		stmt.setInt(1, idStudent);
		stmt.setInt(2, idDepartment);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<Thesis> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM thesis inner join user student on student.idUser=thesis.idStudent inner join user supervisor on supervisor.idUser=thesis.idSupervisor left join user cosupervisor on cosupervisor.idUser=thesis.idCosupervisor ORDER BY year DESC, semester DESC, title");
		List<Thesis> list = new ArrayList<Thesis>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<Thesis> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT thesis.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM thesis INNER JOIN project ON project.idProject=thesis.idProject " +
				"INNER JOIN proposal ON proposal.idProposal=project.idProposal " +
				"INNER JOIN user student ON student.idUser=thesis.idStudent " +
				"INNER JOIN user supervisor ON supervisor.idUser=thesis.idSupervisor " +
				"LEFT JOIN user cosupervisor on cosupervisor.idUser=thesis.idCosupervisor " +
				"WHERE proposal.idDepartment=? AND thesis.semester=? AND thesis.year=? ORDER BY thesis.title");
		stmt.setInt(1, idDepartment);
		stmt.setInt(2, semester);
		stmt.setInt(3, year);
		ResultSet rs = stmt.executeQuery();
		List<Thesis> list = new ArrayList<Thesis>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public int save(Thesis thesis) throws SQLException{
		boolean insert = (thesis.getIdThesis() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO thesis(idProject, semester, year, title, subarea, idStudent, idSupervisor, idCosupervisor, file, fileType, submissionDate, abstract) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE thesis SET idProject=?, semester=?, year=?, title=?, subarea=?, idStudent=?, idSupervisor=?, idCosupervisor=?, file=?, fileType=?, submissionDate=?, abstract=? WHERE idThesis=?");
		}
		
		stmt.setInt(1, thesis.getProject().getIdProject());
		stmt.setInt(2, thesis.getSemester());
		stmt.setInt(3, thesis.getYear());
		stmt.setString(4, thesis.getTitle());
		stmt.setString(5, thesis.getSubarea());
		stmt.setInt(6, thesis.getStudent().getIdUser());
		stmt.setInt(7, thesis.getSupervisor().getIdUser());
		if((thesis.getCosupervisor() == null) || (thesis.getCosupervisor().getIdUser() == 0)){
			stmt.setNull(8, Types.INTEGER);
		}else{
			stmt.setInt(8, thesis.getCosupervisor().getIdUser());
		}
		stmt.setBytes(9, thesis.getFile());
		stmt.setInt(10, thesis.getFileType().getValue());
		stmt.setDate(11, new java.sql.Date(thesis.getSubmissionDate().getTime()));
		stmt.setString(12, thesis.getAbstract());
		
		if(!insert){
			stmt.setInt(13, thesis.getIdThesis());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				thesis.setIdThesis(rs.getInt(1));
			}
		}
		
		return thesis.getIdThesis();
	}
	
	private Thesis loadObject(ResultSet rs) throws SQLException{
		Thesis p = new Thesis();
		
		p.setIdThesis(rs.getInt("idThesis"));
		p.getProject().setIdProject(rs.getInt("idProject"));
		p.setTitle(rs.getString("title"));
		p.setSubarea(rs.getString("subarea"));
		p.getStudent().setIdUser(rs.getInt("idStudent"));
		p.getStudent().setName(rs.getString("studentname"));
		p.getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		p.getSupervisor().setName(rs.getString("supervisorname"));
		p.setCosupervisor(new User());
		p.getCosupervisor().setIdUser(rs.getInt("idCosupervisor"));
		p.getCosupervisor().setName(rs.getString("cosupervisorname"));
		p.setFile(rs.getBytes("file"));
		p.setFileType(DocumentType.valueOf(rs.getInt("fileType")));
		p.setSemester(rs.getInt("semester"));
		p.setYear(rs.getInt("year"));
		p.setSubmissionDate(rs.getDate("submissionDate"));
		p.setAbstract(rs.getString("abstract"));
		
		return p;
	}
	
}
