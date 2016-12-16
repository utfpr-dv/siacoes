package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ThemeSuggestion;

public class ThemeSuggestionDAO {
	
	public ThemeSuggestion findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT * FROM themesuggestion WHERE idThemeSuggestion=?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public List<ThemeSuggestion> listAll(boolean onlyActives) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM themesuggestion " + (onlyActives ? " WHERE active=1 " : "") + " ORDER BY submissionDate DESC, title");
		
		List<ThemeSuggestion> list = new ArrayList<ThemeSuggestion>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public List<ThemeSuggestion> listByDepartment(int idDepartment, boolean onlyActives) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM themesuggestion WHERE idDepartment=" + String.valueOf(idDepartment) + (onlyActives ? " AND active=1 " : "") + " ORDER BY submissionDate DESC, title");
		
		List<ThemeSuggestion> list = new ArrayList<ThemeSuggestion>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public int save(ThemeSuggestion theme) throws SQLException{
		boolean insert = (theme.getIdThemeSuggestion() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO themesuggestion(idDepartment, title, proponent, objectives, proposal, submissionDate, active) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE themesuggestion SET idDepartment=?, title=?, proponent=?, objectives=?, proposal=?, submissionDate=?, active=? WHERE idThemeSuggestion=?");
		}
		
		stmt.setInt(1, theme.getDepartment().getIdDepartment());
		stmt.setString(2, theme.getTitle());
		stmt.setString(3, theme.getProponent());
		stmt.setString(4, theme.getObjectives());
		stmt.setString(5, theme.getProposal());
		stmt.setDate(6, new java.sql.Date(theme.getSubmissionDate().getTime()));
		stmt.setInt(7, (theme.isActive() ? 1 : 0));
		
		if(!insert){
			stmt.setInt(8, theme.getIdThemeSuggestion());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				theme.setIdThemeSuggestion(rs.getInt(1));
			}
		}
		
		return theme.getIdThemeSuggestion();
	}
	
	public boolean delete(int id) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		return stmt.execute("DELETE FROM themesuggestion WHERE idThemeSuggestion = " + String.valueOf(id));
	}
	
	private ThemeSuggestion loadObject(ResultSet rs) throws SQLException{
		ThemeSuggestion theme = new ThemeSuggestion();
		
		theme.setIdThemeSuggestion(rs.getInt("idThemeSuggestion"));
		theme.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		theme.setTitle(rs.getString("title"));
		theme.setProponent(rs.getString("proponent"));
		theme.setObjectives(rs.getString("objectives"));
		theme.setProposal(rs.getString("proposal"));
		theme.setSubmissionDate(rs.getDate("submissionDate"));
		theme.setActive(rs.getInt("active") == 1);
		
		return theme;
	}

}
