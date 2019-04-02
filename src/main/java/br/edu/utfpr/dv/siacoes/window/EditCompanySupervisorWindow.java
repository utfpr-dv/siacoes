package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditCompanySupervisorWindow extends EditWindow {

	private final User user;
	
	private final CompanyComboBox comboCompany;
	private final TextField textName;
	private final TextField textPhone;
	private final TextField textEmail;
	private final CheckBox checkActive;
	
	public EditCompanySupervisorWindow(User user, ListView parentView){
		super("Editar Supervisor", parentView);
		
		if(user == null){
			this.user = new User();
		}else{
			this.user = user;
		}
		
		this.comboCompany = new CompanyComboBox();
		this.comboCompany.setRequired(true);
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
		this.textPhone = new TextField("Telefone");
		this.textPhone.setWidth("400px");
		this.textPhone.setMaxLength(100);
		
		this.textEmail = new TextField("E-mail");
		this.textEmail.setWidth("400px");
		this.textEmail.setMaxLength(100);
		this.textEmail.setRequired(true);
		
		this.checkActive = new CheckBox("Ativo");
		
		this.addField(this.textName);
		this.addField(this.comboCompany);
		this.addField(this.textPhone);
		this.addField(this.textEmail);
		this.addField(this.checkActive);
		
		this.loadSupervisor();
		this.textName.focus();
	}
	
	private void loadSupervisor(){
		this.comboCompany.setCompany(this.user.getCompany());
		this.textName.setValue(this.user.getName());
		this.textEmail.setValue(this.user.getEmail());
		this.textPhone.setValue(this.user.getPhone());
		this.checkActive.setValue(this.user.isActive());
	}
	
	@Override
	public void save() {
		try{
			this.user.setCompany(this.comboCompany.getCompany());
			this.user.setName(this.textName.getValue());
			this.user.setEmail(this.textEmail.getValue());
			this.user.setPhone(this.textPhone.getValue());
			this.user.setActive(this.checkActive.getValue());
			
			if(this.user.getIdUser() == 0) {
				this.user.setLogin(this.user.getEmail());
			}
			
			if((this.user.getProfiles() != null) && (this.user.getProfiles().size() == 0)) {
				this.user.getProfiles().add(UserProfile.COMPANYSUPERVISOR);
			}
			
			new UserBO().save(Session.getIdUserLog(), this.user);
			
			this.showSuccessNotification("Salvar Supervisor", "Supervisor salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Supervisor", e.getMessage());
		}
	}

}
