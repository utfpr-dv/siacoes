package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.EmailConfig;

public class EmailConfigDAO {
	
	public EmailConfig loadConfiguration() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM emailconfig");
			
			if(rs.next()){
				EmailConfig email = new EmailConfig();
				
				email.setIdEmailConfig(rs.getInt("idEmailConfig"));
				email.setHost(rs.getString("host"));
				email.setUser(rs.getString("user"));
				email.setPassword(rs.getString("password"));
				email.setPort(rs.getInt("port"));
				email.setEnableSsl(rs.getInt("enableSsl") == 1);
				email.setAuthenticate(rs.getInt("authenticate") == 1);
				email.setSignature(rs.getString("signature"));
				
				return email;
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
	
	public int save(int idUser, EmailConfig email) throws SQLException{
		boolean insert = (email.getIdEmailConfig() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO emailconfig(host, \"user\", password, port, enableSsl, authenticate, signature) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE emailconfig SET host=?, \"user\"=?, password=?, port=?, enableSsl=?, authenticate=?, signature=? WHERE idEmailConfig=?");
			}
			
			stmt.setString(1, email.getHost());
			stmt.setString(2, email.getUser());
			stmt.setString(3, email.getPassword());
			stmt.setInt(4, email.getPort());
			stmt.setInt(5, (email.isEnableSsl() ? 1 : 0));
			stmt.setInt(6, (email.isAuthenticate() ? 1 : 0));
			stmt.setString(7, email.getSignature());
			
			if(!insert){
				stmt.setInt(8, email.getIdEmailConfig());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					email.setIdEmailConfig(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, email);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, email);
			}
			
			return email.getIdEmailConfig();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

}
