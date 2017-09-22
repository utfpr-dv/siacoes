package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditCompanySupervisorWindow;

public class CompanySupervisorView extends ListView {
	
	public static final String NAME = "companysupervisor";
	
	public CompanySupervisorView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Supervisores");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addColumn("Telefone", String.class);
		this.getGrid().addColumn("E-mail", String.class);
		
		this.getGrid().getColumns().get(1).setWidth(100);
		
		try{
			UserBO bo = new UserBO();
			List<User> list = bo.listAllCompanySupervisors(false);
			
			for(User c : list){
				Object itemId = this.getGrid().addRow(c.getName(), c.getCompany().getName(), c.getPhone(), c.getEmail());
				this.addRowId(itemId, c.getIdUser());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Supervisores", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		User user = new User();
		user.setProfile(UserProfile.COMPANYSUPERVISOR);
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
			
			Notification.show("Editar Empresa", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
