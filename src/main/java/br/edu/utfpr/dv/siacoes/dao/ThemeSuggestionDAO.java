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
import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;

public class ThemeSuggestionDAO {
	
	public ThemeSuggestion findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM themesuggestion WHERE idThemeSuggestion=?");
		
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
	
	public List<ThemeSuggestion> listAll(boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM themesuggestion " + (onlyActives ? " WHERE active=1 " : "") + " ORDER BY submissionDate DESC, title");
			
			List<ThemeSuggestion> list = new ArrayList<ThemeSuggestion>();
			
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
	
	public List<ThemeSuggestion> listByDepartment(int idDepartment, boolean onlyActives) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM themesuggestion WHERE idDepartment=" + String.valueOf(idDepartment) + (onlyActives ? " AND active=1 " : "") + " ORDER BY submissionDate DESC, title");
			
			List<ThemeSuggestion> list = new ArrayList<ThemeSuggestion>();
			
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
	
	public int save(int idUser, ThemeSuggestion theme) throws SQLException{
		boolean insert = (theme.getIdThemeSuggestion() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO themesuggestion(idDepartment, idUser, title, proponent, objectives, proposal, submissionDate, active) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE themesuggestion SET idDepartment=?, idUser=?, title=?, proponent=?, objectives=?, proposal=?, submissionDate=?, active=? WHERE idThemeSuggestion=?");
			}
			
			stmt.setInt(1, theme.getDepartment().getIdDepartment());
			if((theme.getUser() == null) || (theme.getUser().getIdUser() == 0)){
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setInt(2, theme.getUser().getIdUser());
			}
			stmt.setString(3, theme.getTitle());
			stmt.setString(4, theme.getProponent());
			stmt.setString(5, theme.getObjectives());
			stmt.setString(6, theme.getProposal());
			stmt.setDate(7, new java.sql.Date(theme.getSubmissionDate().getTime()));
			stmt.setInt(8, (theme.isActive() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(9, theme.getIdThemeSuggestion());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					theme.setIdThemeSuggestion(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, theme);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, theme);
			}
			
			return theme.getIdThemeSuggestion();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean delete(int idUser, int id) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ThemeSuggestion theme = this.findById(id);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			boolean ret = stmt.execute("DELETE FROM themesuggestion WHERE idThemeSuggestion = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, theme);
			
			return ret;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ThemeSuggestion loadObject(ResultSet rs) throws SQLException{
		ThemeSuggestion theme = new ThemeSuggestion();
		
		theme.setIdThemeSuggestion(rs.getInt("idThemeSuggestion"));
		theme.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		theme.getUser().setIdUser(rs.getInt("idUser"));
		theme.setTitle(rs.getString("title"));
		theme.setProponent(rs.getString("proponent"));
		theme.setObjectives(rs.getString("objectives"));
		theme.setProposal(rs.getString("proposal"));
		theme.setSubmissionDate(rs.getDate("submissionDate"));
		theme.setActive(rs.getInt("active") == 1);
		
		return theme;
	}

}
