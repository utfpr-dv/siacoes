package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Certificate;

@PageTitle("Autenticar Declaração")
@Route(value = "authenticate")
public class AuthenticateView extends ViewFrame implements HasUrlParameter<String> {

	public static final String NAME = "authenticate";

	private final Details panel;
	private final TextField textGuid;
	private final Button buttonAuthenticate;
	private final H1 label;
	
	public AuthenticateView(){
		this.label = new H1("Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	
		this.panel = new Details();
		this.panel.setSummaryText("Autenticação de Documentos");
		this.panel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		this.panel.setOpened(true);
		this.panel.getElement().getStyle().set("width", "500px");
		
		this.textGuid = new TextField("Código de Autenticação");
		this.textGuid.setWidth("350px");
		this.textGuid.setMaxLength(36);
		
		this.buttonAuthenticate = new Button("Autenticar", event -> {
            validate();
        });
		this.buttonAuthenticate.setWidth("200px");
		this.buttonAuthenticate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		VerticalLayout vl = new VerticalLayout(this.textGuid, this.buttonAuthenticate);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setMargin(false);
		vl.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		
		this.panel.setContent(vl);
		
		VerticalLayout vl2 = new VerticalLayout(this.label, this.panel);
		vl2.setSpacing(false);
		vl2.setMargin(false);
		vl2.setPadding(false);
		vl2.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		
		this.setViewContent(vl2);
		
		this.textGuid.focus();
	}
	
	private void validate(){
		try {
			CertificateBO bo = new CertificateBO();
			Certificate certificate = bo.findByGuid(this.textGuid.getValue().trim());
			
			if(certificate == null){
				this.showErrorNotification("Autenticar Documento", "Documento não encontrado. Verifique se o código de autenticação está correto.");
			}else{
				this.getUI().ifPresent(ui -> ui.navigate(CertificateView.class, certificate.getGuid()));
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Autenticar Documento", e.getMessage());
		}
	}
	
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if((parameter != null) && (!parameter.trim().isEmpty())){
			this.textGuid.setValue(parameter);
			
			this.validate();
		}
	}
	
}
