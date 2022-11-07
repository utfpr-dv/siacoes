package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.SystemDAO;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

public class SystemBO {

	public long getDatabaseSize() throws Exception {
		try {
			return new SystemDAO().getDatabaseSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getCertificatesSize() throws Exception {
		try {
			return new SystemDAO().getCertificatesSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getSigacSize() throws Exception {
		try {
			return new SystemDAO().getSigacSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getSigesSize() throws Exception {
		try {
			return new SystemDAO().getSigesSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getSigetSize() throws Exception {
		try {
			return new SystemDAO().getSigetSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getLogSize() throws Exception {
		try {
			return new SystemDAO().getLogSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public long getSignatureSize() throws Exception {
		try {
			return new SystemDAO().getSignatureSize();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getDatabaseSizeAsString() throws Exception {
		try {
			return StringUtils.getFormattedBytes(this.getDatabaseSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getCertificatesSizeAsString() throws Exception {
		try {
			return StringUtils.getFormattedBytes(this.getCertificatesSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getSigacSizeAsString() throws Exception {
		try {
			return StringUtils.getFormattedBytes(this.getSigacSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getSigesSizeAsString() throws Exception {
		try {
			return StringUtils.getFormattedBytes(this.getSigesSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getSigetSizeAsString() throws Exception {
		try {
			return StringUtils.getFormattedBytes(this.getSigetSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getLogSizeAsString() throws Exception {
		try {
			return StringUtils.getFormattedBytes(this.getLogSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
	public String getSignatureSizeAsString() throws Exception {
		try {
			return StringUtils.getFormattedBytes(this.getSignatureSize());
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e);
		}
	}
	
}
