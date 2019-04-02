package br.edu.utfpr.dv.siacoes.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class UpdateEvent {
	
	public enum Event{
		INSERT(0), UPDATE(1), DELETE(2);
		
		private final int value; 
		Event(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static Event valueOf(int value) {
			for(Event d : Event.values()) {
				if(d.getValue() == value) {
					return d;
				}
			}
			
			return null;
		}
		
		public String toString() {
			return this.getDescription();
		}
		
		public String getDescription() {
			switch(this) {
				case INSERT:
					return "Inserção";
				case UPDATE:
					return "Atualização";
				case DELETE:
					return "Exclusão";
				default:
					return "Não Identificado";
			}
		}
	}
	
	private Connection conn;
	
	private long idLog;
	private User user;
	private Event event;
	private String className;
	private int idObject;
	private Date date;
	private byte[] data;
	
	public long getIdLog() {
		return idLog;
	}
	public void setIdLog(long idLog) {
		this.idLog = idLog;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getIdObject() {
		return idObject;
	}
	public void setIdObject(int idObject) {
		this.idObject = idObject;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

	public UpdateEvent() {
		this.setIdLog(0);
		this.setUser(new User());
		this.setEvent(Event.INSERT);
		this.setClassName("");
		this.setIdObject(0);
		this.setDate(new Date());
		this.setData(null);
		
		try {
			this.conn = ConnectionDAO.getInstance().getConnection();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, "LOG ERROR: " + e.getMessage(), e);
		}
	}
	
	public UpdateEvent(Connection conn) {
		this.setIdLog(0);
		this.setUser(new User());
		this.setEvent(Event.INSERT);
		this.setClassName("");
		this.setIdObject(0);
		this.setDate(new Date());
		this.setData(null);
		
		try {
			if(conn == null) {
				this.conn = ConnectionDAO.getInstance().getConnection();	
			} else {
				this.conn = conn;
			}
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, "LOG ERROR: " + e.getMessage(), e);
		}
	}
	
	public void registerInsert(int idUser, Object object) {
		try {
			this.registerEvent(Event.INSERT, idUser, object);
		} catch (IOException | SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, "LOG ERROR: " + e.getMessage(), e);
		}
	}
	
	public void registerUpdate(int idUser, Object object) {
		try {
			this.registerEvent(Event.UPDATE, idUser, object);
		} catch (IOException | SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, "LOG ERROR: " + e.getMessage(), e);
		}
	}
	
	public void registerDelete(int idUser, Object object) {
		try {
			this.registerEvent(Event.DELETE, idUser, object);
		} catch (IOException | SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, "LOG ERROR: " + e.getMessage(), e);
		}
	}
	
	private void registerEvent(Event event, int idUser, Object object) throws IOException, SQLException {
		byte[] data = UpdateEvent.zipData(object);
		String className = UpdateEvent.getClassName(object);
		int idObject = UpdateEvent.getIdObject(object);
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			this.conn = ConnectionDAO.getInstance().getConnection();
			
			stmt = this.conn.prepareStatement("INSERT INTO eventlog(idUser, event, className, idObject, date, data) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			stmt.setInt(1, idUser);
			stmt.setInt(2, event.getValue());
			stmt.setString(3, className);
			stmt.setInt(4, idObject);
			stmt.setTimestamp(5, new java.sql.Timestamp(DateUtils.getNow().getTime().getTime()));
			stmt.setBytes(6, data);
			
			stmt.execute();
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public static List<String> listClassNames() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT DISTINCT eventlog.className FROM eventlog ORDER BY className");
			
			List<String> list = new ArrayList<String>();
			
			while(rs.next()) {
				list.add(rs.getString("className"));
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
	
	public static UpdateEvent findEvent(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT eventlog.*, \"user\".name AS username, \"user\".login AS login " +
					"FROM eventlog INNER JOIN \"user\" ON eventlog.idUser=\"user\".idUser " +
					"WHERE eventlog.idlog = ?");
			
			stmt.setLong(1, id);
					
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return UpdateEvent.loadObject(rs);
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
	
	public static List<UpdateEvent> list(int idUser, String className, Date startDate, Date endDate) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT eventlog.*, \"user\".name AS username, \"user\".login AS login " +
					"FROM eventlog INNER JOIN \"user\" ON eventlog.idUser=\"user\".idUser " +
					"WHERE eventlog.date BETWEEN ? AND ? " +
					(idUser != 0 ? " AND eventlog.idUser = " + String.valueOf(idUser): "") + (!className.trim().isEmpty() ? " AND eventlog.className = ? " : "") +
					" ORDER BY eventlog.date, eventlog.event");
			
			stmt.setTimestamp(1, new java.sql.Timestamp(DateUtils.getDayBegin(startDate).getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(DateUtils.getDayEnd(endDate).getTime()));
			if(!className.trim().isEmpty()) {
				stmt.setString(3, className.trim());
			}
					
			rs = stmt.executeQuery();
			
			List<UpdateEvent> list = new ArrayList<UpdateEvent>();
			
			while(rs.next()) {
				list.add(UpdateEvent.loadObject(rs));
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
	
	public static String unzipDataToJson(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		ZipInputStream zip = new ZipInputStream(stream);
		
		zip.getNextEntry();
		
		ObjectInputStream objIn = new ObjectInputStream(zip);
		
		Object obj = objIn.readObject();
		
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.writeValueAsString(obj);
	}
	
	private static UpdateEvent loadObject(ResultSet rs) throws SQLException {
		UpdateEvent log = new UpdateEvent();
		
		log.setIdLog(rs.getLong("idLog"));
		log.getUser().setIdUser(rs.getInt("idUser"));
		log.getUser().setName(rs.getString("username"));
		log.getUser().setLogin(rs.getString("login"));
		log.setEvent(Event.valueOf(rs.getInt("event")));
		log.setDate(rs.getTimestamp("date"));
		log.setClassName(rs.getString("className"));
		log.setIdObject(rs.getInt("idObject"));
		log.setData(rs.getBytes("data"));
		
		return log;
	}
	
	private static byte[] zipData(Object object) throws IOException {
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(ret);
		
		ZipEntry e = new ZipEntry("data");
		zip.putNextEntry(e);
		
		ObjectOutputStream objOut = new ObjectOutputStream(zip);
		
		objOut.writeObject(object);
		objOut.flush();
		
		zip.closeEntry();
		
		zip.flush();
		zip.close();
		
		return ret.toByteArray();
	}
	
	private static String getClassName(Object object) {
		String ret;
		Class<?> enclosingClass = object.getClass().getEnclosingClass();
		
		if (enclosingClass != null) {
			ret = enclosingClass.getName();
		} else {
			ret = object.getClass().getName();
		}
		
		ret = ret.substring(ret.lastIndexOf('.') + 1);
		
		return ret;
	}
	
	private static int getIdObject(Object object) {
		try {
			String className = UpdateEvent.getClassName(object);
			Field field = object.getClass().getDeclaredField("id" + className);
			
			field.setAccessible(true);
			
			return field.getInt(object);
		} catch(Exception ex) {
			return 0;
		}
	}

}
