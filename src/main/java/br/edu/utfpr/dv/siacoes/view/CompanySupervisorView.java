package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditCompanySupervisorWindow;
import br.edu.utfpr.dv.siacoes.window.RegisterProfessorWindow;

public class CompanySupervisorView extends ListView {
	
	public static final String NAME = "companysupervisor";
	
	public final Button buttonRegisterProfessor;
	
	public CompanySupervisorView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Supervisores");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.buttonRegisterProfessor = new Button("Registrar Professor", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	registerProfessor();
            }
        });
		this.buttonRegisterProfessor.setIcon(FontAwesome.USER_PLUS);
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
		this.addActionButton(this.buttonRegisterProfessor);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addColumn("Telefone", String.class);
		this.getGrid().addColumn("E-mail", String.class);
		
		this.getGrid().getColumns().get(2).setWidth(150);
		
		try{
			UserBO bo = new UserBO();
			List<User> list = bo.listAllCompanySupervisors(false);
			
			for(User c : list){
				Object itemId = this.getGrid().addRow(c.getName(), c.getCompany().getName(), c.getPhone(), c.getEmail());
				this.addRowId(itemId, c.getIdUser());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Supervisores", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		User user = new User();
		user.setExternal(true);
		user.setProfiles(new ArrayList<UserProfile>());
		user.getProfiles().add(UserProfile.COMPANYSUPERVISOR);
		UI.getCurrent().addWindow(new EditCompanySupervisorWindow(user, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			UserBO bo = new UserBO();
			User user = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditCompanySupervisorWindow(user, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Supervisor", e.getMessage());
		}
	}
	
	private void registerProfessor() {
		UI.getCurrent().addWindow(new RegisterProfessorWindow(this));
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
