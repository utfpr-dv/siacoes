package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.UserDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditUserWindow;

@PageTitle("Usuários")
@Route(value = "user", layout = MainLayout.class)
public class UserView extends ListView<UserDataSource> {
	
	private final Select<String> comboProfile;
	private final TextField textName;
	private final Checkbox checkActive;
	private final Checkbox checkExternal;
	private final Button buttonLoginAs;
	
	public UserView(){
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.getGrid().addColumn(UserDataSource::getLogin, "Login").setHeader("Login").setFlexGrow(0).setWidth("300px");
		this.getGrid().addColumn(UserDataSource::getName, "Name").setHeader("Nome");
		this.getGrid().addColumn(UserDataSource::getEmail).setHeader("E-mail");
		this.getGrid().addColumn(UserDataSource::getProfile).setHeader("Perfil");
		
		this.comboProfile = new Select<String>();
		this.comboProfile.setLabel("Perfil");
		this.comboProfile.setWidth("400px");
		this.comboProfile.setItems(UserProfile.STUDENT.toString(), UserProfile.PROFESSOR.toString(), UserProfile.SUPERVISOR.toString(), UserProfile.COMPANYSUPERVISOR.toString(), UserProfile.ADMINISTRATIVE.toString(), UserProfile.ADMINISTRATOR.toString(), "Todos");
		this.comboProfile.setValue("Todos");
		
		this.textName = new TextField("Nome:");
		this.textName.setWidth("400px");
		
		this.checkActive = new Checkbox("Somente usuários ativos");
		this.checkActive.setValue(true);
		
		this.checkExternal = new Checkbox("Somente usuários externos");
		
		this.buttonLoginAs = new Button("Logar como ...", new Icon(VaadinIcon.USER), event -> {
            loginAs();
        });
		
		VerticalLayout vl = new VerticalLayout(this.checkActive, this.checkExternal);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		
		this.addFilterField(new HorizontalLayout(this.textName, this.comboProfile, vl));
		
		this.addActionButton(this.buttonLoginAs);
		
		this.setDeleteVisible(false);
	}
	
	protected void loadGrid(){
		try {
			int profile = -1;
			
			if(!this.comboProfile.getValue().equals("Todos")){
				profile = UserProfile.valueOf(this.comboProfile.getValue()).getValue();
			}
			
			UserBO bo = new UserBO();
	    	List<User> list = bo.list(this.textName.getValue(), profile, this.checkActive.getValue(), this.checkExternal.getValue());
	    	
	    	this.getGrid().setItems(UserDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Usuários", e.getMessage());
		}
    }
	
	private void loginAs(){
		Object id = this.getIdSelected();
		
		if(id != null){
			try {
				UserBO bo = new UserBO();
				User user = bo.findById((int)id);
				
				Session.loginAs(user);
				
				MainLayout.reloadNaviItems();
				this.getUI().ifPresent(ui -> ui.navigate(Home.class));
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Logar como", e.getMessage());
			}
		}else{
			this.showWarningNotification("Logar como", "Selecione um usuário.");
		}
	}

	@Override
	public void addClick() {
		EditUserWindow window = new EditUserWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			UserBO bo = new UserBO();
			User user = bo.findById((int)id);
			
			EditUserWindow window = new EditUserWindow(user, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Usuário", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() {
		// TODO Auto-generated method stub
		
	}

}
