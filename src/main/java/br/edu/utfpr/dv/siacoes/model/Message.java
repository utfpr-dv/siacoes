package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idMessage;
	private User user;
	private String title;
	private String message;
	private boolean read;
	private Date date;
	private SystemModule module;
	
	public Message() {
		this.setIdMessage(0);
		this.setUser(new User());
		this.setTitle("");
		this.setMessage("");
		this.setRead(false);
		this.setDate(new Date());
		this.setModule(SystemModule.GENERAL);
	}
	
	public int getIdMessage() {
		return idMessage;
	}
	public void setIdMessage(int idMessage) {
		this.idMessage = idMessage;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public SystemModule getModule() {
		return module;
	}
	public void setModule(SystemModule module) {
		this.module = module;
	}

}
