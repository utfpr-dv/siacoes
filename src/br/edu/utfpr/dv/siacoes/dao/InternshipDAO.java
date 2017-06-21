package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;

public class InternshipDAO {
	
	private Connection conn;
	
	public InternshipDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public InternshipDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();
		}else{
			this.conn = conn;	
		}
	}
	
	public List<Internship> listAll() throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
				"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
				"INNER JOIN user student ON student.iduser=internship.idstudent " +
				"INNER JOIN user supervisor ON supervisor.iduser=internship.idsupervisor " + 
				"ORDER BY internship.startDate DESC");
		
		List<Internship> list = new ArrayList<Internship>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Internship> list(int year, int idStudent, int idSupervisor, int idCompany, int type, int status) throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
				"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
				"INNER JOIN user student ON student.iduser=internship.idstudent " +
				"INNER JOIN user supervisor ON supervisor.iduser=internship.idsupervisor " +
				"WHERE 1=1 " +
				(year > 0 ? " AND (YEAR(internship.startDate) >= " + String.valueOf(year) + " AND (YEAR(internship.endDate) <= " + String.valueOf(year) + " OR internship.endDate IS NULL)) " : "") +
				(idStudent > 0 ? " AND internship.idstudent = " + String.valueOf(idStudent) : "") +
				(idSupervisor > 0 ? " AND internship.idsupervisor = " + String.valueOf(idSupervisor) : "") +
				(idCompany > 0 ? " AND internship.idcompany = " + String.valueOf(idCompany) : "") +
				(type >= 0 ? " AND internship.type = " + String.valueOf(type) : "") +
				(status == 0 ? " AND (internship.endDate IS NULL OR internship.endDate >= CURDATE())" : (status == 1 ? " AND internship.endDate < CURDATE()" : "")) +
				" ORDER BY internship.startDate DESC");
		
		List<Internship> list = new ArrayList<Internship>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Internship> listByCompany(int idCompany) throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
				"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
				"INNER JOIN user student ON student.iduser=internship.idstudent " +
				"INNER JOIN user supervisor ON supervisor.iduser=internship.idsupervisor " + 
				"WHERE internship.idcompany=" + String.valueOf(idCompany) + " ORDER BY internship.startDate DESC");
		
		List<Internship> list = new ArrayList<Internship>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Internship> listByCompanySupervisor(int idCompanySupervisor) throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
				"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
				"INNER JOIN user student ON student.iduser=internship.idstudent " +
				"INNER JOIN user supervisor ON supervisor.iduser=internship.idsupervisor " + 
				"WHERE internship.idcompanysupervisor=" + String.valueOf(idCompanySupervisor) + " ORDER BY internship.startDate DESC");
		
		List<Internship> list = new ArrayList<Internship>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Internship> listBySupervisor(int idSupervisor) throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
				"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
				"INNER JOIN user student ON student.iduser=internship.idstudent " +
				"INNER JOIN user supervisor ON supervisor.iduser=internship.idsupervisor " +  
				"WHERE internship.idsupervisor=" + String.valueOf(idSupervisor) + " ORDER BY internship.startDate DESC");
		
		List<Internship> list = new ArrayList<Internship>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Internship> listByStudent(int idStudent) throws SQLException{
		Statement stmt = this.conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
				"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
				"INNER JOIN user student ON student.iduser=internship.idstudent " +
				"INNER JOIN user supervisor ON supervisor.iduser=internship.idsupervisor " +  
				"WHERE internship.idstudent=" + String.valueOf(idStudent) + " ORDER BY internship.startDate DESC");
		
		List<Internship> list = new ArrayList<Internship>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public Internship findById(int id) throws SQLException{
		PreparedStatement stmt = this.conn.prepareStatement(
				"SELECT internship.*, company.name AS companyName, student.name AS studentName, supervisor.name AS supervisorName " +
				"FROM internship INNER JOIN company ON company.idcompany=internship.idcompany " +
				"INNER JOIN user student ON student.iduser=internship.idstudent " +
				"INNER JOIN user supervisor ON supervisor.iduser=internship.idsupervisor " + 
				"WHERE internship.idinternship=?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public int save(Internship internship) throws SQLException{
		boolean insert = (internship.getIdInternship() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = this.conn.prepareStatement("INSERT INTO internship(iddepartment, idcompany, idcompanysupervisor, idsupervisor, idstudent, type, comments, startDate, endDate, totalHours, internshipPlan, finalReport, reportTitle) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = this.conn.prepareStatement("UPDATE internship SET iddepartment=?, idcompany=?, idcompanysupervisor=?, idsupervisor=?, idstudent=?, type=?, comments=?, startDate=?, endDate=?, totalHours=?, internshipPlan=?, finalReport=?, reportTitle=? WHERE idinternship=?");
		}
		
		stmt.setInt(1, internship.getDepartment().getIdDepartment());
		stmt.setInt(2, internship.getCompany().getIdCompany());
		stmt.setInt(3, internship.getCompanySupervisor().getIdUser());
		stmt.setInt(4, internship.getSupervisor().getIdUser());
		stmt.setInt(5, internship.getStudent().getIdUser());
		stmt.setInt(6, internship.getType().getValue());
		stmt.setString(7, internship.getComments());
		stmt.setDate(8, new java.sql.Date(internship.getStartDate().getTime()));
		if(internship.getEndDate() == null){
			stmt.setNull(9, Types.DATE);
		}else{
			stmt.setDate(9, new java.sql.Date(internship.getEndDate().getTime()));
		}
		stmt.setInt(10, internship.getTotalHours());
		stmt.setBytes(11, internship.getInternshipPlan());
		if(internship.getFinalReport() == null){
			stmt.setNull(12, Types.BLOB);
		}else{
			stmt.setBytes(12, internship.getFinalReport());
		}
		stmt.setString(13, internship.getReportTitle());
		
		if(!insert){
			stmt.setInt(14, internship.getIdInternship());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				internship.setIdInternship(rs.getInt(1));
			}
		}
		
		return internship.getIdInternship();
	}
	
	private Internship loadObject(ResultSet rs) throws SQLException{
		Internship internship = new Internship();
		
		internship.setIdInternship(rs.getInt("idinternship"));
		internship.getDepartment().setIdDepartment(rs.getInt("iddepartment"));
		internship.getCompany().setIdCompany(rs.getInt("idcompany"));
		internship.getCompany().setName(rs.getString("companyName"));
		internship.getCompanySupervisor().setIdUser(rs.getInt("idcompanysupervisor"));
		internship.getSupervisor().setIdUser(rs.getInt("idsupervisor"));
		internship.getSupervisor().setName(rs.getString("supervisorName"));
		internship.getStudent().setIdUser(rs.getInt("idstudent"));
		internship.getStudent().setName(rs.getString("studentName"));
		internship.setType(InternshipType.valueOf(rs.getInt("type")));
		internship.setComments(rs.getString("comments"));
		internship.setStartDate(rs.getDate("startDate"));
		internship.setEndDate(rs.getDate("endDate"));
		internship.setTotalHours(rs.getInt("totalHours"));
		internship.setInternshipPlan(rs.getBytes("internshipPlan"));
		internship.setFinalReport(rs.getBytes("finalReport"));
		internship.setReportTitle(rs.getString("reportTitle"));
		
		return internship;
	}
	
	public boolean delete(int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			return stmt.execute("DELETE FROM internship WHERE idinternship = " + String.valueOf(id));
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

}
