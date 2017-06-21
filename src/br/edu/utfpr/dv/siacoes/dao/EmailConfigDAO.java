package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.edu.utfpr.dv.siacoes.model.EmailConfig;

public class EmailConfigDAO {
	
	public EmailConfig loadConfiguration() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT * FROM emailconfig");
			
			if(rs.next()){
				EmailConfig email = new EmailConfig();
				
				email.setIdEmailConfig(rs.getInt("idEmailConfig"));
				email.setHost(rs.getString("host"));
				email.setUser(rs.getString("user"));
				email.setPassword(rs.getString("password"));
				email.setPort(rs.getInt("port"));
				email.setEnableSsl(rs.getInt("enableSsl") == 1);
				email.setAuthenticate(rs.getInt("authenticate") == 1);
				
				return email;
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(EmailConfig email) throws SQLException{
		boolean insert = (email.getIdEmailConfig() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO emailconfig(host, user, password, port, enableSsl, authenticate) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE emailconfig SET host=?, user=?, password=?, port=?, enableSsl=?, authenticate=? WHERE idEmailConfig=?");
			}
			
			stmt.setString(1, email.getHost());
			stmt.setString(2, email.getUser());
			stmt.setString(3, email.getPassword());
			stmt.setInt(4, email.getPort());
			stmt.setInt(5, (email.isEnableSsl() ? 1 : 0));
			stmt.setInt(6, (email.isAuthenticate() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(7, email.getIdEmailConfig());
			}
			
			stmt.execute();
			
			if(insert){
				ResultSet rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					email.setIdEmailConfig(rs.getInt(1));
				}
			}
			
			return email.getIdEmailConfig();
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

}
