package br.edu.utfpr.dv.siacoes.window;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
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
	private final TabSheet tab;
	
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
		
		this.textStudentCode = new TextField("R.A.");
		this.textStudentCode.setWidth("200px");
		this.textStudentCode.setMaxLength(45);
		this.textStudentCode.setEnabled(false);
		
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
		this.imagePhoto.setWidth("200px");
		this.imagePhoto.setHeight("200px");
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.setHeight("350px");
		this.tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textName, this.textEmail);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.textStudentCode, this.comboSemester, this.textYear);
		h3.setSpacing(true);
		
		VerticalLayout tabData = new VerticalLayout(h1, h2, h3);
		tabData.setSpacing(true);
		
		this.tab.addTab(tabData, "Informações Pessoais");
		
		HorizontalLayout layoutPhoto = new HorizontalLayout(this.imagePhoto, this.uploadPhoto);
		layoutPhoto.setSpacing(true);
		
		VerticalLayout tabCustomization = new VerticalLayout(layoutPhoto);
		tabCustomization.setSpacing(true);
		
		this.tab.addTab(tabCustomization, "Personalização");
		
		this.addField(this.tab);
		
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Informações", e.getMessage());
		}
	}
	
	private void loadPhoto() {
		if(this.user.getPhoto() != null) {
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
