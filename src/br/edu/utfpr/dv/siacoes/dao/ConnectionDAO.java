package br.edu.utfpr.dv.siacoes.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbcp2.BasicDataSource;

public class ConnectionDAO {
	
	private String SERVER = "192.168.56.20";
	private String DATABASE = "diget";
	private String USER = "mysql";
	private String PASSWORD = "mysql";
	
	private Connection connection = null;
	private BasicDataSource bds = null;
	private static ConnectionDAO instance = null;
	
	private ConnectionDAO(){}
	
	public static synchronized ConnectionDAO getInstance() throws SQLException{
		if((ConnectionDAO.instance == null) || (ConnectionDAO.instance.getConnection() == null) || (ConnectionDAO.instance.getConnection().isClosed())){
			ConnectionDAO.instance = new ConnectionDAO();
			ConnectionDAO.instance.openConnection();
		}
		
		/*if((ConnectionDAO.instance == null) || (ConnectionDAO.instance.bds == null)){
			ConnectionDAO.instance = new ConnectionDAO();
			ConnectionDAO.instance.createDataSource();
		}*/
		
		return ConnectionDAO.instance;
	}
	
	private void openConnection() throws SQLException{
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			this.connection = DriverManager.getConnection(this.getConnectionString());
			//this.connection = DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DATABASE + "?autoReconnect=true&user=" + USER + "&password=" + PASSWORD);
			
			Statement stmt = this.connection.createStatement();
			stmt.execute("SET GLOBAL max_allowed_packet=1024*1024*14;");
		} catch (SQLException e) {
			this.connection = null;
			
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw e;
		}
	}
	
	public Connection getConnection() throws SQLException{
		if(!this.connection.isValid(0)){
			this.openConnection();
		}
		
		return this.connection;
		//return this.bds.getConnection();
	}
	
	private void createDataSource() throws SQLException{
		String user, password, server, database; 
		
		try{
			Properties props = new Properties();
	        FileInputStream fis = new FileInputStream(this.getClass().getClassLoader().getResource("/db.properties").getPath());
	        //FileInputStream fis = new FileInputStream(this.getClass().getClassLoader().getResource("/dblocal.properties").getPath());
	        
	        props.load(fis);
	        
	        server = props.getProperty("DB_SERVER");
	        database = props.getProperty("DB_NAME");
	        user = props.getProperty("DB_USERNAME");
	        password = props.getProperty("DB_PASSWORD");
	     }catch(Exception e){
	    	 server = SERVER;
	    	 database = DATABASE;
	    	 user = USER;
	    	 password = PASSWORD;
		}
		
		bds = new BasicDataSource();
		bds.setDriver(new com.mysql.jdbc.Driver());
		//bds.setDriverClassName("com.mysql.jdbc.Driver");
		bds.setUsername(user);
		bds.setPassword(password);
		bds.setUrl("jdbc:mysql://" + server + "/" + database + "?autoReconnect=true");
		bds.setTestWhileIdle(false);
        bds.setTestOnBorrow(true);
        bds.setValidationQuery("SELECT 1");
        bds.setTestOnReturn(false);
        bds.setTimeBetweenEvictionRunsMillis(30000);
        bds.setInitialSize(3);
        bds.setRemoveAbandonedTimeout(60);
        bds.setMinEvictableIdleTimeMillis(30000);
        bds.setMinIdle(1);
        bds.setLogAbandoned(true);
        
		Statement stmt = this.bds.getConnection().createStatement();
		stmt.execute("SET GLOBAL max_allowed_packet=1024*1024*14;");
	}
	
	private String getConnectionString(){
		try{
			Properties props = new Properties();
	        FileInputStream fis = new FileInputStream(this.getClass().getClassLoader().getResource("/db.properties").getPath());
	        
	        props.load(fis);
	        
	        return "jdbc:mysql://" + props.getProperty("DB_SERVER") + "/" + props.getProperty("DB_NAME") + "?autoReconnect=true&user=" + props.getProperty("DB_USERNAME") + "&password=" + props.getProperty("DB_PASSWORD");
		}catch(Exception e){
			return "jdbc:mysql://" + SERVER + "/" + DATABASE + "?autoReconnect=true&user=" + USER + "&password=" + PASSWORD;
		}
	}

}
