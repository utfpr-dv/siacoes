package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.User;

public class UserDAO {
	
	public User findByLogin(String login) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM user WHERE login = ?");
		
		stmt.setString(1, login);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public User findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM user WHERE idUser = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public String findEmail(int idUser) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT email FROM user WHERE idUser = ?");
		
		stmt.setInt(1, idUser);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return rs.getString("email");
		}else{
			return "";
		}
	}
	
	public List<User> listAll(boolean onlyActives) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE login <> 'admin' " + (onlyActives ? " AND active = 1 " : "") + " ORDER BY name");
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<User> listAllProfessors(boolean onlyActives) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE login <> 'admin' AND profile IN (1, 2) " + (onlyActives ? " AND active = 1 " : "") + " ORDER BY name");
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<User> listProfessorsByDepartment(int idDepartment, boolean onlyActives) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE login <> 'admin' AND idDepartment=" + String.valueOf(idDepartment) + " AND profile IN (1, 2) " + (onlyActives ? " AND active = 1 " : "") + " ORDER BY name");
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<User> listProfessorsByCampus(int idCampus, boolean onlyActives) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM user INNER JOIN department ON department.idDepartment=user.idDepartment " + 
				"WHERE login <> 'admin' AND idCampus=" + String.valueOf(idCampus) + " AND profile IN (1, 2) " + (onlyActives ? " AND active = 1 " : "") + " ORDER BY name");
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<User> listAllStudents(boolean onlyActives) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE login <> 'admin' AND profile = 0 " + (onlyActives ? " AND active = 1 " : "") + " ORDER BY name");
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<User> listStudentBySupervisor(int idSupervisor, int semester, int year) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT user.* FROM user INNER JOIN proposal ON proposal.idStudent=user.idUser " +
				"WHERE user.profile=0 AND proposal.semester=" + String.valueOf(semester) + " AND proposal.year=" + String.valueOf(year) + " AND (proposal.idSupervisor=" + String.valueOf(idSupervisor) + " OR proposal.idCosupervisor=" + String.valueOf(idSupervisor) + ") " +
				" UNION " +
				"SELECT user.* FROM user INNER JOIN project ON project.idStudent=user.idUser " + 
				"WHERE user.profile=0 AND project.semester=" + String.valueOf(semester) + " AND project.year=" + String.valueOf(year) + " AND (project.idSupervisor=" + String.valueOf(idSupervisor) + " OR project.idCosupervisor=" + String.valueOf(idSupervisor) + ") " + 
				" UNION " +
				"SELECT user.* FROM user INNER JOIN thesis ON thesis.idStudent=user.idUser " + 
				"WHERE user.profile=0 AND thesis.semester=" + String.valueOf(semester) + " AND thesis.year=" + String.valueOf(year) + " AND (thesis.idSupervisor=" + String.valueOf(idSupervisor) + " OR thesis.idCosupervisor=" + String.valueOf(idSupervisor) + ") " +
				"ORDER BY name");
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));			
		}
		
		return list;
	}
	
	public List<User> list(String name, int profile, boolean onlyActives, boolean onlyExternal) throws SQLException {
		String sql = "SELECT * FROM user WHERE login <> 'admin' " + 
				(!name.isEmpty() ? " AND name LIKE ? " : "") +
				(profile >= 0 ? " AND profile = ? " : "") +
				(onlyActives ? " AND active = 1 " : "") +
				(onlyExternal ? " AND external = 1 " : "") +
				"ORDER BY name";
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(sql);
		int p = 1;
		
		if(!name.isEmpty()){
			stmt.setString(p, "%" + name + "%");
			p++;
		}
		if(profile >= 0){
			stmt.setInt(p, profile);
			p++;
		}
		
		ResultSet rs = stmt.executeQuery();
		List<User> list = new ArrayList<User>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(User user) throws SQLException{
		boolean insert = (user.getIdUser() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO user(name, login, password, email, profile, institution, research, lattes, external, active, area, idDepartment, sigacManager, sigesManager, sigetManager, studentCode, registerSemester, registerYear) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE user SET name=?, login=?, password=?, email=?, profile=?, institution=?, research=?, lattes=?, external=?, active=?, area=?, idDepartment=?, sigacManager=?, sigesManager=?, sigetManager=?, studentCode=?, registerSemester=?, registerYear=? WHERE idUser=?");
		}
		
		stmt.setString(1, user.getName());
		stmt.setString(2, user.getLogin());
		stmt.setString(3, user.getPassword());
		stmt.setString(4, user.getEmail());
		stmt.setInt(5, user.getProfile().getValue());
		stmt.setString(6, user.getInstitution());
		stmt.setString(7, user.getResearch());
		stmt.setString(8, user.getLattes());
		stmt.setInt(9, user.isExternal() ? 1 : 0);
		stmt.setInt(10, user.isActive() ? 1 : 0);
		stmt.setString(11, user.getArea());
		if((user.getDepartment() == null) || (user.getDepartment().getIdDepartment() == 0)){
			stmt.setNull(12, Types.INTEGER);
		}else{
			stmt.setInt(12, user.getDepartment().getIdDepartment());	
		}
		stmt.setInt(13, (user.isSigacManager() ? 1 : 0));
		stmt.setInt(14, (user.isSigesManager() ? 1 : 0));
		stmt.setInt(15, (user.isSigetManager() ? 1 : 0));
		stmt.setString(16, user.getStudentCode());
		stmt.setInt(17, user.getRegisterSemester());
		stmt.setInt(18, user.getRegisterYear());
		
		if(!insert){
			stmt.setInt(19, user.getIdUser());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				user.setIdUser(rs.getInt(1));
			}
		}
		
		return user.getIdUser();
	}
	
	private User loadObject(ResultSet rs) throws SQLException{
		User user = new User();
		
		user.setIdUser(rs.getInt("iduser"));
		user.setName(rs.getString("name"));
		user.setLogin(rs.getString("login"));
		user.setPassword(rs.getString("password"));
		user.setEmail(rs.getString("email"));
		user.setInstitution(rs.getString("institution"));
		user.setResearch(rs.getString("research"));
		user.setLattes(rs.getString("lattes"));
		user.setProfile(User.UserProfile.valueOf(rs.getInt("profile")));
		user.setExternal(rs.getInt("external") == 1);
		user.setActive(rs.getInt("active") == 1);
		user.setArea(rs.getString("area"));
		if(rs.getInt("idDepartment") == 0){
			user.setDepartment(null);
		}else{
			user.getDepartment().setIdDepartment(rs.getInt("idDepartment"));	
		}
		user.setSigacManager(rs.getInt("sigacManager") == 1);
		user.setSigesManager(rs.getInt("sigesManager") == 1);
		user.setSigetManager(rs.getInt("sigetManager") == 1);
		user.setStudentCode(rs.getString("studentCode"));
		user.setRegisterSemester(rs.getInt("registerSemester"));
		user.setRegisterYear(rs.getInt("registerYear"));
		
		return user;
	}

}
