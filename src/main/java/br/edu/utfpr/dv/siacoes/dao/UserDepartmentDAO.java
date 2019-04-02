package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;

public class UserDepartmentDAO {
	
	private Connection conn;
	
	public UserDepartmentDAO() throws SQLException {
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public UserDepartmentDAO(Connection conn) throws SQLException {
		if(conn == null) {
			this.conn = ConnectionDAO.getInstance().getConnection();	
		} else {
			this.conn = conn;
		}
	}
	
	public UserDepartment findById(int id) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = this.conn.prepareStatement("SELECT userdepartment.*, department.idCampus, department.name AS departmentName, campus.name AS campusName " +
					"FROM userdepartment INNER JOIN department ON department.idDepartment=userdepartment.idDepartment " +
					"INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE idUserDepartment = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return null;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public UserDepartment find(int idUser, UserProfile profile, int idDepartment) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = this.conn.prepareStatement("SELECT userdepartment.*, department.idCampus, department.name AS departmentName, campus.name AS campusName " +
					"FROM userdepartment INNER JOIN department ON department.idDepartment=userdepartment.idDepartment " +
					"INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE idUser = ? AND profile = ? AND department.idDepartment = ?");
		
			stmt.setInt(1, idUser);
			stmt.setInt(2, profile.getValue());
			stmt.setInt(3, idDepartment);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return null;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<UserDepartment> list(int idUser) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = this.conn.prepareStatement("SELECT userdepartment.*, department.idCampus, department.name AS departmentName, campus.name AS campusName " +
					"FROM userdepartment INNER JOIN department ON department.idDepartment=userdepartment.idDepartment " +
					"INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE idUser = ?");
		
			stmt.setInt(1, idUser);
			
			rs = stmt.executeQuery();
			
			List<UserDepartment> list = new ArrayList<UserDepartment>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<UserDepartment> list(int idUser, UserProfile profile) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = this.conn.prepareStatement("SELECT userdepartment.*, department.idCampus, department.name AS departmentName, campus.name AS campusName " +
					"FROM userdepartment INNER JOIN department ON department.idDepartment=userdepartment.idDepartment " +
					"INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE idUser = ? AND profile = ?");
		
			stmt.setInt(1, idUser);
			stmt.setInt(2, profile.getValue());
			
			rs = stmt.executeQuery();
			
			List<UserDepartment> list = new ArrayList<UserDepartment>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public int save(int idUser, UserDepartment user) throws SQLException {
		boolean insert = (user.getIdUserDepartment() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			if(insert) {
				stmt = this.conn.prepareStatement("INSERT INTO userdepartment(idUser, idDepartment, profile, sigacManager, sigesManager, sigetManager, departmentManager, registerSemester, registerYear) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			} else {
				stmt = this.conn.prepareStatement("UPDATE userdepartment SET idUser=?, idDepartment=?, profile=?, sigacManager=?, sigesManager=?, sigetManager=?, departmentManager=?, registerSemester=?, registerYear=? WHERE idUserDepartment=?");
			}
			
			stmt.setInt(1, user.getUser().getIdUser());
			stmt.setInt(2, user.getDepartment().getIdDepartment());
			stmt.setInt(3, user.getProfile().getValue());
			stmt.setInt(4, (user.isSigacManager() ? 1 : 0));
			stmt.setInt(5, (user.isSigesManager() ? 1 : 0));
			stmt.setInt(6, (user.isSigetManager() ? 1 : 0));
			stmt.setInt(7, (user.isDepartmentManager() ? 1 : 0));
			stmt.setInt(8, user.getRegisterSemester());
			stmt.setInt(9, user.getRegisterYear());
			
			if(!insert) {
				stmt.setInt(10, user.getIdUserDepartment());
			}
			
			stmt.execute();
			
			if(insert) {
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()) {
					user.setIdUserDepartment(rs.getInt(1));
				}

				new UpdateEvent(this.conn).registerInsert(idUser, user);
			} else {
				new UpdateEvent(this.conn).registerUpdate(idUser, user);
			}
			
			return user.getIdUserDepartment();
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private UserDepartment loadObject(ResultSet rs) throws SQLException {
		UserDepartment user = new UserDepartment();
		
		user.setIdUserDepartment(rs.getInt("idUserDepartment"));
		user.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		user.getDepartment().getCampus().setIdCampus(rs.getInt("idCampus"));
		user.getDepartment().getCampus().setName(rs.getString("campusName"));
		user.getDepartment().setName(rs.getString("departmentName"));
		user.getUser().setIdUser(rs.getInt("idUser"));
		user.setProfile(UserProfile.valueOf(rs.getInt("profile")));
		user.setDepartmentManager(rs.getInt("departmentManager") == 1);
		user.setSigacManager(rs.getInt("sigacManager") == 1);
		user.setSigesManager(rs.getInt("sigesManager") == 1);
		user.setSigetManager(rs.getInt("sigetManager") == 1);
		user.setRegisterSemester(rs.getInt("registerSemester"));
		user.setRegisterYear(rs.getInt("registerYear"));
		
		return user;
	}

}
