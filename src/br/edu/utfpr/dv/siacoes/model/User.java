package br.edu.utfpr.dv.siacoes.model;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class User {
	
	public enum UserProfile{
		STUDENT(0), PROFESSOR(1), ADMINISTRATOR(2), MANAGER(3);
		
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
					return "Aluno";
				default:
					return "";
			}
		}
	}
	
	private int idUser;
	private Department department;
	private String name;
	private String login;
	private String password;
	private String email;
	private String institution;
	private String research;
	private String area;
	private String lattes;
	private UserProfile profile;
	private boolean external;
	private boolean active;
	private boolean sigacManager;
	private boolean sigesManager;
	private boolean sigetManager;
	private String studentCode;
	private int registerSemester;
	private int registerYear;
	
	public User(){
		this.setIdUser(0);
		this.setDepartment(new Department());
		this.setName("");
		this.setLogin("");
		this.setPassword("");
		this.setEmail("");
		this.setInstitution("");
		this.setResearch("");
		this.setArea("");
		this.setLattes("");
		this.setProfile(UserProfile.STUDENT);
		this.setExternal(true);
		this.setActive(true);
		this.setSigacManager(false);
		this.setSigesManager(false);
		this.setSigetManager(false);
		this.setStudentCode("");
		this.setRegisterSemester(DateUtils.getSemester());
		this.setRegisterYear(DateUtils.getYear());
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
	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public UserProfile getProfile(){
		return profile;
	}
	public void setProfile(UserProfile profile){
		this.profile = profile;
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
	public void setDepartment(Department department){
		this.department = department;
	}
	public Department getDepartment(){
		return department;
	}
	public void setSigacManager(boolean sigacManager){
		this.sigacManager = sigacManager;
	}
	public boolean isSigacManager(){
		return sigacManager;
	}
	public void setSigesManager(boolean sigesManager){
		this.sigesManager = sigesManager;
	}
	public boolean isSigesManager(){
		return sigesManager;
	}
	public void setSigetManager(boolean sigetManager){
		this.sigetManager = sigetManager;
	}
	public boolean isSigetManager(){
		return this.sigetManager;
	}
	public void setStudentCode(String studentCode){
		this.studentCode = studentCode;
	}
	public String getStudentCode(){
		return studentCode;
	}
	public void setRegisterSemester(int registerSemester){
		this.registerSemester = registerSemester;
	}
	public int getRegisterSemester(){
		return registerSemester;
	}
	public void setRegisterYear(int registerYear){
		this.registerYear = registerYear;
	}
	public int getRegisterYear(){
		return registerYear;
	}
	
	@Override
	public String toString(){
		return this.getName();
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
