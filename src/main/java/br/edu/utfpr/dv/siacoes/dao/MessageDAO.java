package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Message;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class MessageDAO {
	
	public Message findById(int id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT message.*, \"user\".name " +
					"FROM message INNER JOIN \"user\" ON \"user\".idUser=message.idUser " +
					"WHERE message.idMessage = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return this.loadObject(rs);
			} else {
				return null;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Message> listAll() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT message.*, \"user\".name " +
					"FROM message INNER JOIN \"user\" ON \"user\".idUser=message.idUser " +
					"ORDER BY message.date DESC");
			List<Message> list = new ArrayList<Message>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));			
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int getUnreadMessages(int idUser) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT COUNT(*) AS total " +
					"FROM message " +
					"WHERE message.read = 0 AND message.idUser = " + String.valueOf(idUser));

			if(rs.next()) {
				return rs.getInt("total");
			} else {
				return 0;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Message> listByUser(int idUser) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT message.*, \"user\".name " +
					"FROM message INNER JOIN \"user\" ON \"user\".idUser=message.idUser " +
					"WHERE message.idUser = " + String.valueOf(idUser) +
					" ORDER BY message.date DESC");
			List<Message> list = new ArrayList<Message>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));			
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Message> listRead(int idUser, boolean read) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT message.*, \"user\".name " +
					"FROM message INNER JOIN \"user\" ON \"user\".idUser=message.idUser " +
					"WHERE message.read = " + (read ? "1" : "0") + " AND message.idUser = " + String.valueOf(idUser) +
					" ORDER BY message.date DESC");
			List<Message> list = new ArrayList<Message>();
			
			while(rs.next()) {
				list.add(this.loadObject(rs));			
			}
			
			return list;
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public boolean markAsRead(int idMessage, boolean read) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("UPDATE message SET read=? WHERE idMessage=?");
			
			stmt.setInt(1, (read ? 1 : 0));
			stmt.setInt(2, idMessage);
			
			return stmt.execute();
		} finally {
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(Message message) throws SQLException {
		boolean insert = (message.getIdMessage() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert) {
				stmt = conn.prepareStatement("INSERT INTO message(idUser, title, message, date, read, module) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			} else {
				stmt = conn.prepareStatement("UPDATE message SET idUser=?, title=?, message=?, date=?, read=?, module=? WHERE idMessage=?");
			}
			
			stmt.setInt(1, message.getUser().getIdUser());
			stmt.setString(2, message.getTitle());
			stmt.setString(3, message.getMessage());
			stmt.setTimestamp(4, new java.sql.Timestamp(message.getDate().getTime()));
			stmt.setInt(5, (message.isRead() ? 1 : 0));
			stmt.setInt(6, message.getModule().getValue());
			
			if(!insert){
				stmt.setInt(7, message.getIdMessage());
			}
			
			stmt.execute();
			
			if(insert) {
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()) {
					message.setIdMessage(rs.getInt(1));
				}
			}
			
			return message.getIdMessage();
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Message loadObject(ResultSet rs) throws SQLException {
		Message message = new Message();
		
		message.setIdMessage(rs.getInt("idMessage"));
		message.getUser().setIdUser(rs.getInt("idUser"));
		message.getUser().setName(rs.getString("name"));
		message.setTitle(rs.getString("title"));
		message.setMessage(rs.getString("message"));
		message.setRead(rs.getInt("read") == 1);
		message.setDate(rs.getTimestamp("date"));
		message.setModule(SystemModule.valueOf(rs.getInt("module")));
		
		return message;
	}

}
