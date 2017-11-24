package br.edu.utfpr.dv.siacoes.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;

public class AppConfig {
	
	public enum AppTheme{
		DEFAULT(0), FACEBOOK(1), FLAT(2), LIGHT(3), METRO(4), PINK(5);
		
		private final int value; 
		AppTheme(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static AppTheme valueOf(int value){
			for(AppTheme d : AppTheme.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public String toString(){
			return this.getDescription();
		}
		
		public String getDescription(){
			switch(this){
				case FACEBOOK:
					return "Facebook";
				case FLAT:
					return "Flat";
				case LIGHT:
					return "Light";
				case METRO:
					return "Metro";
				case PINK:
					return "Pink";
				default:
					return "Default";
			}
		}
	}
	
	private AppTheme theme;
	private String host;
	
	private AppConfig(){
		this.setTheme(AppTheme.DEFAULT);
		this.setHost("");
	}
	
	public AppTheme getTheme() {
		return theme;
	}
	public void setTheme(AppTheme theme) {
		this.theme = theme;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	private static AppConfig instance = null;
	
	public static synchronized AppConfig getInstance(){
		if(AppConfig.instance == null){
			AppConfig.instance = new AppConfig();
			try {
				AppConfig.instance.loadConfig();
			} catch (SQLException e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		return AppConfig.instance;
	}
	
	private void loadConfig() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM appconfig");
			
			if(rs.next()){
				this.setTheme(AppTheme.valueOf(rs.getInt("theme")));
				this.setHost(rs.getString("host"));
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
	
	public boolean save() throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		Statement stmt2 = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			stmt2 = conn.createStatement();
			rs = stmt2.executeQuery("SELECT * FROM appconfig");
			if(rs.next()){
				stmt = conn.prepareStatement("UPDATE appconfig SET theme=?, host=?");	
			}else{
				stmt = conn.prepareStatement("INSERT INTO appconfig(theme, host) VALUES(?, ?)");
			}
			
			stmt.setInt(1, this.getTheme().getValue());
			stmt.setString(2, this.getHost());
			
			stmt.execute();
			
			return true;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt2 != null) && !stmt2.isClosed())
				stmt2.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

}
