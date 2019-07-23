package br.edu.utfpr.dv.siacoes.model;

public class Credential {
	
	private String login;
	private String password;
	private String device;
	
	public Credential() {
		this.setLogin("");
		this.setPassword("");
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}

}
