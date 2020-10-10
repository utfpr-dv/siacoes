package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;

public class UserDepartmentDataSource extends BasicDataSource {

	private int idDepartment;
	private UserProfile idProfile;
	private String department;
	private String campus;
	private String profile;
	
	public UserDepartmentDataSource(UserDepartment department) {
		this.setIdDepartment(department.getDepartment().getIdDepartment());
		this.setIdProfile(department.getProfile());
		this.setId(department.getIdUserDepartment());
		this.setDepartment(department.getDepartment().getName());
		this.setCampus(department.getDepartment().getCampus().getName());
		this.setProfile(department.getProfile().toString());
	}
	
	public static List<UserDepartmentDataSource> load(List<UserDepartment> list) {
		List<UserDepartmentDataSource> ret = new ArrayList<UserDepartmentDataSource>();
		
		for(UserDepartment department : list) {
			ret.add(new UserDepartmentDataSource(department));
		}
		
		return ret;
	}
	
	public int getIdDepartment() {
		return idDepartment;
	}
	public void setIdDepartment(int idDepartment) {
		this.idDepartment = idDepartment;
	}
	public UserProfile getIdProfile() {
		return idProfile;
	}
	public void setIdProfile(UserProfile idProfile) {
		this.idProfile = idProfile;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getCampus() {
		return campus;
	}
	public void setCampus(String campus) {
		this.campus = campus;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
}
