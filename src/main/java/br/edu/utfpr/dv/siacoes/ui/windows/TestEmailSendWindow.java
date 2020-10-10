package br.edu.utfpr.dv.siacoes.ui.windows;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.bo.EmailMessageBO;
import br.edu.utfpr.dv.siacoes.model.EmailMessage;

public class TestEmailSendWindow extends BasicWindow {
	
	private final TextField textTo;
	private final Button buttonSend;
	
	public TestEmailSendWindow(){
		super("Testar Envio de E-mails");
		
		this.textTo = new TextField("Destinatário");
		this.textTo.setWidth("400px");
		
		this.buttonSend = new Button("Enviar", new Icon(VaadinIcon.PAPERPLANE_O), event -> {
            sendEmail();
        });
		this.buttonSend.setWidth("150px");
		
		VerticalLayout vl = new VerticalLayout(this.textTo, this.buttonSend);
		vl.setSpacing(true);
		vl.setMargin(false);
		
		this.add(vl);
	}
	
	private void sendEmail(){
		EmailMessageBO bo = new EmailMessageBO();
		EmailMessage message = new EmailMessage();
		
		message.setSubject("Teste de Envio");
		message.setMessage("Teste de envio de e-mail.");
		
		try {
			bo.sendEmailNoThread(null, new String[] { this.textTo.getValue() }, message, null, false);
			
			this.showSuccessNotification("Enviar E-mail", "Envio realizado com sucesso.");
		} catch (Exception e) {
			e.printStackTrace();
			
			this.showErrorNotification("Enviar E-mail", e.getMessage());
		}
	}

}
