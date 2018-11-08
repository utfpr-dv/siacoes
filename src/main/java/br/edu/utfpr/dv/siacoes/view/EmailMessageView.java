package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeSelect;
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
import br.edu.utfpr.dv.siacoes.window.TestEmailSendWindow;

public class EmailMessageView extends ListView {
	
	public static final String NAME = "emailmessage";
	
	private final NativeSelect comboModule;
	private final Button buttonConfigurations;
	private final Button buttonTest;
	
	public EmailMessageView(){
		super(SystemModule.GENERAL);
		
		this.setCaption("Mensagens de E-mail");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.comboModule = new NativeSelect("Módulo");
		this.comboModule.setWidth("400px");
		this.comboModule.setNullSelectionAllowed(false);
		this.comboModule.addItem(SystemModule.GENERAL);
		this.comboModule.addItem(SystemModule.SIGAC);
		this.comboModule.addItem(SystemModule.SIGES);
		this.comboModule.addItem(SystemModule.SIGET);
		this.comboModule.addItem("Todos");
		this.comboModule.select("Todos");
		
    	this.setAddVisible(false);
    	this.setDeleteVisible(false);
    	
    	this.buttonConfigurations = new Button("Configurações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().addWindow(new EditEmailConfigWindow());
            }
        });
    	this.buttonConfigurations.setIcon(FontAwesome.GEAR);
    	
    	this.buttonTest = new Button("Testar Envio", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UI.getCurrent().addWindow(new TestEmailSendWindow());
            }
        });
    	this.buttonTest.setIcon(FontAwesome.SEND);
    	
    	this.addActionButton(this.buttonConfigurations);
    	this.addActionButton(this.buttonTest);
    	
    	this.addFilterField(this.comboModule);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Tipo", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Mensagem", String.class);
		
		try {
			EmailMessageBO bo = new EmailMessageBO();
	    	List<EmailMessage> list;
	    	
	    	if(!this.comboModule.getValue().equals("Todos")){
	    		list = bo.listByModule((SystemModule)this.comboModule.getValue());
	    	}else{
	    		list  = bo.listAll();
	    	}
	    	
	    	for(EmailMessage d : list){
				Object itemId = this.getGrid().addRow(d.getIdEmailMessage().toString(), d.getSubject(), d.getMessage());
				this.addRowId(itemId, d.getIdEmailMessage().getValue());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Mensagens", e.getMessage());
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
			
			this.showErrorNotification("Editar Mensagem", e.getMessage());
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
