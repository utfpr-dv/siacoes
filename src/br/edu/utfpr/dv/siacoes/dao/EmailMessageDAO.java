package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.EmailMessage;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;

public class EmailMessageDAO {
	
	public List<EmailMessage> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT * FROM emailmessage");
			
			List<EmailMessage> list = new ArrayList<EmailMessage>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public EmailMessage findByMessageType(MessageType type) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT * FROM emailmessage WHERE idEmailMessage=" + String.valueOf(type.getValue()));
			
			if(rs.next()){
				return this.loadObject(rs);
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
	
	public MessageType save(EmailMessage message) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("UPDATE emailmessage SET subject=?, message=? WHERE idEmailMessage=?");
		
			stmt.setString(1, message.getSubject());
			stmt.setString(2, message.getMessage());
			stmt.setInt(3, message.getIdEmailMessage().getValue());
			
			stmt.execute();
			
			return message.getIdEmailMessage();
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private EmailMessage loadObject(ResultSet rs) throws SQLException{
		EmailMessage message = new EmailMessage();
		
		message.setIdEmailMessage(MessageType.valueOf(rs.getInt("idEmailMessage")));
		message.setDataFields(rs.getString("dataFields"));
		message.setMessage(rs.getString("message"));
		message.setSubject(rs.getString("subject"));
		
		return message;
	}

}
