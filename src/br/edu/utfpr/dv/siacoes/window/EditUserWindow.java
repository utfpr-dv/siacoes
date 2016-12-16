package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.StringUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditUserWindow extends EditWindow {
	
	private final User user;
	
	private final TextField textLogin;
	private final TextField textName;
	private final TextField textEmail;
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textInstitution;
	private final TextField textArea;
	private final TextArea textResearch;
	private final TextField textLattes;
	private final NativeSelect comboProfile;
	private final CheckBox checkExternal;
	private final CheckBox checkActive;
	private final CheckBox checkSigacManager;
	private final CheckBox checkSigesManager;
	private final CheckBox checkSigetManager;
	private final TextField textStudentCode;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;

	public EditUserWindow(User user, ListView parentView){
		super("Editar Usuário", parentView);
		
		if(user == null){
			this.user = new User();
		}else{
			this.user = user;
		}
		
		this.textLogin = new TextField("Login");
		this.textLogin.setWidth("400px");
		this.textLogin.setMaxLength(50);
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		
		this.textEmail = new TextField("E-mail");
		this.textEmail.setWidth("400px");
		this.textEmail.setMaxLength(100);
		
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
		
		this.textInstitution = new TextField("Instituição");
		this.textInstitution.setWidth("400px");
		this.textInstitution.setMaxLength(100);
		
		this.textArea = new TextField("Área/Subárea");
		this.textArea.setWidth("800px");
		this.textArea.setMaxLength(100);
		
		this.textResearch = new TextArea("Áreas de Pesquisa");
		this.textResearch.setWidth("800px");
		
		this.textLattes = new TextField("Link do Lattes");
		this.textLattes.setWidth("400px");
		this.textLattes.setMaxLength(100);
		
		this.comboProfile = new NativeSelect("Perfil");
		this.comboProfile.setWidth("400px");
		this.comboProfile.setNullSelectionAllowed(false);
		this.comboProfile.addItem(UserProfile.STUDENT);
		this.comboProfile.addItem(UserProfile.PROFESSOR);
		this.comboProfile.addItem(UserProfile.ADMINISTRATOR);
		this.comboProfile.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureStudent(comboProfile.getValue() == UserProfile.STUDENT);
			}
		});
		
		this.checkSigacManager = new CheckBox("Responsável por Atividades Complementares");
		
		this.checkSigesManager = new CheckBox("Responsável por Estágios");
		
		this.checkSigetManager = new CheckBox("Responsável por TCC");
		
		this.checkExternal = new CheckBox("Usuário externo");
		this.checkExternal.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureExternal(checkExternal.getValue());
			}
		});
		
		this.checkActive = new CheckBox("Ativo");
		
		this.textStudentCode = new TextField("R.A.");
		this.textStudentCode.setWidth("200px");
		this.textStudentCode.setMaxLength(45);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setCaption("Semestre de Ingresso");
		
		this.textYear = new YearField();
		this.textYear.setCaption("Ano de Ingresso");
		
		if(Session.isUserAdministrator()){
			this.addField(new HorizontalLayout(this.textLogin, new VerticalLayout(this.checkActive, this.checkExternal)));	
		}else{
			this.textName.setEnabled(false);
		}
		this.addField(new HorizontalLayout(this.textName, this.textEmail));
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		this.addField(new HorizontalLayout(this.textStudentCode, this.comboSemester, this.textYear));
		if(Session.isUserAdministrator()){
			this.addField(new HorizontalLayout(this.comboProfile, new VerticalLayout(this.checkSigacManager, this.checkSigesManager, this.checkSigetManager)));
		}
		if(Session.isUserProfessor()){
			this.addField(new HorizontalLayout(this.textInstitution, this.textLattes));
			this.addField(this.textArea);
			this.addField(this.textResearch);
		}
		
		this.loadUser();
		this.textLogin.focus();
	}
	
	private void configureStudent(boolean student){
		if(!student){
			this.textStudentCode.setVisible(false);
			this.comboSemester.setVisible(false);
			this.textYear.setVisible(false);
		}
	}
	
	private void loadUser(){
		try{
			if((this.user.getDepartment() != null) && (this.user.getDepartment().getIdDepartment() != 0)){
				CampusBO bo = new CampusBO();
				Campus campus = bo.findByDepartment(this.user.getDepartment().getIdDepartment());
				
				this.comboCampus.setCampus(campus);
				
				this.comboDepartment.setIdCampus(campus.getIdCampus());
				
				this.comboDepartment.setDepartment(this.user.getDepartment());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textName.setValue(this.user.getName());
		this.textEmail.setValue(this.user.getEmail());
		this.textInstitution.setValue(this.user.getInstitution());
		this.textStudentCode.setValue(this.user.getStudentCode());
		this.comboSemester.setSemester(this.user.getRegisterSemester());
		this.textYear.setYear(this.user.getRegisterYear());
		
		if(Session.isUserProfessor()){
			this.textArea.setValue(this.user.getArea());
			this.textResearch.setValue(this.user.getResearch());
			this.textLattes.setValue(this.user.getLattes());
		}
		
		if(Session.isUserAdministrator()){
			this.textLogin.setValue(this.user.getLogin());
			this.comboProfile.setValue(this.user.getProfile());
			this.checkExternal.setValue(this.user.isExternal());
			this.checkActive.setValue(this.user.isActive());
			this.checkSigacManager.setValue(this.user.isSigacManager());
			this.checkSigesManager.setValue(this.user.isSigesManager());
			this.checkSigetManager.setValue(this.user.isSigetManager());
		}
		
		this.configureExternal(this.user.isExternal());
		this.configureStudent(this.user.getProfile() == UserProfile.STUDENT);
	}
	
	private void configureExternal(boolean external){
		if(external){
			this.comboCampus.setEnabled(false);
			this.comboDepartment.setEnabled(false);
			this.textInstitution.setEnabled(true);
			this.textName.setEnabled(true);
			this.textStudentCode.setEnabled(true);
		}else{
			this.comboCampus.setEnabled(true);
			this.comboDepartment.setEnabled(true);
			this.textInstitution.setEnabled(false);
			this.textName.setEnabled(false);
			this.textStudentCode.setEnabled(false);
		}
	}
	
	@Override
	public void save() {
		try{
			UserBO bo = new UserBO();
			
			if(this.checkExternal.getValue()){
				this.user.setName(this.textName.getValue());
				this.user.setInstitution(this.textInstitution.getValue());
			}else{
				this.user.setDepartment(this.comboDepartment.getDepartment());	
			}
			
			this.user.setEmail(this.textEmail.getValue());
			this.user.setStudentCode(this.textStudentCode.getValue());
			this.user.setRegisterSemester(this.comboSemester.getSemester());
			this.user.setRegisterYear(this.textYear.getYear());
			
			if(Session.isUserProfessor()){
				this.user.setArea(this.textArea.getValue());
				this.user.setResearch(this.textResearch.getValue());
				this.user.setLattes(this.textLattes.getValue());
			}
			
			if(Session.isUserAdministrator()){
				this.user.setLogin(this.textLogin.getValue());
				this.user.setProfile((UserProfile)this.comboProfile.getValue());
				this.user.setExternal(this.checkExternal.getValue());
				this.user.setActive(this.checkActive.getValue());
				this.user.setSigacManager(this.checkSigacManager.getValue());
				this.user.setSigesManager(this.checkSigesManager.getValue());
				this.user.setSigetManager(this.checkSigetManager.getValue());
				
				if(this.user.getPassword().isEmpty()){
					this.user.setPassword(StringUtils.generateMD5Hash(this.user.getLogin()));
				}
			}
			
			bo.save(user);
			
			Notification.show("Salvar Usuário", "Usuário salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Usuário", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
