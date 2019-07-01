package br.edu.utfpr.dv.siacoes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum UserProfile{
		STUDENT(0), PROFESSOR(1), ADMINISTRATOR(2), MANAGER(3), COMPANYSUPERVISOR(4), SUPERVISOR(5), ADMINISTRATIVE(6);
		
		private final int value; 
		UserProfile(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static UserProfile valueOf(int value){
			for(UserProfile u : UserProfile.values()){
				if(u.getValue() == value){
					return u;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case MANAGER:
					return "Gerente";
				case ADMINISTRATOR:
					return "Administrador";
				case PROFESSOR:
					return "Professor";
				case STUDENT:
					return "Acadêmico";
				case COMPANYSUPERVISOR:
					return "Supervisor de Estágio";
				case SUPERVISOR:
					return "Orientador";
				case ADMINISTRATIVE:
					return "Técnico Administrativo";
				default:
					return "";
			}
		}
	}
	
	private int idUser;
	private Company company;
	private String name;
	private String login;
	private transient String password;
	private String salt;
	private String email;
	private String phone;
	private String institution;
	private String research;
	private String area;
	private String lattes;
	private List<UserProfile> profiles;
	private boolean external;
	private boolean active;
	private String studentCode;
	private transient byte[] photo;
	private List<UserDepartment> departments;
	
	public User(){
		this.setIdUser(0);
		this.setCompany(new Company());
		this.setName("");
		this.setLogin("");
		this.setPassword("");
		this.setSalt("");
		this.setEmail("");
		this.setPhone("");
		this.setInstitution("");
		this.setResearch("");
		this.setArea("");
		this.setLattes("");
		this.setProfiles(new ArrayList<UserProfile>());
		this.setExternal(true);
		this.setActive(true);
		this.setStudentCode("");
		this.setPhoto(null);
		this.setDepartments(null);
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public List<UserProfile> getProfiles(){
		return profiles;
	}
	public void setProfiles(List<UserProfile> profiles){
		this.profiles = profiles;
	}
	public boolean hasProfile(UserProfile profile) {
		for(UserProfile p : this.profiles) {
			if(p == profile) {
				return true;
			}
		}
		
		return false;
	}
	public void setInstitution(String institution){
		this.institution = institution;
	}
	public String getInstitution(){
		return institution;
	}
	public void setResearch(String research){
		this.research = research;
	}
	public String getResearch(){
		return research;
	}
	public void setArea(String area){
		this.area = area;
	}
	public String getArea(){
		return area;
	}
	public void setLattes(String lattes){
		this.lattes = lattes;
	}
	public String getLattes(){
		return lattes;
	}
	public void setExternal(boolean external){
		this.external = external;
	}
	public boolean isExternal(){
		return external;
	}
	public void setActive(boolean active){
		this.active = active;
	}
	public boolean isActive(){
		return active;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public void setStudentCode(String studentCode){
		this.studentCode = studentCode;
	}
	public String getStudentCode(){
		return studentCode;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public List<UserDepartment> getDepartments() {
		return departments;
	}
	public void setDepartments(List<UserDepartment> departments) {
		this.departments = departments;
	}

	@Override
	public String toString(){
		return this.getName();
	}
	
	public String getProfilesString() {
		String ret = "";
		
		if(this.getProfiles() != null) {
			for(UserProfile p : this.getProfiles()) {
				ret = ret + p.toString() + ", ";
			}
		}
		
		return ret;
	}
	
	@Override
    public boolean equals(final Object object) {
        if (!(object instanceof User)) {
            return false;
        }else if(this.getIdUser() == ((User)object).getIdUser()){
        	return true;
        }else{
        	return false;
        }
    }

}
