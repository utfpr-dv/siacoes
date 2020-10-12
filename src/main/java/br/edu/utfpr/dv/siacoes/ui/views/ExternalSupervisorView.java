package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.UserDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditExternalSupervisorWindow;

@PageTitle("Orientadores Externos")
@Route(value = "externalsupervisor", layout = MainLayout.class)
public class ExternalSupervisorView extends ListView<UserDataSource> {
	
	public ExternalSupervisorView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(UserDataSource::getName, "Name").setHeader("Nome");
		this.getGrid().addColumn(UserDataSource::getEmail).setHeader("E-mail");
		this.getGrid().addColumn(UserDataSource::getInstitution, "Institution").setHeader("Instituição");
		this.getGrid().addColumn(UserDataSource::getResearchArea).setHeader("Áreas de Pesquisa");
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try {
			UserBO bo = new UserBO();
	    	List<User> list = bo.listAllSupervisors(true, true);
	    	
	    	this.getGrid().setItems(UserDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Orientadores", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		User user = new User();
		user.setExternal(true);
		user.setProfiles(new ArrayList<UserProfile>());
		user.getProfiles().add(UserProfile.SUPERVISOR);
		
		EditExternalSupervisorWindow window = new EditExternalSupervisorWindow(user, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			UserBO bo = new UserBO();
			User user = bo.findById((int)id);
			
			EditExternalSupervisorWindow window = new EditExternalSupervisorWindow(user, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Orientador", e.getMessage());
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
