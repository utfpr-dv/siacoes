package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class UserDAO {
	
	public User findByLogin(String login) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT \"user\".*, company.name AS companyName, department.idCampus " +
					"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
					"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
					"WHERE \"user\".login = ?");
		
			stmt.setString(1, login);
			
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public User findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT \"user\".*, company.name AS companyName, department.idCampus " +
					"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
					"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
					"WHERE \"user\".idUser = ?");
		
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public User findManager(int idDepartment, SystemModule module) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			String sql = "SELECT \"user\".*, company.name AS companyName, department.idCampus " +
					"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
					"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
					"WHERE \"user\".iddepartment = ? ";
			
			switch(module){
				case SIGAC:
					sql += " AND \"user\".sigacmanager=1 ";
					break;
				case SIGES:
					sql += " AND \"user\".sigesmanager=1 ";
					break;
				case SIGET:
					sql += " AND \"user\".sigetmanager=1 ";
					break;
				default:
					sql += " AND 1=2 ";
			}
			
			sql += " ORDER BY \"user\".profile";
			
			stmt = conn.prepareStatement(sql);
		
			stmt.setInt(1, idDepartment);
			
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public User findDepartmentManager(int idDepartment) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT \"user\".*, company.name AS companyName, department.idCampus " +
				"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
				"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
				"WHERE \"user\".iddepartment = ? AND \"user\".departmentmanager=1 ORDER BY \"user\".profile");
	
			stmt.setInt(1, idDepartment);
			
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
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public String findEmail(int idUser) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT email FROM \"user\" WHERE idUser = ?");
		
			stmt.setInt(1, idUser);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return rs.getString("email");
			}else{
				return "";
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
	
	public List<User> listAll(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, company.name AS companyName, department.idCampus " +
						"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
						"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
						"WHERE \"user\".login <> 'admin' " + (onlyActives ? " AND \"user\".active = 1 " : "") + " ORDER BY \"user\".name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> listAllProfessors(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, company.name AS companyName, department.idCampus " +
						"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
						"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
						"WHERE \"user\".login <> 'admin' AND \"user\".profile IN (1, 2) " + (onlyActives ? " AND \"user\".active = 1 " : "") + " ORDER BY \"user\".name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> listProfessorsByDepartment(int idDepartment, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, company.name AS companyName, department.idCampus " +
						"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
						"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
						"WHERE \"user\".login <> 'admin' AND \"user\".idDepartment=" + String.valueOf(idDepartment) + " AND \"user\".profile IN (1, 2) " + (onlyActives ? " AND \"user\".active = 1 " : "") + " ORDER BY \"user\".name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> listProfessorsByCampus(int idCampus, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, company.name AS companyName, department.idCampus " +
					"FROM \"user\" INNER JOIN department ON department.idDepartment=\"user\".idDepartment " + 
					"LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
					"WHERE \"user\".login <> 'admin' AND idCampus=" + String.valueOf(idCampus) + " AND \"user\".profile IN (1, 2) " + (onlyActives ? " AND \"user\".active = 1 " : "") + " ORDER BY \"user\".name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> listAllStudents(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, company.name AS companyName, department.idCampus " +
						"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
						"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
						"WHERE \"user\".login <> 'admin' AND \"user\".profile = 0 " + (onlyActives ? " AND \"user\".active = 1 " : "") + " ORDER BY \"user\".name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> listAllCompanySupervisors(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, company.name AS companyName, department.idCampus " +
						"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
						"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
						"WHERE \"user\".login <> 'admin' AND \"user\".profile = 4 " + (onlyActives ? " AND \"user\".active = 1 " : "") + " ORDER BY \"user\".name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> listSupervisorsByCompany(int idCompany, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, company.name AS companyName, department.idCampus " +
						"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
						"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
						"WHERE \"user\".login <> 'admin' AND \"user\".profile = 4 AND \"user\".idcompany=" + String.valueOf(idCompany) + (onlyActives ? " AND \"user\".active = 1 " : "") + " ORDER BY \"user\".name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> listStudentBySupervisor(int idSupervisor) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT \"user\".*, department.idCampus FROM \"user\" INNER JOIN proposal ON proposal.idStudent=\"user\".idUser " +
					"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
					"WHERE \"user\".profile=0 AND (proposal.idSupervisor=" + String.valueOf(idSupervisor) + " OR proposal.idCosupervisor=" + String.valueOf(idSupervisor) + ") " +
					" UNION " +
					"SELECT \"user\".*, department.idCampus FROM \"user\" INNER JOIN project ON project.idStudent=\"user\".idUser " + 
					"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
					"WHERE \"user\".profile=0 AND (project.idSupervisor=" + String.valueOf(idSupervisor) + " OR project.idCosupervisor=" + String.valueOf(idSupervisor) + ") " + 
					" UNION " +
					"SELECT \"user\".*, department.idCampus FROM \"user\" INNER JOIN thesis ON thesis.idStudent=\"user\".idUser " + 
					"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
					"WHERE \"user\".profile=0 AND (thesis.idSupervisor=" + String.valueOf(idSupervisor) + " OR thesis.idCosupervisor=" + String.valueOf(idSupervisor) + ") " +
					"ORDER BY name");
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
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
	
	public List<User> list(String name, int profile, boolean onlyActives, boolean onlyExternal) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			String sql = "SELECT \"user\".*, company.name AS companyName, department.idCampus " +
						"FROM \"user\" LEFT JOIN company ON \"user\".idcompany=company.idcompany " +
						"LEFT JOIN department ON \"user\".idDepartment=department.idDepartment " +
						"WHERE login <> 'admin' " + 
						(!name.isEmpty() ? " AND \"user\".name ILIKE ? " : "") +
						(profile >= 0 ? " AND \"user\".profile = ? " : "") +
						(onlyActives ? " AND \"user\".active = 1 " : "") +
						(onlyExternal ? " AND \"user\".external = 1 " : "") +
						"ORDER BY \"user\".name";
			stmt = conn.prepareStatement(sql);
			int p = 1;
			
			if(!name.isEmpty()){
				stmt.setString(p, "%" + name + "%");
				p++;
			}
			if(profile >= 0){
				stmt.setInt(p, profile);
				p++;
			}
			
			rs = stmt.executeQuery();
			List<User> list = new ArrayList<User>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
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
	
	public int save(User user) throws SQLException{
		boolean insert = (user.getIdUser() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO \"user\"(name, login, password, email, profile, institution, research, lattes, external, active, area, idDepartment, sigacManager, sigesManager, sigetManager, departmentManager, studentCode, registerSemester, registerYear, phone, idcompany, photo) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE \"user\" SET name=?, login=?, password=?, email=?, profile=?, institution=?, research=?, lattes=?, external=?, active=?, area=?, idDepartment=?, sigacManager=?, sigesManager=?, sigetManager=?, departmentManager=?, studentCode=?, registerSemester=?, registerYear=?, phone=?, idcompany=?, photo=? WHERE idUser=?");
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
			stmt.setInt(16, (user.isDepartmentManager() ? 1 : 0));
			stmt.setString(17, user.getStudentCode());
			stmt.setInt(18, user.getRegisterSemester());
			stmt.setInt(19, user.getRegisterYear());
			stmt.setString(20, user.getPhone());
			if((user.getCompany() == null) || (user.getCompany().getIdCompany() == 0)){
				stmt.setNull(21, Types.INTEGER);
			}else{
				stmt.setInt(21, user.getCompany().getIdCompany());	
			}
			if(user.getPhoto() == null){
				stmt.setNull(22, Types.BINARY);
			}else{
				stmt.setBytes(22, user.getPhoto());	
			}
			
			if(!insert){
				stmt.setInt(23, user.getIdUser());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					user.setIdUser(rs.getInt(1));
				}
			}
			
			return user.getIdUser();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private User loadObject(ResultSet rs) throws SQLException{
		User user = new User();
		
		user.setIdUser(rs.getInt("iduser"));
		user.setName(rs.getString("name"));
		user.setLogin(rs.getString("login"));
		user.setPassword(rs.getString("password"));
		user.setEmail(rs.getString("email"));
		user.setPhone(rs.getString("phone"));
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
			user.getDepartment().setCampus(new Campus());
			user.getDepartment().getCampus().setIdCampus(rs.getInt("idCampus"));
		}
		if(rs.getInt("idcompany") == 0){
			user.setCompany(null);
		}else{
			user.getCompany().setIdCompany(rs.getInt("idcompany"));
			user.getCompany().setName(rs.getString("companyName"));
		}
		user.setSigacManager(rs.getInt("sigacManager") == 1);
		user.setSigesManager(rs.getInt("sigesManager") == 1);
		user.setSigetManager(rs.getInt("sigetManager") == 1);
		user.setDepartmentManager(rs.getInt("departmentmanager") == 1);
		user.setStudentCode(rs.getString("studentCode"));
		user.setRegisterSemester(rs.getInt("registerSemester"));
		user.setRegisterYear(rs.getInt("registerYear"));
		user.setPhoto(rs.getBytes("photo"));
		
		return user;
	}
	
	public String[] findEmails(int[] ids) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			String sql = "";
			
			for(int id : ids){
				if(sql == "")
					sql = String.valueOf(id);
				else
					sql = sql + ", " + String.valueOf(id);
			}
			
			if(sql != ""){
				List<String> emails = new ArrayList<String>();
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT email FROM \"user\" WHERE idUser IN (" + sql + ")");
				
				while(rs.next()){
					emails.add(rs.getString("email"));
				}
				
				return (String[])emails.toArray();
			}else
				return null;
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
