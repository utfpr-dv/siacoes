package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.bo.EmailMessageBO;
import br.edu.utfpr.dv.siacoes.model.EmailMessage;
import br.edu.utfpr.dv.siacoes.model.EmailMessage.MessageType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditEmailConfigWindow;
import br.edu.utfpr.dv.siacoes.window.EditEmailMessageWindow;

public class EmailMessageView extends ListView {
	
	public static final String NAME = "emailmessage";
	
	private final Button buttonConfigurations;
	
	public EmailMessageView(){
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
    	this.setFiltersVisible(false);
    	this.setAddVisible(false);
    	this.setDeleteVisible(false);
    	
    	this.buttonConfigurations = new Button("Configurações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().addWindow(new EditEmailConfigWindow());
            }
        });
    	
    	this.addActionButton(this.buttonConfigurations);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Tipo", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Mensagem", String.class);
		
		try {
			EmailMessageBO bo = new EmailMessageBO();
	    	List<EmailMessage> list = bo.listAll();
	    	
	    	for(EmailMessage d : list){
				Object itemId = this.getGrid().addRow(d.getIdEmailMessage().toString(), d.getSubject(), d.getMessage());
				this.addRowId(itemId, d.getIdEmailMessage().getValue());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Mensagens", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			EmailMessageBO bo = new EmailMessageBO();
			EmailMessage message = bo.findByMessageType(MessageType.valueOf((int)id));
			
			UI.getCurrent().addWindow(new EditEmailMessageWindow(message, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Documento", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
