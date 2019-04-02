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
import br.edu.utfpr.dv.siacoes.model.Campus;

public class CampusDAO {
	
	public Campus findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM campus WHERE idCampus = ?");
		
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
	
	public Campus findByDepartment(int idDepartment) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idCampus FROM department WHERE idDepartment=?");
		
			stmt.setInt(1, idDepartment);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.findById(rs.getInt("idCampus"));
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
	
	public List<Campus> listAll(boolean onlyActive) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM campus " + (onlyActive ? " WHERE active=1" : "") + " ORDER BY name");
			
			List<Campus> list = new ArrayList<Campus>();
			
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
	
	public int save(int idUser, Campus campus) throws SQLException{
		boolean insert = (campus.getIdCampus() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO campus(name, address, logo, active, site, initials) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE campus SET name=?, address=?, logo=?, active=?, site=?, initials=? WHERE idCampus=?");
			}
			
			stmt.setString(1, campus.getName());
			stmt.setString(2, campus.getAddress());
			if(campus.getLogo() == null){
				stmt.setNull(3, Types.BINARY);
			}else{
				stmt.setBytes(3, campus.getLogo());	
			}
			stmt.setInt(4, campus.isActive() ? 1 : 0);
			stmt.setString(5, campus.getSite());
			stmt.setString(6, campus.getInitials());
			
			if(!insert){
				stmt.setInt(7, campus.getIdCampus());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					campus.setIdCampus(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, campus);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, campus);
			}
			
			return campus.getIdCampus();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Campus loadObject(ResultSet rs) throws SQLException{
		Campus campus = new Campus();
		
		campus.setIdCampus(rs.getInt("idCampus"));
		campus.setName(rs.getString("name"));
		campus.setAddress(rs.getString("address"));
		campus.setLogo(rs.getBytes("logo"));
		campus.setActive(rs.getInt("active") == 1);
		campus.setSite(rs.getString("site"));
		campus.setInitials(rs.getString("initials"));
		
		return campus;
	}

}
