package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class UserInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String login;
	private String email;
	private String phone;
	private String institution;
	private String research;
	private String area;
	private String lattes;
	private String studentCode;
	private int registerSemester;
	private int registerYear;
	private byte[] photo;
	private boolean external;
	private boolean active;
	
	public UserInfo() {
		this.setName("");
		this.setLogin("");
		this.setEmail("");
		this.setPhone("");
		this.setInstitution("");
		this.setResearch("");
		this.setArea("");
		this.setLattes("");
		this.setExternal(true);
		this.setActive(true);
		this.setStudentCode("");
		this.setRegisterSemester(DateUtils.getSemester());
		this.setRegisterYear(DateUtils.getYear());
		this.setPhoto(null);
	}
	
	public UserInfo(User user, UserDepartment department) {
		this.setName(user.getName());
		this.setLogin(user.getLogin());
		this.setEmail(user.getEmail());
		this.setPhone(user.getPhone());
		this.setInstitution(user.getInstitution());
		this.setResearch(user.getResearch());
		this.setArea(user.getArea());
		this.setLattes(user.getLattes());
		this.setExternal(user.isExternal());
		this.setActive(user.isActive());
		this.setStudentCode(user.getStudentCode());
		this.setPhoto(user.getPhoto());
		
		if(department != null) {
			this.setRegisterSemester(department.getRegisterSemester());
			this.setRegisterYear(department.getRegisterYear());
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getResearch() {
		return research;
	}
	public void setResearch(String research) {
		this.research = research;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getLattes() {
		return lattes;
	}
	public void setLattes(String lattes) {
		this.lattes = lattes;
	}
	public String getStudentCode() {
		return studentCode;
	}
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
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
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public boolean isExternal() {
		return external;
	}
	public void setExternal(boolean external) {
		this.external = external;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

}
