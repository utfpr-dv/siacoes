package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.window.EditExternalSupervisorWindow;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class ExternalSupervisorView extends ListView {
	
	public static final String NAME = "externalsupervisor";
	
	public ExternalSupervisorView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setCaption("Orientadores Externos");
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("E-mail", String.class);
		this.getGrid().addColumn("Instituição", String.class);
		this.getGrid().addColumn("Áreas de Pesquisa", String.class);
		
		try {
			UserBO bo = new UserBO();
	    	List<User> list = bo.listAllSupervisors(true, true);
	    	
	    	for(User u : list){
				Object itemId = this.getGrid().addRow(u.getName(), u.getEmail(), u.getInstitution(), u.getResearch());
				this.addRowId(itemId, u.getIdUser());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Orientadores", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		User user = new User();
		user.setExternal(true);
		user.setProfiles(new ArrayList<UserProfile>());
		user.getProfiles().add(UserProfile.SUPERVISOR);
		
		UI.getCurrent().addWindow(new EditExternalSupervisorWindow(user, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			UserBO bo = new UserBO();
			User user = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditExternalSupervisorWindow(user, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Orientador", e.getMessage());
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
