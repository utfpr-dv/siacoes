package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Campus;

public class CampusDAO {
	
	public Campus findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM campus WHERE idCampus = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public Campus findByDepartment(int idDepartment) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT idCampus FROM department WHERE idDepartment=?");
		
		stmt.setInt(1, idDepartment);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.findById(rs.getInt("idCampus"));
		}else{
			return null;
		}
	}
	
	public List<Campus> listAll(boolean onlyActive) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM campus " + (onlyActive ? " WHERE active=1" : "") + " ORDER BY name");
		
		List<Campus> list = new ArrayList<Campus>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(Campus campus) throws SQLException{
		boolean insert = (campus.getIdCampus() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO campus(name, address, logo, active, site) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE campus SET name=?, address=?, logo=?, active=?, site=? WHERE idCampus=?");
		}
		
		stmt.setString(1, campus.getName());
		stmt.setString(2, campus.getAddress());
		if(campus.getLogo() == null){
			stmt.setNull(3, Types.BLOB);
		}else{
			stmt.setBytes(3, campus.getLogo());	
		}
		stmt.setInt(4, campus.isActive() ? 1 : 0);
		stmt.setString(5, campus.getSite());
		
		if(!insert){
			stmt.setInt(6, campus.getIdCampus());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				campus.setIdCampus(rs.getInt(1));
			}
		}
		
		return campus.getIdCampus();
	}
	
	private Campus loadObject(ResultSet rs) throws SQLException{
		Campus campus = new Campus();
		
		campus.setIdCampus(rs.getInt("idCampus"));
		campus.setName(rs.getString("name"));
		campus.setAddress(rs.getString("address"));
		campus.setLogo(rs.getBytes("logo"));
		campus.setActive(rs.getInt("active") == 1);
		campus.setSite(rs.getString("site"));
		
		return campus;
	}

}
