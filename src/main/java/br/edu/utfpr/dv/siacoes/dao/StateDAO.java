package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.State;

public class StateDAO {
	
	public List<State> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT state.*, country.name AS countryName " +
					"FROM state INNER JOIN country ON country.idcountry=state.idcountry ORDER BY state.name");
			
			List<State> list = new ArrayList<State>();
			
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
	
	public List<State> listByCountry(int idCountry) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT state.*, country.name AS countryName " +
					"FROM state INNER JOIN country ON country.idcountry=state.idcountry " +
					"WHERE state.idcountry=" + String.valueOf(idCountry) + " ORDER BY state.name");
			
			List<State> list = new ArrayList<State>();
			
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
	
	public State findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT state.*, country.name AS countryName " +
				"FROM state INNER JOIN country ON country.idcountry=state.idcountry " +
				"WHERE idstate=?");
		
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
	
	public int save(int idUser, State state) throws SQLException{
		boolean insert = (state.getIdState() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO state(idcountry, name, initials) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE state SET idcountry=?, name=?, initials=? WHERE idstate=?");
			}
			
			stmt.setInt(1, state.getCountry().getIdCountry());
			stmt.setString(2, state.getName());
			stmt.setString(3, state.getInitials());
			
			if(!insert){
				stmt.setInt(4, state.getIdState());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					state.setIdState(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, state);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, state);
			}
			
			return state.getIdState();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private State loadObject(ResultSet rs) throws SQLException{
		State state = new State();
		
		state.setIdState(rs.getInt("idstate"));
		state.getCountry().setIdCountry(rs.getInt("idcountry"));
		state.getCountry().setName(rs.getString("countryName"));
		state.setName(rs.getString("name"));
		state.setInitials(rs.getString("initials"));
		
		return state;
	}

}
