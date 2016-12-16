package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDAO {

	public Department findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement(
				"SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
				(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");
		
		List<Department> list = new ArrayList<Department>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
		
		List<Department> list = new ArrayList<Department>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(Department department) throws SQLException{
		boolean insert = (department.getIdDepartment() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO department(idCampus, name, logo, active, sigacMinimumScore, sigetMinimumScore, site, fullName, sigetRegisterProposal) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, sigacMinimumScore=?, sigetMinimumScore=?, site=?, fullName=?, sigetRegisterProposal=? WHERE idDepartment=?");
		}
		
		stmt.setInt(1, department.getCampus().getIdCampus());
		stmt.setString(2, department.getName());
		if(department.getLogo() == null){
			stmt.setNull(3, Types.BLOB);
		}else{
			stmt.setBytes(3, department.getLogo());	
		}
		stmt.setInt(4, department.isActive() ? 1 : 0);
		stmt.setDouble(5, department.getSigacMinimumScore());
		stmt.setDouble(6, department.getSigetMinimumScore());
		stmt.setString(7, department.getSite());
		stmt.setString(8, department.getFullName());
		stmt.setInt(9, (department.isSigetRegisterProposal() ? 1 : 0));
		
		if(!insert){
			stmt.setInt(10, department.getIdDepartment());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				department.setIdDepartment(rs.getInt(1));
			}
		}
		
		return department.getIdDepartment();
	}
	
	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSigacMinimumScore(rs.getDouble("sigacMinimumScore"));
		department.setSigetMinimumScore(rs.getDouble("sigetMinimumScore"));
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setSigetRegisterProposal(rs.getInt("sigetRegisterProposal") == 1);
		
		return department;
	}
	
}
