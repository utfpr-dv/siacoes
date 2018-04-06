package br.edu.utfpr.dv.siacoes.window;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
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
	private final CheckBox checkExternal;
	private final CheckBox checkActive;
	private final CheckBox checkSigacManager;
	private final CheckBox checkSigesManager;
	private final CheckBox checkSigetManager;
	private final CheckBox checkDepartmentManager;
	private final TextField textStudentCode;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final FileUploader uploadPhoto;
	private final Image imagePhoto;
	private final CheckBox checkStudent;
	private final CheckBox checkProfessor;
	private final CheckBox checkCompanySupervisor;
	private final CheckBox checkSupervisor;
	private final CheckBox checkAdministrative;
	private final CheckBox checkAdministrator;
	private final CompanyComboBox comboCompany;
	
	private final TabSheet tab;
	private final VerticalLayout tabData;
	private final VerticalLayout tabProfessional;
	private final VerticalLayout tabCustomization;
	private final VerticalLayout tabProfile;
	private final VerticalLayout tabStudent;

	public EditUserWindow(User u, ListView parentView){
		super("Editar Usuário", parentView);
		
		if(u == null){
			this.user = new User();
		}else{
			this.user = u;
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
		
		this.comboCompany = new CompanyComboBox();
		
		this.checkSigacManager = new CheckBox("Responsável por Atividades Complementares");
		
		this.checkSigesManager = new CheckBox("Responsável por Estágios");
		
		this.checkSigetManager = new CheckBox("Responsável por TCC");
		
		this.checkDepartmentManager = new CheckBox("Responsável pelo Departamento");
		
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
		
		this.uploadPhoto = new FileUploader("Enviar Foto");
		this.uploadPhoto.getAcceptedDocumentTypes().add(DocumentType.JPEG);
		this.uploadPhoto.getAcceptedDocumentTypes().add(DocumentType.PNG);
		this.uploadPhoto.setMaxBytesLength(300 * 1024);
		this.uploadPhoto.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadPhoto.getFileUploadListener() != null) {
					user.setPhoto(uploadPhoto.getUploadedFile());
				}
				
				loadPhoto();
			}
		});
		
		this.imagePhoto = new Image();
		this.imagePhoto.setStyleName("ImagePhoto");
		this.imagePhoto.setSizeUndefined();
		
		this.tabData = new VerticalLayout();
		this.tabData.setSpacing(true);
		
		if(Session.isUserAdministrator()){
			HorizontalLayout h = new HorizontalLayout(this.textLogin, new VerticalLayout(this.checkActive, this.checkExternal));
			h.setSpacing(true);
			this.tabData.addComponent(h);
		}else{
			this.textName.setEnabled(false);
		}
		HorizontalLayout h1 = new HorizontalLayout(this.textName, this.textEmail);
		h1.setSpacing(true);
		this.tabData.addComponent(h1);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h2.setSpacing(true);
		this.tabData.addComponent(h2);
		
		if(Session.isUserAdministrator()){
			this.tabData.addComponent(this.checkDepartmentManager);
			this.tabData.addComponent(this.checkSigacManager);
			this.tabData.addComponent(this.checkSigesManager);
			this.tabData.addComponent(this.checkSigetManager);
		}
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.setHeight("350px");
		this.tab.addTab(this.tabData, "Dados Gerais");
		
		this.checkStudent = new CheckBox("Acadêmico");
		this.checkStudent.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureProfile();
			}
		});
		this.checkProfessor = new CheckBox("Professor");
		this.checkProfessor.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(checkProfessor.getValue()) {
					checkSupervisor.setValue(true);
				}
				configureProfile();
			}
		});
		this.checkSupervisor = new CheckBox("Orientador");
		this.checkSupervisor.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureProfile();
			}
		});
		this.checkCompanySupervisor = new CheckBox("Supervisor de Empresa");
		this.checkCompanySupervisor.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureProfile();
			}
		});
		this.checkAdministrative = new CheckBox("Técnico Administrativo");
		this.checkAdministrator = new CheckBox("Administrador");
		
		this.tabProfile = new VerticalLayout(this.checkStudent, this.checkProfessor, this.checkSupervisor, this.checkCompanySupervisor, this.checkAdministrative, this.checkAdministrator);
		this.tabProfile.setSpacing(true);
		
		this.tab.addTab(this.tabProfile, "Perfil");
		if(!Session.isUserAdministrator()){
			this.tab.getTab(1).setVisible(false);
		}
		
		this.tabStudent = new VerticalLayout();
		this.tabStudent.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.textStudentCode, this.comboSemester, this.textYear);
		h3.setSpacing(true);
		this.tabStudent.addComponent(h3);
		
		this.tab.addTab(this.tabStudent, "Acadêmico");
				
		this.tabProfessional = new VerticalLayout();
		this.tabProfessional.setSpacing(true);
		
		HorizontalLayout h = new HorizontalLayout(this.textInstitution, this.textLattes);
		h.setSpacing(true);
		this.tabProfessional.addComponent(h);
		
		this.tabProfessional.addComponent(this.textArea);
		this.tabProfessional.addComponent(this.textResearch);
		this.tabProfessional.addComponent(this.comboCompany);
		
		this.tab.addTab(this.tabProfessional, "Profissional");
		
		HorizontalLayout layoutPhoto = new HorizontalLayout(this.imagePhoto, this.uploadPhoto);
		layoutPhoto.setSpacing(true);
		
		this.tabCustomization = new VerticalLayout(layoutPhoto);
		this.tabCustomization.setSpacing(true);
		
		this.tab.addTab(this.tabCustomization, "Personalização");
		
		this.addField(this.tab);
		
		this.loadUser();
		this.textLogin.focus();
	}
	
	private void configureProfile(){
		this.tab.getTab(2).setVisible(this.checkStudent.getValue());
		
		this.tab.getTab(3).setVisible(this.checkProfessor.getValue() || this.checkSupervisor.getValue() || this.checkCompanySupervisor.getValue());
		
		this.checkDepartmentManager.setVisible(this.checkProfessor.getValue());
		this.checkSigacManager.setVisible(this.checkProfessor.getValue());
		this.checkSigesManager.setVisible(this.checkProfessor.getValue());
		this.checkSigetManager.setVisible(this.checkProfessor.getValue());
		
		this.comboCompany.setVisible(this.checkCompanySupervisor.getValue());
		
		this.textInstitution.setVisible(this.checkProfessor.getValue() || this.checkSupervisor.getValue());
		this.textLattes.setVisible(this.checkProfessor.getValue() || this.checkSupervisor.getValue());
		this.textArea.setVisible(this.checkProfessor.getValue() || this.checkSupervisor.getValue());
		this.textResearch.setVisible(this.checkProfessor.getValue() || this.checkSupervisor.getValue());
		
		if(!this.checkProfessor.getValue()){
			this.checkDepartmentManager.setValue(false);
			this.checkSigacManager.setValue(false);
			this.checkSigesManager.setValue(false);
			this.checkSigetManager.setValue(false);
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
		this.comboCompany.setCompany(this.user.getCompany());
		
		if(Session.isUserProfessor()){
			this.textArea.setValue(this.user.getArea());
			this.textResearch.setValue(this.user.getResearch());
			this.textLattes.setValue(this.user.getLattes());
		}
		
		if(Session.isUserAdministrator()){
			this.textLogin.setValue(this.user.getLogin());
			this.checkExternal.setValue(this.user.isExternal());
			this.checkActive.setValue(this.user.isActive());
			this.checkSigacManager.setValue(this.user.isSigacManager());
			this.checkSigesManager.setValue(this.user.isSigesManager());
			this.checkSigetManager.setValue(this.user.isSigetManager());
			this.checkDepartmentManager.setValue(this.user.isDepartmentManager());
		}
		
		this.checkStudent.setValue(this.user.hasProfile(UserProfile.STUDENT));
		this.checkProfessor.setValue(this.user.hasProfile(UserProfile.PROFESSOR));
		this.checkSupervisor.setValue(this.user.hasProfile(UserProfile.SUPERVISOR) || this.user.hasProfile(UserProfile.PROFESSOR));
		this.checkCompanySupervisor.setValue(this.user.hasProfile(UserProfile.COMPANYSUPERVISOR));
		this.checkAdministrative.setValue(this.user.hasProfile(UserProfile.ADMINISTRATIVE));
		this.checkAdministrator.setValue(this.user.hasProfile(UserProfile.ADMINISTRATOR));
		
		this.configureExternal(this.user.isExternal());
		this.configureProfile();
		
		this.loadPhoto();
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
			
			if(this.uploadPhoto.getFileUploadListener() != null) {
				this.user.setPhoto(this.uploadPhoto.getUploadedFile());
			}
			
			this.user.setEmail(this.textEmail.getValue());
			this.user.setStudentCode(this.textStudentCode.getValue());
			this.user.setRegisterSemester(this.comboSemester.getSemester());
			this.user.setRegisterYear(this.textYear.getYear());
			this.user.setCompany(this.comboCompany.getCompany());
			
			if(Session.isUserProfessor()){
				this.user.setInstitution(this.textInstitution.getValue());
				this.user.setArea(this.textArea.getValue());
				this.user.setResearch(this.textResearch.getValue());
				this.user.setLattes(this.textLattes.getValue());
			}
			
			if(Session.isUserAdministrator()){
				this.user.setLogin(this.textLogin.getValue());
				this.user.setExternal(this.checkExternal.getValue());
				this.user.setActive(this.checkActive.getValue());
				this.user.setSigacManager(this.checkSigacManager.getValue());
				this.user.setSigesManager(this.checkSigesManager.getValue());
				this.user.setSigetManager(this.checkSigetManager.getValue());
				this.user.setDepartmentManager(this.checkDepartmentManager.getValue());
				
				if(this.user.getPassword().isEmpty()){
					this.user.setPassword(StringUtils.generateSHA3Hash(this.user.getLogin()));
				}
				
				this.user.setProfiles(new ArrayList<UserProfile>());
				if(this.checkStudent.getValue()) this.user.getProfiles().add(UserProfile.STUDENT);
				if(this.checkProfessor.getValue()) this.user.getProfiles().add(UserProfile.PROFESSOR);
				if(this.checkSupervisor.getValue() && !this.checkProfessor.getValue()) this.user.getProfiles().add(UserProfile.SUPERVISOR);
				if(this.checkCompanySupervisor.getValue()) this.user.getProfiles().add(UserProfile.COMPANYSUPERVISOR);
				if(this.checkAdministrative.getValue()) this.user.getProfiles().add(UserProfile.ADMINISTRATIVE);
				if(this.checkAdministrator.getValue()) this.user.getProfiles().add(UserProfile.ADMINISTRATOR);
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
	
	private void loadPhoto(){
		if(this.user.getPhoto() != null){
			StreamResource resource = new StreamResource(
	            new StreamResource.StreamSource() {
	                @Override
	                public InputStream getStream() {
	                    return new ByteArrayInputStream(user.getPhoto());
	                }
	            }, "userphoto" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(DateUtils.getNow().getTime()) + ".jpg");
	
			resource.setCacheTime(0);
		    this.imagePhoto.setSource(resource);
		}
	}
	
}
