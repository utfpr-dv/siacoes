package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class UserDepartment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idUserDepartment;
	private User user;
	private Department department;
	private UserProfile profile;
	private boolean sigacManager;
	private boolean sigesManager;
	private boolean sigetManager;
	private boolean departmentManager;
	private int registerSemester;
	private int registerYear;
	
	public UserDepartment() {
		this.setIdUserDepartment(0);
		this.setUser(new User());
		this.setDepartment(new Department());
		this.setProfile(UserProfile.STUDENT);
		this.setSigacManager(false);
		this.setSigesManager(false);
		this.setSigetManager(false);
		this.setDepartmentManager(false);
		this.setRegisterSemester(DateUtils.getSemester());
		this.setRegisterYear(DateUtils.getYear());
	}
	
	public int getIdUserDepartment() {
		return idUserDepartment;
	}
	public void setIdUserDepartment(int idUserDepartment) {
		this.idUserDepartment = idUserDepartment;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public UserProfile getProfile() {
		return profile;
	}
	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}
	public boolean isSigacManager() {
		return sigacManager;
	}
	public void setSigacManager(boolean sigacManager) {
		this.sigacManager = sigacManager;
	}
	public boolean isSigesManager() {
		return sigesManager;
	}
	public void setSigesManager(boolean sigesManager) {
		this.sigesManager = sigesManager;
	}
	public boolean isSigetManager() {
		return sigetManager;
	}
	public void setSigetManager(boolean sigetManager) {
		this.sigetManager = sigetManager;
	}
	public boolean isDepartmentManager() {
		return departmentManager;
	}
	public void setDepartmentManager(boolean departmentManager) {
		this.departmentManager = departmentManager;
	}
	public int getRegisterSemester() {
		return registerSemester;
	}
	public void setRegisterSemester(int registerSemester) {
		this.registerSemester = registerSemester;
	}
	public int getRegisterYear() {
		return registerYear;
	}
	public void setRegisterYear(int registerYear) {
		this.registerYear = registerYear;
	}

}
