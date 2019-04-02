package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.City;

public class CityDAO {
	
	public List<City> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT city.*, state.name AS stateName, state.initials AS stateInitials, country.idcountry, country.name AS countryName " +
					"FROM city INNER JOIN state ON state.idstate=city.idstate " +
					"INNER JOIN country ON country.idcountry=state.idcountry ORDER BY city.name");
			
			List<City> list = new ArrayList<City>();
			
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
	
	public List<City> listByState(int idState) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT city.*, state.name AS stateName, state.initials AS stateInitials, country.idcountry, country.name AS countryName " +
					"FROM city INNER JOIN state ON state.idstate=city.idstate " +
					"INNER JOIN country ON country.idcountry=state.idcountry WHERE city.idstate=" + String.valueOf(idState) + " ORDER BY city.name");
			
			List<City> list = new ArrayList<City>();
			
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
	
	public City findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT city.*, state.name AS stateName, state.initials AS stateInitials, country.idcountry, country.name AS countryName " +
				"FROM city INNER JOIN state ON state.idstate=city.idstate " +
				"INNER JOIN country ON country.idcountry=state.idcountry WHERE city.idcity=?");
		
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
	
	public int save(int idUser, City city) throws SQLException{
		boolean insert = (city.getIdCity() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO city(idstate, name) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE city SET idstate=?, name=? WHERE idcity=?");
			}
			
			stmt.setInt(1, city.getState().getIdState());
			stmt.setString(2, city.getName());
			
			if(!insert){
				stmt.setInt(3, city.getIdCity());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					city.setIdCity(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, city);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, city);
			}
			
			return city.getIdCity();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private City loadObject(ResultSet rs) throws SQLException{
		City city = new City();
		
		city.setIdCity(rs.getInt("idcity"));
		city.getState().setIdState(rs.getInt("idstate"));
		city.getState().setName(rs.getString("stateName"));
		city.getState().setInitials(rs.getString("stateInitials"));
		city.getState().getCountry().setIdCountry(rs.getInt("idcountry"));
		city.getState().getCountry().setName(rs.getString("countryName"));
		city.setName(rs.getString("name"));
		
		return city;
	}

}
