package br.edu.utfpr.dv.siacoes.ui.windows;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader.AcceptedDocumentType;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditStudentProfileWindow extends EditWindow {
	
	private final User user;
	private final UserDepartment profile;

	private final TextField textName;
	private final TextField textEmail;
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textStudentCode;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final FileUploader uploadPhoto;
	private final Image imagePhoto;
	private final Tabs tab;
	
	public EditStudentProfileWindow(User user, UserDepartment profile) {
		super("Completar Perfil", null);
		
		this.user = user;
		if(profile == null) {
			this.profile = new UserDepartment();
			this.profile.setProfile(UserProfile.STUDENT);
		} else {
			this.profile = profile;
		}
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setEnabled(false);
		
		this.textEmail = new TextField("E-mail");
		this.textEmail.setWidth("400px");
		this.textEmail.setMaxLength(100);
		this.textEmail.setEnabled(user.getIdUser() == Session.getUser().getIdUser());
		
		this.comboCampus = new CampusComboBox();
		
		if(this.comboCampus.getCampus() != null){
			this.comboDepartment = new DepartmentComboBox(this.comboCampus.getCampus().getIdCampus());
		}else{
			this.comboDepartment = new DepartmentComboBox(0);
		}
		
		this.comboCampus.addValueChangeListener(event -> {
			if(comboCampus.getCampus() != null){
				comboDepartment.setIdCampus(comboCampus.getCampus().getIdCampus());
			}else{
				comboDepartment.setIdCampus(0);
			}
		});
		
		this.textStudentCode = new TextField("R.A.");
		this.textStudentCode.setWidth("200px");
		this.textStudentCode.setMaxLength(45);
		this.textStudentCode.setEnabled(false);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setLabel("Semestre de Ingresso");
		
		this.textYear = new YearField();
		this.textYear.setLabel("Ano de Ingresso");
		
		this.uploadPhoto = new FileUploader("Enviar Foto");
		this.uploadPhoto.setAcceptedType(AcceptedDocumentType.IMAGE);
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
		this.imagePhoto.setWidth("200px");
		this.imagePhoto.setHeight("200px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.textName, this.textEmail);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.textStudentCode, this.comboSemester, this.textYear);
		h3.setSpacing(true);
		
		VerticalLayout tabData = new VerticalLayout(h1, h2, h3);
		tabData.setSpacing(false);
		tabData.setPadding(false);
		tabData.setMargin(false);
		
		Tab tab1 = new Tab("Informações Pessoais");
		
		HorizontalLayout layoutPhoto = new HorizontalLayout(this.imagePhoto, this.uploadPhoto);
		layoutPhoto.setSpacing(true);
		
		VerticalLayout tabCustomization = new VerticalLayout(layoutPhoto);
		tabCustomization.setSpacing(false);
		tabCustomization.setPadding(false);
		tabCustomization.setMargin(false);
		tabCustomization.setVisible(false);
		
		Tab tab2 = new Tab("Personalização");
		tab2.setVisible(user.getIdUser() == Session.getUser().getIdUser());
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, tabData);
		tabsToPages.put(tab2, tabCustomization);
		Div pages = new Div(tabData, tabCustomization);
		
		this.tab = new Tabs(tab1, tab2);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tab1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("820px");
		layout.setHeight("370px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		this.loadProfile();
	}
	
	private void loadProfile() {
		this.textName.setValue(this.user.getName());
		this.textEmail.setValue(this.user.getEmail());
		this.textStudentCode.setValue(this.user.getStudentCode());
		this.comboCampus.setCampus(this.profile.getDepartment().getCampus());
		this.comboDepartment.setDepartment(this.profile.getDepartment());
		this.comboSemester.setSemester(this.profile.getRegisterSemester());
		this.textYear.setYear(this.profile.getRegisterYear());
		
		if(this.profile.getIdUserDepartment() != 0) {
			this.comboCampus.setEnabled(false);
			this.comboDepartment.setEnabled(false);
		}
		
		this.loadPhoto();
	}
	
	@Override
	public void save() {
		try {
			this.user.setEmail(this.textEmail.getValue());
			
			new UserBO().save(Session.getIdUserLog(), this.user);
			
			this.profile.setDepartment(this.comboDepartment.getDepartment());
			this.profile.setRegisterSemester(this.comboSemester.getSemester());
			this.profile.setRegisterYear(this.textYear.getYear());
			this.profile.setUser(this.user);
			
			new UserDepartmentBO().save(Session.getIdUserLog(), this.profile);
			
			this.showSuccessNotification("Salvar Informações", "Informações salvas com sucesso.");
			
			this.close();
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Informações", e.getMessage());
		}
	}
	
	private void loadPhoto() {
		if(this.user.getPhoto() != null) {
			StreamResource resource = new StreamResource("userphoto" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(DateUtils.getNow().getTime()) + ".png", () -> new ByteArrayInputStream(this.user.getPhoto()));
			resource.setCacheTime(0);
			this.imagePhoto.setSrc(resource);
		}
	}

}
