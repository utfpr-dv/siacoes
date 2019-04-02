package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

public class EmailConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int idEmailConfig;
	private String host;
	private String user;
	private String password;
	private int port;
	private boolean enableSsl;
	private boolean authenticate;
	private String signature;
	
	public EmailConfig(){
		this.setIdEmailConfig(0);
		this.setHost("");
		this.setUser("");
		this.setPassword("");
		this.setPort(0);
		this.setEnableSsl(false);
		this.setAuthenticate(false);
		this.setSignature("");
	}
	
	public int getIdEmailConfig() {
		return idEmailConfig;
	}
	public void setIdEmailConfig(int idEmailConfig) {
		this.idEmailConfig = idEmailConfig;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isEnableSsl() {
		return enableSsl;
	}
	public void setEnableSsl(boolean enableSsl) {
		this.enableSsl = enableSsl;
	}
	public boolean isAuthenticate() {
		return authenticate;
	}
	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}
