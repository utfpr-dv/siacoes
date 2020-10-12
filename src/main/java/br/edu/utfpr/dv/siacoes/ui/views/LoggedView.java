package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class LoggedView extends ViewFrame implements BeforeEnterObserver {
	
	private UserProfile profilePermissions;
    private SystemModule module;
	
	public void setModule(SystemModule module){
    	this.module = module;
    }
	
	public SystemModule getModule(){
    	return this.module;
    }
    
    public void setProfilePerimissions(UserProfile profile){
    	this.profilePermissions = profile;
    }
    
    public UserProfile getProfilePermissions(){
    	return this.profilePermissions;
    }

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		boolean isLoggedIn = Session.isAuthenticated();
		
		if (!isLoggedIn) {
            event.forwardTo(LoginView.class);
        } else {
        	if((this.getProfilePermissions() == UserProfile.ADMINISTRATOR) && (!Session.isUserAdministrator())){
        		event.rerouteTo("403");
        	} else if((this.getProfilePermissions() == UserProfile.MANAGER) && !Session.isUserManager(this.getModule()) && !Session.isUserDepartmentManager()){
        		event.rerouteTo("403");
        	} else if((this.getProfilePermissions() == UserProfile.PROFESSOR) && (!Session.isUserProfessor())) {
        		event.rerouteTo("403");
        	} else if((this.getProfilePermissions() == UserProfile.SUPERVISOR) && (!Session.isUserProfessor() && !Session.isUserSupervisor())) {
        		event.rerouteTo("403");
        	} else if((this.getProfilePermissions() == UserProfile.COMPANYSUPERVISOR) && (!Session.isUserCompanySupervisor())) {
        		event.rerouteTo("403");
        	} else if((this.getProfilePermissions() == UserProfile.ADMINISTRATIVE) && (!Session.isUserAdministrative())) {
        		event.rerouteTo("403");
        	}
        }
	}
	
}
