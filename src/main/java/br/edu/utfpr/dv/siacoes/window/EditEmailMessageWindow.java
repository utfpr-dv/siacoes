package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.EmailMessageBO;
import br.edu.utfpr.dv.siacoes.model.EmailMessage;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditEmailMessageWindow extends EditWindow {

	private final EmailMessage message;
	
	private final Label labelMessageType;
	private final TextField textSubject;
	private final TextArea textMessage;
	private final Label labelDataFields;
	
	public EditEmailMessageWindow(EmailMessage message, ListView parentView){
		super("Editar Mensage", parentView);
		
		this.message = message;
		
		this.labelMessageType = new Label();
		this.labelMessageType.setCaption("Tipo da Mensagem");
		this.labelMessageType.setWidth("600px");
		
		this.textSubject = new TextField("Assunto");
		this.textSubject.setMaxLength(255);
		this.textSubject.setWidth("600px");
		
		this.textMessage = new TextArea("Mensagem");
		this.textMessage.setWidth("600px");
		
		this.labelDataFields = new Label();
		this.labelDataFields.setCaption("Campos de Dados");
		this.labelDataFields.setWidth("600px");
		
		this.addField(this.labelMessageType);
		this.addField(this.textSubject);
		this.addField(this.textMessage);
		this.addField(this.labelDataFields);
		
		this.loadMessage();
		this.textSubject.focus();
	}
	
	private void loadMessage(){
		this.labelMessageType.setValue(this.message.getIdEmailMessage().toString());
		this.textSubject.setValue(this.message.getSubject());
		this.textMessage.setValue(this.message.getMessage());
		this.labelDataFields.setValue(this.message.getDataFields());
	}
	
	@Override
	public void save() {
		try{
			EmailMessageBO bo = new EmailMessageBO();
			
			this.message.setSubject(this.textSubject.getValue());
			this.message.setMessage(this.textMessage.getValue());
			
			bo.save(Session.getIdUserLog(), this.message);
			
			this.showSuccessNotification("Salvar Mensagem", "Mensagem salva com sucesso.");
			this.close();
			this.parentViewRefreshGrid();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Mensagem", e.getMessage());
		}
	}

}
