package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;

public class EditUserDepartmentWindow extends EditWindow {
	
	private final UserDepartment department;
	private final EditUserWindow parentWindow;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final Select<UserProfile> comboProfile;
	private final Checkbox checkSigacManager;
	private final Checkbox checkSigesManager;
	private final Checkbox checkSigetManager;
	private final Checkbox checkDepartmentManager;

	public EditUserDepartmentWindow(UserDepartment department, EditUserWindow parentWindow) {
		super("Editar Perfil", null);
		
		this.parentWindow = parentWindow;
		this.department = department;
		
		this.comboDepartment = new DepartmentComboBox(0);
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.addValueChangeListener(event -> {
			if(comboCampus.getCampus() != null){
				comboDepartment.setIdCampus(comboCampus.getCampus().getIdCampus());
			}else{
				comboDepartment.setIdCampus(0);
			}
		});
		
		this.comboProfile = new Select<UserProfile>();
		this.comboProfile.setLabel("Perfil");
		this.comboProfile.setWidth("200px");
		this.comboProfile.setItems(UserProfile.STUDENT, UserProfile.PROFESSOR);
		this.comboProfile.addValueChangeListener(event -> {
			configureProfile();
		});
		
		this.checkSigacManager = new Checkbox("Responsável por Atividades Complementares");
		
		this.checkSigesManager = new Checkbox("Responsável por Estágios");
		
		this.checkSigetManager = new Checkbox("Responsável por TCC");
		
		this.checkDepartmentManager = new Checkbox("Responsável pelo Departamento");
		
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		
		VerticalLayout v1 = new VerticalLayout(this.checkSigacManager, this.checkSigesManager, this.checkSigetManager, this.checkDepartmentManager);
		v1.setSpacing(false);
		v1.setMargin(false);
		v1.setPadding(false);
		
		this.addField(new HorizontalLayout(this.comboProfile, v1));
		
		this.loadProfile();
	}
	
	private void loadProfile() {
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.department.getDepartment().getIdDepartment());
			
			if(campus != null){
				this.comboCampus.setCampus(campus);
				
				this.comboDepartment.setIdCampus(campus.getIdCampus());
				
				this.comboDepartment.setDepartment(this.department.getDepartment());
			}else{
				this.comboCampus.setCampus(Session.getSelectedDepartment().getDepartment().getCampus());
				
				this.comboDepartment.setIdCampus(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus());
				
				this.comboDepartment.setDepartment(Session.getSelectedDepartment().getDepartment());
			}
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.comboProfile.setValue(this.department.getProfile());
		this.checkSigacManager.setValue(this.department.isSigacManager());
		this.checkSigesManager.setValue(this.department.isSigesManager());
		this.checkSigetManager.setValue(this.department.isSigetManager());
		this.checkDepartmentManager.setValue(this.department.isDepartmentManager());
		
		if(this.department.getIdUserDepartment() != 0) {
			this.comboCampus.setEnabled(false);
			this.comboDepartment.setEnabled(false);
		}
		
		this.configureProfile();
	}
	
	private void configureProfile() {
		this.checkSigacManager.setVisible(this.comboProfile.getValue() == UserProfile.PROFESSOR);
		this.checkSigesManager.setVisible(this.comboProfile.getValue() == UserProfile.PROFESSOR);
		this.checkSigetManager.setVisible(this.comboProfile.getValue() == UserProfile.PROFESSOR);
		this.checkDepartmentManager.setVisible(this.comboProfile.getValue() == UserProfile.PROFESSOR);
	}
	
	@Override
	public void save() {
		try {
			this.department.setProfile((UserProfile)this.comboProfile.getValue());
			this.department.setDepartment(this.comboDepartment.getDepartment());
			this.department.setSigacManager(this.checkSigacManager.getValue());
			this.department.setSigesManager(this.checkSigesManager.getValue());
			this.department.setSigetManager(this.checkSigetManager.getValue());
			this.department.setDepartmentManager(this.checkDepartmentManager.getValue());
			
			this.parentWindow.saveDepartment(this.department);
			
			this.showSuccessNotification("Salvar Perfil", "Perfil salvo com sucesso.");
			
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Perfil", e.getMessage());
		}
	}

}
