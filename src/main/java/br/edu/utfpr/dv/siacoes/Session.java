package br.edu.utfpr.dv.siacoes;

import com.vaadin.server.VaadinSession;

import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class Session {
	
	public static boolean isAuthenticated(){
		return ((VaadinSession.getCurrent().getAttribute("user") != null) && (((User)VaadinSession.getCurrent().getAttribute("user")).getIdUser() != 0));
	}

	public static void setUser(User user){
		VaadinSession.getCurrent().setAttribute("user", user);
		if((user != null) && (user.getProfiles() != null) && (user.getProfiles().size() > 0)) {
			Session.setSelectedProfile(user.getProfiles().get(0));	
		} else {
			Session.setSelectedProfile(null);
		}
	}
	
	public static User getUser(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return new User();
		}else{
			return (User)VaadinSession.getCurrent().getAttribute("user");
		}
	}
	
	public static void setSelectedProfile(UserProfile profile){
		VaadinSession.getCurrent().setAttribute("profile", profile);
	}
	
	public static UserProfile getSelectedProfile(){
		if(VaadinSession.getCurrent().getAttribute("profile") == null) {
			if(Session.getUser().getProfiles().size() > 0) {
				return Session.getUser().getProfiles().get(0);	
			} else {
				return UserProfile.STUDENT;
			}
		} else {
			return (UserProfile)VaadinSession.getCurrent().getAttribute("profile");
		}
	}
	
	public static void setAdministrator(User user){
		VaadinSession.getCurrent().setAttribute("admin", user);
	}
	
	public static User getAdministrator(){
		if(VaadinSession.getCurrent().getAttribute("admin") == null){
			return new User();
		}else{
			return (User)VaadinSession.getCurrent().getAttribute("admin");
		}
	}
	
	public static void loginAs(User user){
		if(Session.isUserAdministrator()){
			if(VaadinSession.getCurrent().getAttribute("admin") == null){
				Session.setAdministrator(Session.getUser());
			}
			
			Session.setUser(user);
		}
	}
	
	public static void logoffAs(){
		if(VaadinSession.getCurrent().getAttribute("admin") != null){
			Session.setUser(Session.getAdministrator());
			Session.setAdministrator(null);
		}
	}
	
	public static boolean isLoggedAs(){
		return (VaadinSession.getCurrent().getAttribute("admin") != null);
	}
	
	public static void logoff(){
		Session.setUser(null);
        Session.setAdministrator(null);
	}
	
	public static boolean isUserProfessor(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return (Session.getSelectedProfile() == User.UserProfile.PROFESSOR);
		}
	}
	
	public static boolean isUserAdministrator(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return (Session.getSelectedProfile() == User.UserProfile.ADMINISTRATOR);
		}
	}
	
	public static boolean isUserStudent(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return (Session.getSelectedProfile() == User.UserProfile.STUDENT);
		}
	}
	
	public static boolean isUserSupervisor(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return ((Session.getSelectedProfile() == User.UserProfile.SUPERVISOR) || (Session.getSelectedProfile() == User.UserProfile.PROFESSOR));
		}
	}
	
	public static boolean isUserCompanySupervisor(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return (Session.getSelectedProfile() == User.UserProfile.COMPANYSUPERVISOR);
		}
	}
	
	public static boolean isUserManager(SystemModule module){
		if(Session.isUserAdministrator()) {
			return true;
		}
		if(!Session.isUserProfessor()){
			return false;
		}
		
		if(module == SystemModule.GENERAL){
			return true;
		}else if((module == SystemModule.SIGAC) && Session.getUser().isSigacManager()){
			return true;
		}else if((module == SystemModule.SIGES) && Session.getUser().isSigesManager()){
			return true;
		}else if((module == SystemModule.SIGET) && Session.getUser().isSigetManager()){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isUserDepartmentManager(){
		if(Session.isUserAdministrator()) {
			return true;
		}
		if(!Session.isUserProfessor()){
			return false;
		}
		
		return Session.getUser().isDepartmentManager();
	}
	
	public static void putReport(byte[] report, String id){
		VaadinSession.getCurrent().setAttribute(id, report);
	}
	
	public static byte[] getReport(String id){
		byte[] report = (byte[]) VaadinSession.getCurrent().getAttribute(id);
		
		VaadinSession.getCurrent().setAttribute(id, null);
		
		return report;
	}
	
}
