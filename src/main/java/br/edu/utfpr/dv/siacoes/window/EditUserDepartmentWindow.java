package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;

public class EditUserDepartmentWindow extends EditWindow {
	
	private final UserDepartment department;
	private final EditUserWindow parentWindow;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final NativeSelect comboProfile;
	private final CheckBox checkSigacManager;
	private final CheckBox checkSigesManager;
	private final CheckBox checkSigetManager;
	private final CheckBox checkDepartmentManager;

	public EditUserDepartmentWindow(UserDepartment department, EditUserWindow parentWindow) {
		super("Editar Perfil", null);
		
		this.parentWindow = parentWindow;
		this.department = department;
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setNullSelectionAllowed(true);
		this.comboCampus.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(comboCampus.getCampus() != null){
					comboDepartment.setIdCampus(comboCampus.getCampus().getIdCampus());
				}else{
					comboDepartment.setIdCampus(0);
				}
			}
		});
		
		if(this.comboCampus.getCampus() != null){
			this.comboDepartment = new DepartmentComboBox(this.comboCampus.getCampus().getIdCampus());
		}else{
			this.comboDepartment = new DepartmentComboBox(0);
		}
		this.comboDepartment.setNullSelectionAllowed(true);
		
		this.comboProfile = new NativeSelect("Perfil");
		this.comboProfile.setWidth("200px");
		this.comboProfile.setNullSelectionAllowed(false);
		this.comboProfile.addItem(UserProfile.STUDENT);
		this.comboProfile.addItem(UserProfile.PROFESSOR);
		this.comboProfile.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureProfile();
			}
		});
		
		this.checkSigacManager = new CheckBox("Responsável por Atividades Complementares");
		
		this.checkSigesManager = new CheckBox("Responsável por Estágios");
		
		this.checkSigetManager = new CheckBox("Responsável por TCC");
		
		this.checkDepartmentManager = new CheckBox("Responsável pelo Departamento");
		
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		
		VerticalLayout v1 = new VerticalLayout(this.checkSigacManager, this.checkSigesManager, this.checkSigetManager, this.checkDepartmentManager);
		v1.setSpacing(true);
		
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Perfil", e.getMessage());
		}
	}

}
