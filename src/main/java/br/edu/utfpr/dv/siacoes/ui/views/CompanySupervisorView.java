package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.CompanySupervisorDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditCompanySupervisorWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.RegisterProfessorWindow;

@PageTitle("Supervisores")
@Route(value = "companysupervisor", layout = MainLayout.class)
public class CompanySupervisorView extends ListView<CompanySupervisorDataSource> {
	
	public final Button buttonRegisterProfessor;
	
	public CompanySupervisorView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(CompanySupervisorDataSource::getName, "Name").setHeader("Nome");
		this.getGrid().addColumn(CompanySupervisorDataSource::getCompany, "Company").setHeader("Empresa");
		this.getGrid().addColumn(CompanySupervisorDataSource::getPhone).setHeader("Telefone").setFlexGrow(0).setWidth("150px");
		this.getGrid().addColumn(CompanySupervisorDataSource::getEmail).setHeader("E-mail");
		
		this.buttonRegisterProfessor = new Button("Registrar Prof.", new Icon(VaadinIcon.USER_CARD), event -> {
            registerProfessor();
        });
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
		this.addActionButton(this.buttonRegisterProfessor);
	}

	@Override
	protected void loadGrid() {
		try{
			UserBO bo = new UserBO();
			List<User> list = bo.listAllCompanySupervisors(false);
			
			this.getGrid().setItems(CompanySupervisorDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Supervisores", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		User user = new User();
		user.setExternal(true);
		user.setProfiles(new ArrayList<UserProfile>());
		user.getProfiles().add(UserProfile.COMPANYSUPERVISOR);
		EditCompanySupervisorWindow window = new EditCompanySupervisorWindow(user, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			UserBO bo = new UserBO();
			User user = bo.findById((int)id);
			
			EditCompanySupervisorWindow window = new EditCompanySupervisorWindow(user, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Supervisor", e.getMessage());
		}
	}
	
	private void registerProfessor() {
		RegisterProfessorWindow window = new RegisterProfessorWindow(this);
		window.open();
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
