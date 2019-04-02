package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class Certificate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idCertificate;
	private Department department;
	private User user;
	private SystemModule module;
	private Date date;
	private String guid;
	private transient byte[] file;
	
	public Certificate(){
		this.setIdCertificate(0);
		this.setDepartment(new Department());
		this.setUser(new User());
		this.setModule(SystemModule.GENERAL);
		this.setDate(DateUtils.getNow().getTime());
		this.setGuid("");
		this.setFile(null);
	}
	
	public int getIdCertificate() {
		return idCertificate;
	}
	public void setIdCertificate(int idCertificate) {
		this.idCertificate = idCertificate;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public SystemModule getModule() {
		return module;
	}
	public void setModule(SystemModule module) {
		this.module = module;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}

}
