package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.LoginEvent;

public class LoginLogDataSource extends BasicDataSource {

	private Date date;
	private String type;
	private String ip;
	private String browser;
	
	public LoginLogDataSource(LoginEvent login) {
		this.setId((int)login.getIdLog());
		this.setDate(login.getDate());
		this.setIp(login.getSource());
		this.setType(login.getEvent().toString());
		this.setBrowser(login.getDevice());
	}
	
	public static List<LoginLogDataSource> load(List<LoginEvent> list) {
		List<LoginLogDataSource> ret = new ArrayList<LoginLogDataSource>();
		
		for(LoginEvent login : list) {
			ret.add(new LoginLogDataSource(login));
		}
		
		return ret;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	
}
