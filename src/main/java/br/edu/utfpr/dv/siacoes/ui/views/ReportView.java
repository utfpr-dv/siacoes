package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public abstract class ReportView extends LoggedView {
	
	private final VerticalLayout layoutFields;
	private final Button buttonReport;
    
    public ReportView(SystemModule module){
    	this.setProfilePerimissions(UserProfile.STUDENT);
    	
    	this.buttonReport = new Button("Gerar Relatório", new Icon(VaadinIcon.FILE_TEXT), event -> {
        	try{
        		this.showReport(generateReport());
        	}catch(Exception e){
        		Logger.log(Level.SEVERE, e.getMessage(), e);
        		
        		showErrorNotification("Gerar Relatório", e.getMessage());
        	}
        });
		this.buttonReport.setWidth("200px");
		this.buttonReport.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		this.layoutFields = new VerticalLayout();
		this.layoutFields.setSpacing(true);
		
		VerticalLayout layout = new VerticalLayout(this.layoutFields, this.buttonReport);
		layout.setSpacing(false);
		layout.setMargin(true);
		layout.setSizeFull();
		layout.expand(this.layoutFields);
    	
    	this.setModule(module);
    	
    	this.setViewContent(layout);
    }
    
    public void addFilterField(Component c){
    	if(c instanceof HorizontalLayout){
			((HorizontalLayout)c).setSpacing(true);
			((HorizontalLayout)c).setMargin(false);
			((HorizontalLayout)c).setPadding(false);
		}else if(c instanceof VerticalLayout){
			((VerticalLayout)c).setSpacing(false);
			((VerticalLayout)c).setMargin(false);
			((VerticalLayout)c).setPadding(false);
		}
    	
    	layoutFields.add(c);
    }
    
    public abstract byte[] generateReport() throws Exception;

}
