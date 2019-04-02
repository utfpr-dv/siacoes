package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class RegisterProfessorWindow extends EditWindow {

	private final CompanyComboBox comboCompany;
	private final SupervisorComboBox comboProfessor;
	
	public RegisterProfessorWindow(ListView parentView) {
		super("Registrar Professor", parentView);
		
		this.comboCompany = new CompanyComboBox();
		this.comboCompany.setRequired(true);
		
		this.comboProfessor = new SupervisorComboBox("Professor", 0, SupervisorFilter.INSTITUTION);
		this.comboProfessor.setRequired(true);
		
		this.addField(this.comboProfessor);
		this.addField(this.comboCompany);
	}
	
	@Override
	public void save() {
		try{
			if((this.comboProfessor.getProfessor() == null) || (this.comboProfessor.getProfessor().getIdUser() == 0)) {
				throw new Exception("Informe o professor.");
			}
			if((this.comboCompany.getCompany() == null) || (this.comboCompany.getCompany().getIdCompany() == 0)) {
				throw new Exception("Informe a empresa concedente de estágio.");
			}
			
			UserBO bo = new UserBO();
			User user = bo.findById(this.comboProfessor.getProfessor().getIdUser());
			
			if(user.hasProfile(UserProfile.COMPANYSUPERVISOR)) {
				throw new Exception("O professor já está cadastrado como supervisor de estágio.");
			}
			
			user.getProfiles().add(UserProfile.COMPANYSUPERVISOR);
			user.setCompany(this.comboCompany.getCompany());
			
			bo.save(Session.getIdUserLog(), user);
			
			this.showSuccessNotification("Registrar Professor", "Professor registrado com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Registrar Professor", e.getMessage());
		}
	}

}
