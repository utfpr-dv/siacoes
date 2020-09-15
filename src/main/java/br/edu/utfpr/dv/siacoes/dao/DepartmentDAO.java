package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import br.edu.utfpr.dv.siacoes.dao.base.BaseDAO;
import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDAO extends BaseDAO<Department> {
	@Override
	protected String findByIdQuery() {
		return "SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?";
	}

	@Override
	protected String updateQuery() {
		return "UPDATE department " +
				"SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? " +
				"WHERE idDepartment=?";
	}

	@Override
	protected int getId(Department object) {
		return object.getIdDepartment();
	}

	public List<Department> listAll(boolean onlyActive) throws SQLException{
		return this.list("SELECT department.*, campus.name AS campusName " +
				"FROM department " +
				"INNER JOIN campus " +
				"ON campus.idCampus=department.idCampus " +
				(onlyActive ? " WHERE department.active=1" : "") +
				" ORDER BY department.name");
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		return this.list("SELECT department.*, campus.name AS campusName " +
				       "FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				       "WHERE department.idCampus=" + String.valueOf(idCampus) +
				       (onlyActive ? " AND department.active=1 " : " ") +
				       "ORDER BY department.name");
	}

	@Override
	protected String insertQuery() {
		return "INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) " +
				"VALUES(?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected PreparedStatement insertStatementStep(PreparedStatement stmt, Department object) throws SQLException {
		stmt.setInt(1, object.getCampus().getIdCampus());
		stmt.setString(2, object.getName());
		if(object.getLogo() == null){
			stmt.setNull(3, Types.BINARY);
		}else{
			stmt.setBytes(3, object.getLogo());
		}
		stmt.setInt(4, object.isActive() ? 1 : 0);
		stmt.setString(5, object.getSite());
		stmt.setString(6, object.getFullName());
		stmt.setString(7, object.getInitials());

		return stmt;
	}

	@Override
	protected PreparedStatement updateStatementStep(PreparedStatement stmt, Department object) throws SQLException {
		return null;
	}

	@Override
	protected int insertResultSetStep(int idUser, Connection conn, ResultSet rs, Department object) throws SQLException {
		if(rs.next()){
			object.setIdDepartment(rs.getInt(1));
		}

		new UpdateEvent(conn).registerInsert(idUser, object);

		return object.getIdDepartment();
	}

	protected Department loadObject(ResultSet rs) throws SQLException{
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
