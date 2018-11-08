package br.edu.utfpr.dv.siacoes.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.components.Notification;
import br.edu.utfpr.dv.siacoes.model.Certificate;

import com.vaadin.ui.Button.ClickEvent;

public class AuthenticateView extends CustomComponent implements View {

	public static final String NAME = "authenticate";

	private final Panel panel;
	private final TextField textGuid;
	private final Button buttonAuthenticate;
	private final Label label;
	
	public AuthenticateView(){
		this.label = new Label("Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	this.label.setStyleName("Title");
    	
		this.panel = new Panel("Autenticação de Documentos");
		this.panel.setWidth("500px");
		
		this.textGuid = new TextField("Código de Autenticação");
		this.textGuid.setWidth("350px");
		this.textGuid.setMaxLength(36);
		
		this.buttonAuthenticate = new Button("Autenticar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	validate();
            }
        });
		this.buttonAuthenticate.setWidth("200px");
		
		VerticalLayout vl = new VerticalLayout(this.textGuid, this.buttonAuthenticate);
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setComponentAlignment(this.textGuid, Alignment.MIDDLE_CENTER);
		vl.setComponentAlignment(this.buttonAuthenticate, Alignment.MIDDLE_CENTER);
		
		this.panel.setContent(vl);
		
		VerticalLayout vl2 = new VerticalLayout(this.label, this.panel);
		vl2.setSpacing(true);
		vl2.setMargin(true);
		vl2.setComponentAlignment(this.label, Alignment.MIDDLE_CENTER);
		vl2.setComponentAlignment(this.panel, Alignment.MIDDLE_CENTER);
		
		this.setCompositionRoot(vl2);
		
		this.textGuid.focus();
	}
	
	private void validate(){
		try {
			CertificateBO bo = new CertificateBO();
			Certificate certificate = bo.findByGuid(this.textGuid.getValue().trim());
			
			if(certificate == null){
				Notification.showErrorNotification("Autenticar Documento", "Documento não encontrado. Verifique se o código de autenticação está correto.");
			}else{
				getUI().getPage().open("#!" + CertificateView.NAME + "/" + certificate.getGuid(), "");
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Autenticar Documento", e.getMessage());
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters() != null){
			String guid = event.getParameters();
			
			if(!guid.isEmpty()){
				this.textGuid.setValue(guid);
				
				this.validate();	
			}
		}
	}
	
}
