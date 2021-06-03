package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage.ReminderType;

public class ReminderMessageDAO {
	
	public List<ReminderMessage> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM remindermessage");
			
			List<ReminderMessage> list = new ArrayList<ReminderMessage>();
			
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
	
	public List<ReminderMessage> listByModule(SystemModule module) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM remindermessage WHERE module=" + String.valueOf(module.getValue()));
			
			List<ReminderMessage> list = new ArrayList<ReminderMessage>();
			
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
	
	public ReminderMessage findByMessageType(ReminderType type) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM remindermessage WHERE idReminderMessage=" + String.valueOf(type.getValue()));
			
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
	
	public ReminderType save(int idUser, ReminderMessage message) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("UPDATE remindermessage SET subject=?, message=? WHERE idReminderMessage=?");
		
			stmt.setString(1, message.getSubject());
			stmt.setString(2, message.getMessage());
			stmt.setInt(3, message.getIdReminderMessage().getValue());
			
			stmt.execute();
			
			new UpdateEvent(conn).registerUpdate(idUser, message);
			
			return message.getIdReminderMessage();
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private ReminderMessage loadObject(ResultSet rs) throws SQLException{
		ReminderMessage message = new ReminderMessage();
		
		message.setIdReminderMessage(ReminderType.valueOf(rs.getInt("idReminderMessage")));
		message.setDataFields(rs.getString("dataFields"));
		message.setMessage(rs.getString("message"));
		message.setSubject(rs.getString("subject"));
		message.setModule(SystemModule.valueOf(rs.getInt("module")));
		
		return message;
	}

}
