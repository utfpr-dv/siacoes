package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.components.MenuBar;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class SigesView extends CustomComponent implements View {
	
	public static final String NAME = "siges";
    
    private final MenuBar menu;
    private final Link logoUTFPR;
    private final Link logoES;
    private final Label label;
    
    public SigesView(){
    	this.setCaption(SystemModule.SIGAC.getDescription());
    	this.menu = new MenuBar(SystemModule.SIGES);
    	
    	this.label = new Label("Sistema de Gestão de Atividades Complementares");
    	this.label.setStyleName("Title");
    	
    	this.logoES = new Link();
    	this.logoES.setTargetName("_blank");
    	this.logoES.setStyleName("ImageHome");
    	
    	this.logoUTFPR = new Link(null, new ExternalResource("http://www.utfpr.edu.br"));
    	this.logoUTFPR.setIcon(new ThemeResource("images/assinatura_UTFPR.png"));
    	this.logoUTFPR.setTargetName("_blank");
    	this.logoUTFPR.setStyleName("ImageHome");
    	
    	HorizontalLayout layoutLogo = new HorizontalLayout(this.logoES, this.logoUTFPR);
    	layoutLogo.setSpacing(true);
    	layoutLogo.setSizeFull();
    	layoutLogo.setComponentAlignment(this.logoES, Alignment.MIDDLE_CENTER);
    	layoutLogo.setComponentAlignment(this.logoUTFPR, Alignment.MIDDLE_CENTER);
    	
    	VerticalLayout layoutMain = new VerticalLayout(this.menu, this.label, layoutLogo);
    	layoutMain.setSpacing(true);
    	layoutMain.setExpandRatio(layoutLogo, 1);
    	layoutMain.setSizeFull();
    	
    	this.setSizeFull();
    	this.setCompositionRoot(layoutMain);
    	
    	this.loadImages();
    }
    
    private void loadImages(){
    	try{
    		DepartmentBO dbo = new DepartmentBO();
    		Department department = dbo.findById(Session.getUser().getDepartment().getIdDepartment());
    		
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
		// TODO Auto-generated method stub
		
	}

}
