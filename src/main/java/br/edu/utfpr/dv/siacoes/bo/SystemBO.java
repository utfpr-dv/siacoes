package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SystemDAO;

public class SystemBO {

	public int getDatabaseSize() throws Exception {
		try {
			return new SystemDAO().getDatabaseSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int getCertificatesSize() throws Exception {
		try {
			return new SystemDAO().getCertificatesSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int getSigacSize() throws Exception {
		try {
			return new SystemDAO().getSigacSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int getSigesSize() throws Exception {
		try {
			return new SystemDAO().getSigesSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int getSigetSize() throws Exception {
		try {
			return new SystemDAO().getSigetSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public int getLogSize() throws Exception {
		try {
			return new SystemDAO().getLogSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getDatabaseSizeAsString() throws Exception {
		try {
			return this.getFormattedBytes(this.getDatabaseSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getCertificatesSizeAsString() throws Exception {
		try {
			return this.getFormattedBytes(this.getCertificatesSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getSigacSizeAsString() throws Exception {
		try {
			return this.getFormattedBytes(this.getSigacSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getSigesSizeAsString() throws Exception {
		try {
			return this.getFormattedBytes(this.getSigesSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getSigetSizeAsString() throws Exception {
		try {
			return this.getFormattedBytes(this.getSigetSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getLogSizeAsString() throws Exception {
		try {
			return this.getFormattedBytes(this.getLogSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	private String getFormattedBytes(int bytes) {
		String[] units = {"bytes", "KB", "MB", "GB", "TB", "PB", "YB"};
		int i = 0;
		float bytes2 = bytes;
		
		while((i < (units.length - 1)) && (bytes2 > 1024)) {
			bytes2 = bytes2 / 1024;
			i++;
		}
		
		return String.format("%.2f %s", bytes2, units[i]);
	}
	
}
