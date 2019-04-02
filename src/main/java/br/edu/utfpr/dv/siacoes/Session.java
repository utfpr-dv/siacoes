package br.edu.utfpr.dv.siacoes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.log.LoginEvent;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class Session {
	
	public static boolean isAuthenticated(){
		return ((VaadinSession.getCurrent().getAttribute("user") != null) && (((User)VaadinSession.getCurrent().getAttribute("user")).getIdUser() != 0));
	}

	public static void setUser(User user) {
		VaadinSession.getCurrent().setAttribute("user", user);
		if((user != null) && (user.getProfiles() != null) && (user.getProfiles().size() > 0)) {
			Session.setSelectedProfile(user.getProfiles().get(0));	
		} else {
			Session.setSelectedProfile(null);
		}
		
		if((user != null) && ((Session.getAdministrator() == null) || (Session.getAdministrator().getIdUser() == 0))) {
			WebBrowser webBrowser = UI.getCurrent().getPage().getWebBrowser();
			LoginEvent.registerLogin(user.getIdUser(), webBrowser.getAddress(), webBrowser.getBrowserApplication() + " " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion());
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
		Session.loadListDepartments();
	}
	
	public static UserProfile getSelectedProfile(){
		if(VaadinSession.getCurrent().getAttribute("profile") == null) {
			if(Session.getUser().getProfiles().size() > 0) {
				Session.setSelectedProfile(Session.getUser().getProfiles().get(0));
				Session.loadListDepartments();
				return Session.getUser().getProfiles().get(0);
			} else {
				return UserProfile.STUDENT;
			}
		} else {
			return (UserProfile)VaadinSession.getCurrent().getAttribute("profile");
		}
	}
	
	private static void loadListDepartments() {
		try {
			List<UserDepartment> departments = new UserDepartmentBO().list(Session.getUser().getIdUser(), Session.getSelectedProfile());
			
			VaadinSession.getCurrent().setAttribute("listdepartment", departments);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			VaadinSession.getCurrent().setAttribute("listdepartment", new ArrayList<UserDepartment>());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<UserDepartment> getListDepartments() {
		if(VaadinSession.getCurrent().getAttribute("listdepartment") == null) {
			Session.loadListDepartments();
		}
		
		return (List<UserDepartment>)VaadinSession.getCurrent().getAttribute("listdepartment");
	}
	
	public static void setSelectedDepartment(UserDepartment department) {
		VaadinSession.getCurrent().setAttribute("department", department);
	}
	
	public static UserDepartment getSelectedDepartment() {
		if(VaadinSession.getCurrent().getAttribute("department") == null) {
			try {
				List<UserDepartment> departments = Session.getListDepartments();
				
				if((departments != null) && (departments.size() > 0)) {
					Session.setSelectedDepartment(departments.get(0));
					return departments.get(0);	
				} else {
					UserDepartment department = new UserDepartment();
					
					department.setProfile(Session.getSelectedProfile());
					
					return department;
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				UserDepartment department = new UserDepartment();
				
				department.setProfile(Session.getSelectedProfile());
				
				return department;
			}
		} else {
			return (UserDepartment)VaadinSession.getCurrent().getAttribute("department");
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
	
	public static void loginAs(User user) {
		if(Session.isUserAdministrator()){
			if(VaadinSession.getCurrent().getAttribute("admin") == null){
				Session.setAdministrator(Session.getUser());
			}
			
			Session.setUser(user);
			if(Session.getUser().getProfiles().size() > 0) {
				Session.setSelectedProfile(Session.getUser().getProfiles().get(0));
			} else {
				Session.setSelectedProfile(UserProfile.STUDENT);
			}
			if(Session.getListDepartments().size() > 0) {
				Session.setSelectedDepartment(Session.getListDepartments().get(0));
			} else {
				Session.setSelectedDepartment(null);
			}
		}
	}
	
	public static void logoffAs() {
		if(VaadinSession.getCurrent().getAttribute("admin") != null){
			Session.setUser(Session.getAdministrator());
			Session.setAdministrator(null);
			if(Session.getUser().getProfiles().size() > 0) {
				Session.setSelectedProfile(Session.getUser().getProfiles().get(0));
			} else {
				Session.setSelectedProfile(UserProfile.STUDENT);
			}
			if(Session.getListDepartments().size() > 0) {
				Session.setSelectedDepartment(Session.getListDepartments().get(0));
			} else {
				Session.setSelectedDepartment(null);
			}
		}
	}
	
	public static boolean isLoggedAs(){
		return (VaadinSession.getCurrent().getAttribute("admin") != null);
	}
	
	public static void logoff() {
		WebBrowser webBrowser = UI.getCurrent().getPage().getWebBrowser();
		LoginEvent.registerLogout(Session.getUser().getIdUser(), webBrowser.getAddress(), webBrowser.getBrowserApplication() + " " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion());
		
		Session.setUser(null);
        Session.setAdministrator(null);
        Session.setSelectedProfile(null);
        Session.setSelectedDepartment(null);
        Cookie cookie = new Cookie("credentials", null);
        cookie.setMaxAge(60 * 24 * 3600);
		cookie.setPath("/");
        VaadinService.getCurrentResponse().addCookie(cookie);
        //VaadinSession.getCurrent().close();
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
	
	public static boolean isUserAdministrative(){
		if(VaadinSession.getCurrent().getAttribute("user") == null){
			return false;
		}else{
			return (Session.getSelectedProfile() == User.UserProfile.ADMINISTRATIVE);
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
		}else if((module == SystemModule.SIGAC) && Session.getSelectedDepartment().isSigacManager()){
			return true;
		}else if((module == SystemModule.SIGES) && Session.getSelectedDepartment().isSigesManager()){
			return true;
		}else if((module == SystemModule.SIGET) && Session.getSelectedDepartment().isSigetManager()){
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
		
		return Session.getSelectedDepartment().isDepartmentManager();
	}
	
	public static void putReport(byte[] report, String id){
		VaadinSession.getCurrent().setAttribute(id, report);
	}
	
	public static byte[] getReport(String id){
		byte[] report = (byte[]) VaadinSession.getCurrent().getAttribute(id);
		
		VaadinSession.getCurrent().setAttribute(id, null);
		
		return report;
	}
	
	public static int getIdUserLog() {
		if((Session.getAdministrator() != null) && (Session.getAdministrator().getIdUser() != 0)) {
			return Session.getAdministrator().getIdUser();
		} else if(Session.getUser() != null) {
			return Session.getUser().getIdUser();
		} else {
			return 0;
		}
	}
	
}
