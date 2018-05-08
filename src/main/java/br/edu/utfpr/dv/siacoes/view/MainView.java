package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditProfessorProfileWindow;
import br.edu.utfpr.dv.siacoes.window.EditStudentProfileWindow;

public class MainView extends BasicView {
	
	public static final String NAME = "home";
	
	private final Link logoUTFPR;
    private final Link logoES;
    private final Label label;
    
    public MainView(){
    	this.setCaption(SystemModule.GENERAL.getDescription());
    	
    	this.label = new Label("SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	this.label.setStyleName("Title");
    	
    	this.logoES = new Link();
    	this.logoES.setTargetName("_blank");
    	this.logoES.setStyleName("ImageHome");
    	this.logoES.setSizeUndefined();
    	
    	this.logoUTFPR = new Link(null, new ExternalResource("http://www.utfpr.edu.br"));
    	this.logoUTFPR.setIcon(new ThemeResource("images/assinatura_UTFPR.png"));
    	this.logoUTFPR.setTargetName("_blank");
    	this.logoUTFPR.setStyleName("ImageHome");
    	this.logoUTFPR.setSizeUndefined();
    	
    	HorizontalLayout layoutLogo = new HorizontalLayout(this.logoES, this.logoUTFPR);
    	layoutLogo.setSpacing(true);
    	layoutLogo.setSizeFull();
    	layoutLogo.setComponentAlignment(this.logoES, Alignment.MIDDLE_CENTER);
    	layoutLogo.setComponentAlignment(this.logoUTFPR, Alignment.MIDDLE_CENTER);
    	
    	VerticalLayout layoutMain = new VerticalLayout(this.label, layoutLogo);
    	layoutMain.setSpacing(true);
    	layoutMain.setExpandRatio(layoutLogo, 1);
    	layoutMain.setSizeFull();
    	
    	this.setContent(layoutMain);
    }
    
    private void loadImages(){
    	try{
    		DepartmentBO dbo = new DepartmentBO();
    		Department department = dbo.findById(Session.getSelectedDepartment().getDepartment().getIdDepartment());
    		
    		this.logoES.setResource(new ExternalResource(department.getSite()));
    		if(department.getLogo() != null){
    			StreamResource resource = new StreamResource(
    	            new StreamResource.StreamSource() {
    	                @Override
    	                public InputStream getStream() {
    	                    return new ByteArrayInputStream(department.getLogo());
    	                }
    	            }, "department" + String.valueOf(department.getIdDepartment()) + ".png");
    	
    		    this.logoES.setIcon(resource);
    		}
    		
    		CampusBO cbo = new CampusBO();
    		Campus campus = cbo.findById(department.getCampus().getIdCampus());
    		
    		this.logoUTFPR.setResource(new ExternalResource(campus.getSite()));
    		if(campus.getLogo() != null){
    			StreamResource resource = new StreamResource(
    	            new StreamResource.StreamSource() {
    	                @Override
    	                public InputStream getStream() {
    	                    return new ByteArrayInputStream(campus.getLogo());
    	                }
    	            }, "campus" + String.valueOf(campus.getIdCampus()) + ".png");
    	
    		    this.logoUTFPR.setIcon(resource);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

	@Override
	public void enter(ViewChangeEvent event) {
		try {
			if((Session.getUser() != null) && (Session.getUser().getIdUser() != 0)) {
				if(Session.getSelectedProfile() == UserProfile.STUDENT) {
					if((Session.getListDepartments().size() == 0) || (Session.getSelectedDepartment().getRegisterYear() < 1900)) {
						EditStudentProfileWindow window = new EditStudentProfileWindow(Session.getUser(), Session.getSelectedDepartment());
						
						window.setClosable(false);
						UI.getCurrent().addWindow(window);
					}
				} else if(Session.getSelectedProfile() == UserProfile.PROFESSOR) {
					if(Session.getListDepartments().size() == 0) {
						EditProfessorProfileWindow window = new EditProfessorProfileWindow(Session.getUser(), Session.getSelectedDepartment());
						
						window.setClosable(false);
						UI.getCurrent().addWindow(window);
					}
				}
			}
			
			this.loadImages();
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
