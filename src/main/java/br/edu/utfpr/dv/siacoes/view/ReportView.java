package br.edu.utfpr.dv.siacoes.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public abstract class ReportView extends BasicView {
	
	private final VerticalLayout layoutFields;
	private final Button buttonReport;
    
    public ReportView(SystemModule module){
    	this.setProfilePerimissions(UserProfile.STUDENT);
    	
    	this.buttonReport = new Button("Gerar Relatório", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	try{
            		showReport(generateReport());
            	}catch(Exception e){
            		Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            		
            		showErrorNotification("Gerar Relatório", e.getMessage());
            	}
            }
        });
		this.buttonReport.setWidth("150px");
		this.buttonReport.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.buttonReport.setIcon(FontAwesome.FILE_PDF_O);
		
		this.layoutFields = new VerticalLayout();
		this.layoutFields.setSpacing(true);
		
		VerticalLayout layout = new VerticalLayout(this.layoutFields, this.buttonReport);
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setSizeFull();
		layout.setExpandRatio(this.layoutFields, 1);
    	
    	this.setModule(module);
    	
    	this.setContent(layout);
    }
    
    public void addFilterField(Component c){
    	if(c instanceof HorizontalLayout){
			((HorizontalLayout)c).setSpacing(true);
		}else if(c instanceof VerticalLayout){
			((VerticalLayout)c).setSpacing(true);
		}
    	
    	layoutFields.addComponent(c);
    }
    
    public abstract byte[] generateReport() throws Exception;
    
    @Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
