package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class ProjectDAO {
	
	public Project findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM project inner join user student on student.idUser=project.idStudent inner join user supervisor on supervisor.idUser=project.idSupervisor left join user cosupervisor on cosupervisor.idUser=project.idCosupervisor WHERE idProject = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public Project findByProposal(int idProposal) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM project inner join user student on student.idUser=project.idStudent inner join user supervisor on supervisor.idUser=project.idSupervisor left join user cosupervisor on cosupervisor.idUser=project.idCosupervisor WHERE idProposal = ?");
		
		stmt.setInt(1, idProposal);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public int findIdJury(int idProject) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idJury FROM jury WHERE idProject=?");
		
		stmt.setInt(1, idProject);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return rs.getInt("idJury");
		}else{
			return 0;
		}
	}
	
	public Project findCurrentProject(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
					"SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
					"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProject " + 
					"INNER JOIN user student ON student.idUser=project.idStudent " +
					"INNER JOIN user supervisor ON supervisor.idUser=project.idSupervisor " + 
					"LEFT JOIN user cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
					"WHERE project.idStudent = ? AND proposal.idDepartment=? AND project.semester = ? AND project.year = ?");
		
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
	
	public Project findApprovedProject(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT * FROM (SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName, jury.minimumScore, " +
				"AVG((SELECT (SUM(juryAppraiserScore.score * evaluationItem.ponderosity) / SUM(evaluationItem.ponderosity)) " +
				"FROM juryAppraiser INNER JOIN juryAppraiserScore ON juryAppraiserScore.idJuryAppraiser = juryAppraiser.idJuryAppraiser " +
				"INNER JOIN evaluationItem ON evaluationItem.idEvaluationItem = juryAppraiserScore.idEvaluationItem " +
				"WHERE juryAppraiser.idJury = jury.idJury GROUP BY juryAppraiser.idJuryAppraiser)) AS score " +
				"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProposal " + 
				"INNER JOIN user student ON student.idUser=project.idStudent " +
				"INNER JOIN user supervisor ON supervisor.idUser=project.idSupervisor " +
				"INNER JOIN jury ON jury.idProject = project.idProject " +
				"LEFT JOIN user cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
				"WHERE project.idStudent = ? AND proposal.idDepartment = ? AND (project.year < " + String.valueOf(year) + " OR (project.year = " + String.valueOf(year) + " AND project.semester < " + String.valueOf(semester) + "))) AS p WHERE p.score >= p.minimumScore ORDER BY year DESC, semester DESC");
		
		stmt.setInt(1, idStudent);
		stmt.setInt(2, idDepartment);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public Project findLastProject(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProposal " + 
				"INNER JOIN user student ON student.idUser=project.idStudent " +
				"INNER JOIN user supervisor ON supervisor.idUser=project.idSupervisor " +
				"LEFT JOIN user cosupervisor ON cosupervisor.idUser=project.idCosupervisor " +
				"WHERE project.idStudent = ? AND proposal.idDepartment = ? AND (project.year < " + String.valueOf(year) + " OR (project.year = " + String.valueOf(year) + " AND project.semester < " + String.valueOf(semester) + ")) ORDER BY year DESC, semester DESC");
		
		stmt.setInt(1, idStudent);
		stmt.setInt(2, idDepartment);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<Project> listAll() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM project inner join user student on student.idUser=project.idStudent inner join user supervisor on supervisor.idUser=project.idSupervisor left join user cosupervisor on cosupervisor.idUser=project.idCosupervisor ORDER BY year DESC, semester DESC, title");
		List<Project> list = new ArrayList<Project>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<Project> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT project.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM project INNER JOIN proposal ON proposal.idProposal=project.idProject " +
				"INNER JOIN user student ON student.idUser=project.idStudent " +
				"INNER JOIN user supervisor ON supervisor.idUser=project.idSupervisor " +
				"LEFT JOIN user cosupervisor on cosupervisor.idUser=project.idCosupervisor " +
				"WHERE proposal.idDepartment=? AND project.semester=? AND project.year=? ORDER BY project.title");
		stmt.setInt(1, idDepartment);
		stmt.setInt(2, semester);
		stmt.setInt(3, year);
		ResultSet rs = stmt.executeQuery();
		List<Project> list = new ArrayList<Project>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public int save(Project project) throws SQLException{
		boolean insert = (project.getIdProject() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO project(idProposal, semester, year, title, subarea, idStudent, idSupervisor, idCosupervisor, file, fileType, submissionDate, abstract) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE project SET idProposal=?, semester=?, year=?, title=?, subarea=?, idStudent=?, idSupervisor=?, idCosupervisor=?, file=?, fileType=?, submissionDate=?, abstract=? WHERE idProject=?");
		}
		
		stmt.setInt(1, project.getProposal().getIdProposal());
		stmt.setInt(2, project.getSemester());
		stmt.setInt(3, project.getYear());
		stmt.setString(4, project.getTitle());
		stmt.setString(5, project.getSubarea());
		stmt.setInt(6, project.getStudent().getIdUser());
		stmt.setInt(7, project.getSupervisor().getIdUser());
		if((project.getCosupervisor() == null) || (project.getCosupervisor().getIdUser() == 0)){
			stmt.setNull(8, Types.INTEGER);
		}else{
			stmt.setInt(8, project.getCosupervisor().getIdUser());
		}
		stmt.setBytes(9, project.getFile());
		stmt.setInt(10, project.getFileType().getValue());
		stmt.setDate(11, new java.sql.Date(project.getSubmissionDate().getTime()));
		stmt.setString(12, project.getAbstract());
		
		if(!insert){
			stmt.setInt(13, project.getIdProject());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				project.setIdProject(rs.getInt(1));
			}
		}
		
		return project.getIdProject();
	}
	
	private Project loadObject(ResultSet rs) throws SQLException{
		Project p = new Project();
		
		p.setIdProject(rs.getInt("idProject"));
		p.getProposal().setIdProposal(rs.getInt("idProposal"));
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
