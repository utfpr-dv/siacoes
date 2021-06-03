package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ReminderMessageBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditReminderMessageWindow extends EditWindow {
	
	private final ReminderMessage message;
	
	private final TextField labelMessageType;
	private final TextField textSubject;
	private final TextArea textMessage;
	private final TextField labelDataFields;
	
	public EditReminderMessageWindow(ReminderMessage message, ListView parentView){
		super("Editar Lembrete", parentView);
		
		this.message = message;
		
		this.labelMessageType = new TextField("Tipo da Mensagem");
		this.labelMessageType.setWidth("600px");
		
		this.textSubject = new TextField("Assunto");
		this.textSubject.setMaxLength(255);
		this.textSubject.setWidth("600px");
		
		this.textMessage = new TextArea("Mensagem");
		this.textMessage.setWidth("600px");
		
		this.labelDataFields = new TextField("Campos de Dados");
		this.labelDataFields.setWidth("600px");
		
		this.addField(this.labelMessageType);
		this.addField(this.textSubject);
		this.addField(this.textMessage);
		this.addField(this.labelDataFields);
		
		this.loadMessage();
		this.textSubject.focus();
	}
	
	private void loadMessage(){
		this.labelMessageType.setValue(this.message.getIdReminderMessage().toString());
		this.textSubject.setValue(this.message.getSubject());
		this.textMessage.setValue(this.message.getMessage());
		this.labelDataFields.setValue(this.message.getDataFields());
		
		this.labelMessageType.setReadOnly(true);
		this.labelDataFields.setReadOnly(true);
	}
	
	@Override
	public void save() {
		try{
			ReminderMessageBO bo = new ReminderMessageBO();
			
			this.message.setSubject(this.textSubject.getValue());
			this.message.setMessage(this.textMessage.getValue());
			
			bo.save(Session.getIdUserLog(), this.message);
			
			this.showSuccessNotification("Salvar Lembrete", "Mensagem salva com sucesso.");
			this.close();
			this.parentViewRefreshGrid();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Lembrete", e.getMessage());
		}
	}

}
