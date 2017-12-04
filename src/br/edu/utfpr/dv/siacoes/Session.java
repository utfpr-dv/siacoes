package br.edu.utfpr.dv.siacoes;

import com.vaadin.server.VaadinSession;

import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class Session {
	
	public static boolean isAuthenticated(){
		return ((VaadinSession.getCurrent().getAttribute("user") != null) && (((User)VaadinSession.getCurrent().getAttribute("user")).getIdUser() != 0));
	}

	public static void setUser(User user){
		VaadinSession.getCurrent().setAttribute("user", user);
	}
	
	public static User getUser(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return new User();
		}else{
			return (User)VaadinSession.getCurrent().getAttribute("user");
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
			return ((Session.getUser().getProfile() == User.UserProfile.PROFESSOR) || (Session.getUser().getProfile() == User.UserProfile.ADMINISTRATOR));
		}
	}
	
	public static boolean isUserAdministrator(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return (Session.getUser().getProfile() == User.UserProfile.ADMINISTRATOR);
		}
	}
	
	public static boolean isUserStudent(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return (Session.getUser().getProfile() == User.UserProfile.STUDENT);
		}
	}
	
	public static boolean isUserManager(SystemModule module){
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
