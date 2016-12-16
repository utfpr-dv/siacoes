package br.edu.utfpr.dv.siacoes.ldap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;

public class LdapConfig {
	
	private String host;
	private int port;
	private boolean useSSL;
	private boolean ignoreCertificates;
	private String basedn;
	private String uidField;
	private String cpfField;
	private String registerField;
	private String nameField;
	private String emailField;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public boolean isIgnoreCertificates() {
		return ignoreCertificates;
	}

	public String getBasedn() {
		return basedn;
	}

	public String getUidField() {
		return uidField;
	}

	public String getCpfField() {
		return cpfField;
	}

	public String getRegisterField() {
		return registerField;
	}

	public String getNameField() {
		return nameField;
	}

	public String getEmailField() {
		return emailField;
	}

	private static LdapConfig instance = null;
	
	private LdapConfig(){}
	
	public static synchronized LdapConfig getInstance(){
		if(LdapConfig.instance == null){
			LdapConfig.instance = new LdapConfig();
			try {
				LdapConfig.instance.loadConfig();
			} catch (SQLException e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		return LdapConfig.instance;
	}
	
	private void loadConfig() throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT * FROM ldapconfig");
		
		if(rs.next()){
			this.host = rs.getString("host");
			this.port = rs.getInt("port");
			this.useSSL = (rs.getInt("useSSL") == 1);
			this.ignoreCertificates = (rs.getInt("ignoreCertificates") == 1);
			this.basedn = rs.getString("basedn");
			this.uidField = rs.getString("uidField");
			this.cpfField = rs.getString("cpfField");
			this.registerField = rs.getString("registerField");
			this.nameField = rs.getString("nameField");
			this.emailField = rs.getString("emailField");
		}
	}
	
}
