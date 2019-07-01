package br.edu.utfpr.dv.siacoes.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class LoginEvent {
	
	private long idLog;
	private User user;
	private Event event;
	private Date date;
	private String source;
	private String device;
	
	public LoginEvent() {
		this.setIdLog(0);
		this.setUser(new User());
		this.setEvent(Event.LOGIN);
		this.setDate(new Date());
		this.setSource("");
		this.setDevice("");
	}
	
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}

	public enum Event{
		LOGIN(0), LOGOUT(1);
		
		private final int value; 
		Event(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static Event valueOf(int value){
			for(Event d : Event.values()){
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
				case LOGIN:
					return "Login";
				case LOGOUT:
					return "Logout";
				default:
					return "Não Identificado";
			}
		}
	}
	
	public static void registerLogin(int idUser, String source, String device) {
		try {
			LoginEvent.registerEvent(Event.LOGIN, idUser, source, device);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, "LOG ERROR: " + e.getMessage(), e);
		}
	}
	
	public static void registerLogout(int idUser, String source, String device) {
		try {
			LoginEvent.registerEvent(Event.LOGOUT, idUser, source, device);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, "LOG ERROR: " + e.getMessage(), e);
		}
	}
	
	public static List<LoginEvent> list(int idUser, Date startDate, Date endDate) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
					"SELECT loginlog.*, \"user\".name AS username, \"user\".login AS login " +
					"FROM loginlog INNER JOIN \"user\" ON loginlog.idUser=\"user\".idUser " +
					"WHERE loginlog.idUser=? AND loginlog.date BETWEEN ? AND ? " +
					"ORDER BY loginlog.date, loginlog.event");
			
			stmt.setInt(1, idUser);
			stmt.setTimestamp(2, new java.sql.Timestamp(DateUtils.getDayBegin(startDate).getTime()));
			stmt.setTimestamp(3, new java.sql.Timestamp(DateUtils.getDayEnd(endDate).getTime()));
					
			rs = stmt.executeQuery();
			
			List<LoginEvent> list = new ArrayList<LoginEvent>();
			
			while(rs.next()) {
				LoginEvent log = new LoginEvent();
				
				log.setIdLog(rs.getLong("idLog"));
				log.getUser().setIdUser(rs.getInt("idUser"));
				log.getUser().setName(rs.getString("username"));
				log.getUser().setLogin(rs.getString("login"));
				log.setEvent(Event.valueOf(rs.getInt("event")));
				log.setDate(rs.getTimestamp("date"));
				log.setSource(rs.getString("source"));
				log.setDevice(rs.getString("device"));
				
				list.add(log);
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
	
	private static void registerEvent(Event event, int idUser, String source, String device) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			
			stmt = conn.prepareStatement("INSERT INTO loginlog(idUser, event, date, source, device) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			stmt.setInt(1, idUser);
			stmt.setInt(2, event.getValue());
			stmt.setTimestamp(3, new java.sql.Timestamp(DateUtils.getNow().getTime().getTime()));
			stmt.setString(4, source);
			stmt.setString(5, device);
			
			stmt.execute();
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public static Map<Integer, Integer> listTotalByYear(int year) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT month(date) AS \"month\", COUNT(*) AS total FROM loginlog WHERE year(date) = ? GROUP BY month(date) ORDER BY \"month\"");
			
			stmt.setInt(1, year);
			
			rs = stmt.executeQuery();
			
			Map<Integer, Integer> list = new HashMap<Integer, Integer>();
			
			while(rs.next()) {
				list.put(rs.getInt("month"), rs.getInt("total"));
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
	
	public static Map<Integer, Integer> listTotalByMonth(int year, int month) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT day(date) AS \"day\", COUNT(*) AS total FROM loginlog WHERE year(date) = ? AND month(date) = ? GROUP BY month(date), day(date) ORDER BY \"day\"");
			
			stmt.setInt(1, year);
			stmt.setInt(2, month);
			
			rs = stmt.executeQuery();
			
			Map<Integer, Integer> list = new HashMap<Integer, Integer>();
			
			while(rs.next()) {
				list.put(rs.getInt("day"), rs.getInt("total"));
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

}
