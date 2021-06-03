package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.ReminderMessageBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage;
import br.edu.utfpr.dv.siacoes.model.ReminderMessage.ReminderType;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.EmailMessageDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditEmailConfigWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditReminderMessageWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.TestEmailSendWindow;

@PageTitle("Lembretes via E-mail")
@Route(value = "remindermessage", layout = MainLayout.class)
public class ReminderMessageView extends ListView<EmailMessageDataSource> {

	private final Select<String> comboModule;
	private final Button buttonConfigurations;
	private final Button buttonTest;
	
	public ReminderMessageView(){
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.getGrid().addColumn(EmailMessageDataSource::getType, "Type").setHeader("Tipo");
		this.getGrid().addColumn(EmailMessageDataSource::getTitle, "Title").setHeader("Título");
		this.getGrid().addColumn(EmailMessageDataSource::getMessage).setHeader("Mensagem");
		
		this.comboModule = new Select<String>();
		this.comboModule.setLabel("Módulo");
		this.comboModule.setWidth("400px");
		this.comboModule.setItems(SystemModule.GENERAL.toString(), SystemModule.SIGAC.toString(), SystemModule.SIGES.toString(), SystemModule.SIGET.toString(), "Todos");
		this.comboModule.setValue("Todos");
		
    	this.setAddVisible(false);
    	this.setDeleteVisible(false);
    	
    	this.buttonConfigurations = new Button("Configurações", new Icon(VaadinIcon.COG_O), event -> {
    		EditEmailConfigWindow window = new EditEmailConfigWindow();
    		window.open();
        });
    	
    	this.buttonTest = new Button("Testar Envio", new Icon(VaadinIcon.PAPERPLANE_O), event -> {
    		TestEmailSendWindow window = new TestEmailSendWindow();
    		window.open();
        });
    	
    	this.addActionButton(this.buttonConfigurations);
    	this.addActionButton(this.buttonTest);
    	
    	this.addFilterField(this.comboModule);
	}

	@Override
	protected void loadGrid() {
		try {
			ReminderMessageBO bo = new ReminderMessageBO();
	    	List<ReminderMessage> list;
	    	
	    	if(!this.comboModule.getValue().equals("Todos")){
	    		list = bo.listByModule(SystemModule.valueOf(this.comboModule.getValue()));
	    	}else{
	    		list  = bo.listAll();
	    	}
	    	
	    	this.getGrid().setItems(EmailMessageDataSource.loadReminders(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Lembretes", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			ReminderMessageBO bo = new ReminderMessageBO();
			ReminderMessage message = bo.findByReminderType(ReminderType.valueOf((int)id));
			
			EditReminderMessageWindow window = new EditReminderMessageWindow(message, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Lembrete", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
