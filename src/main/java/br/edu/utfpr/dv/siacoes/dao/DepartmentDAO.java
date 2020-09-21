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
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDAO {

	public Department findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT department.*, campus.name AS campusName " +
							"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
							"WHERE idDepartment = ?");

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

	public List<Department> listAll(boolean onlyActive) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
					(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");

			List<Department> list = new ArrayList<Department>();

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

	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");

			List<Department> list = new ArrayList<Department>();

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
	

	public int save(int idUser, Department department) throws SQLException{
		boolean insert = (department.getIdDepartment() == 0);
		if(insert){
			return salvar(idUser, department);
		}else{
			return alterar(idUser, department);
		}

	}

	public int salvar (int idUser, Department department)  throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		stmt = conn.prepareStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

		try{
			conn = ConnectionDAO.getInstance().getConnection();

			stmt.setInt(1, department.getCampus().getIdCampus());
			stmt.setString(2, department.getName());
			if(department.getLogo() == null){
				stmt.setNull(3, Types.BINARY);
			}else{
				stmt.setBytes(3, department.getLogo());
			}
			stmt.setInt(4, department.isActive() ? 1 : 0);
			stmt.setString(5, department.getSite());
			stmt.setString(6, department.getFullName());
			stmt.setString(7, department.getInitials());

			stmt.execute();

			rs = stmt.getGeneratedKeys();

			if(rs.next()){
				department.setIdDepartment(rs.getInt(1));
			}
			new UpdateEvent(conn).registerInsert(idUser, department);
			return department.getIdDepartment();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

	public int alterar(int idUser, Department department) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?");

			stmt.setInt(1, department.getCampus().getIdCampus());
			stmt.setString(2, department.getName());
			if(department.getLogo() == null){
				stmt.setNull(3, Types.BINARY);
			}else{
				stmt.setBytes(3, department.getLogo());
			}
			stmt.setInt(4, department.isActive() ? 1 : 0);
			stmt.setString(5, department.getSite());
			stmt.setString(6, department.getFullName());
			stmt.setString(7, department.getInitials());
			stmt.setInt(8, department.getIdDepartment());

			stmt.execute();

			new UpdateEvent(conn).registerUpdate(idUser, department);
			return department.getIdDepartment();
		}finally{
			if(rs != null && !rs.isClosed())
				rs.close();
			if(stmt != null && !stmt.isClosed())
				stmt.close();
			if(conn != null && !conn.isClosed())
				conn.close();
		}
	}

	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();

		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));

		return department;
	}

}
