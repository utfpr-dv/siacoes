package br.edu.utfpr.dv.siacoes.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;

public class AppConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	private boolean sigacEnabled;
	private boolean sigesEnabled;
	private boolean sigetEnabled;
	private boolean mobileEnabled;
	
	private AppConfig(){
		this.setTheme(AppTheme.DEFAULT);
		this.setHost("");
		this.setSigacEnabled(true);
		this.setSigesEnabled(true);
		this.setSigetEnabled(true);
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
	public byte[] getHostQRCode(int width, int height) throws WriterException, IOException {
		String charset = "UTF-8";
		Map hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		BitMatrix matrix = new MultiFormatWriter().encode(new String(this.getHost().getBytes(charset), charset), BarcodeFormat.QR_CODE, width, height, hintMap);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		MatrixToImageWriter.writeToStream(matrix, "jpeg", stream);
		
		return stream.toByteArray();
	}
	public boolean isSigacEnabled() {
		return sigacEnabled;
	}
	public void setSigacEnabled(boolean sigacEnabled) {
		this.sigacEnabled = sigacEnabled;
	}
	public boolean isSigesEnabled() {
		return sigesEnabled;
	}
	public void setSigesEnabled(boolean sigesEnabled) {
		this.sigesEnabled = sigesEnabled;
	}
	public boolean isSigetEnabled() {
		return sigetEnabled;
	}
	public void setSigetEnabled(boolean sigetEnabled) {
		this.sigetEnabled = sigetEnabled;
	}
	public boolean isMobileEnabled() {
		return mobileEnabled;
	}
	public void setMobileEnabled(boolean mobileEnabled) {
		this.mobileEnabled = mobileEnabled;
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
				this.setSigacEnabled(rs.getInt("sigacenabled") == 1);
				this.setSigesEnabled(rs.getInt("sigesenabled") == 1);
				this.setSigetEnabled(rs.getInt("sigetenabled") == 1);
				this.setMobileEnabled(rs.getInt("mobileenabled") == 1);
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
				stmt = conn.prepareStatement("UPDATE appconfig SET theme=?, host=?, sigacenabled=?, sigesenabled=?, sigetenabled=?, mobileenabled=?");	
			}else{
				stmt = conn.prepareStatement("INSERT INTO appconfig(theme, host, sigacenabled, sigesenabled, sigetenabled, mobileenabled) VALUES(?, ?, ?, ?, ?, ?)");
			}
			
			stmt.setInt(1, this.getTheme().getValue());
			stmt.setString(2, this.getHost());
			stmt.setInt(3, this.isSigacEnabled() ? 1 : 0);
			stmt.setInt(4, this.isSigesEnabled() ? 1 : 0);
			stmt.setInt(5, this.isSigetEnabled() ? 1 : 0);
			stmt.setInt(6, this.isMobileEnabled() ? 1 : 0);
			
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
