package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProfessorProfileWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditStudentProfileWindow;

@PageTitle("SIACOES")
@Route(value = "", layout = MainLayout.class)
public class Home extends LoggedView implements AfterNavigationObserver, HasUrlParameter<String> {
	
	private Image logoDepartment;
	private Image logoUniversity;
	
	private Anchor anchorDepartment;
	private Anchor anchorUniversity;

	public Home() {
		setId("home");
		setViewContent(createContent());
	}

	private Component createContent() {
		H1 label = new H1("SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	
    	this.logoDepartment = new Image();
    	this.logoDepartment.setMaxHeight("300px");
    	this.logoDepartment.setMaxWidth("500px");
    	
    	this.anchorDepartment = new Anchor();
    	this.anchorDepartment.setTarget("_blank");
    	this.anchorDepartment.getElement().getStyle().set("text-align", "center");
    	this.anchorDepartment.getElement().getStyle().set("align-self", "center");
    	this.anchorDepartment.add(this.logoDepartment);
    	
    	this.logoUniversity = new Image();
    	this.logoUniversity.setMaxHeight("300px");
    	this.logoUniversity.setMaxWidth("500px");
    	
    	this.anchorUniversity = new Anchor();
    	this.anchorUniversity.setTarget("_blank");
    	this.anchorUniversity.getElement().getStyle().set("text-align", "center");
    	this.anchorUniversity.getElement().getStyle().set("align-self", "center");
    	this.anchorUniversity.add(this.logoUniversity);
    	
    	HorizontalLayout layoutLogo = new HorizontalLayout();
    	layoutLogo.addAndExpand(this.anchorDepartment, this.anchorUniversity);
    	layoutLogo.setSpacing(true);
    	layoutLogo.setSizeFull();
    	
    	VerticalLayout layoutMain = new VerticalLayout(label, layoutLogo);
    	layoutMain.setSpacing(true);
    	layoutMain.expand(layoutLogo);
    	layoutMain.setSizeFull();
    	layoutMain.setAlignItems(Alignment.CENTER);
    	
    	this.loadImages();
    	
    	return layoutMain;
	}
	
	private void loadImages() {
		try {
    		Department department = new DepartmentBO().findById(Session.getSelectedDepartment().getDepartment().getIdDepartment());
    		
    		StreamResource resource = new StreamResource("department" + String.valueOf(department.getIdDepartment()) + ".png", () -> new ByteArrayInputStream(department.getLogo()));
			resource.setCacheTime(0);
			this.logoDepartment.setSrc(resource);
			this.anchorDepartment.setHref(department.getSite());
    		
    		Campus campus = new CampusBO().findById(department.getCampus().getIdCampus());
    		
    		StreamResource resource2 = new StreamResource("campus" + String.valueOf(campus.getIdCampus()) + ".png", () -> new ByteArrayInputStream(campus.getLogo()));
			resource2.setCacheTime(0);
			this.logoUniversity.setSrc(resource2);
			this.anchorUniversity.setHref(campus.getSite());
		} catch(Exception e) {
    		e.printStackTrace();
    	}
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		try {
			if((Session.getUser() != null) && (Session.getUser().getIdUser() != 0)) {
				if(Session.getSelectedProfile() == UserProfile.STUDENT) {
					if((Session.getListDepartments().size() == 0) || (Session.getSelectedDepartment().getRegisterYear() < 1900)) {
						EditStudentProfileWindow window = new EditStudentProfileWindow(Session.getUser(), Session.getSelectedDepartment());
						
						window.setClosable(false);
						window.open();
					}
				} else if(Session.getSelectedProfile() == UserProfile.PROFESSOR) {
					if(Session.getListDepartments().size() == 0) {
						EditProfessorProfileWindow window = new EditProfessorProfileWindow(Session.getUser(), Session.getSelectedDepartment());
						
						window.setClosable(false);
						window.open();
					}
				}
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
		HttpServletRequest httpServletRequest = ((VaadinServletRequest)vaadinRequest).getHttpServletRequest();
		String requestUrl = httpServletRequest.getRequestURL().toString();
		
		if(parameter != null && !parameter.trim().isEmpty()) {
			
		}
	}

}
