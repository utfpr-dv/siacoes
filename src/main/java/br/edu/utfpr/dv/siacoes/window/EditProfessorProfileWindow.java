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
import com.vaadin.ui.TextArea;
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
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditProfessorProfileWindow extends EditWindow {
	
	private final User user;
	private final UserDepartment profile;

	private final TextField textName;
	private final TextField textEmail;
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textArea;
	private final TextArea textResearch;
	private final TextField textLattes;
	private final FileUploader uploadPhoto;
	private final Image imagePhoto;
	private final TabSheet tab;
	
	public EditProfessorProfileWindow(User user, UserDepartment profile) {
		super("Completar Perfil", null);
		
		this.user = user;
		if(profile == null) {
			this.profile = new UserDepartment();
			this.profile.setProfile(UserProfile.PROFESSOR);
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
		
		this.textArea = new TextField("Área/Subárea");
		this.textArea.setWidth("800px");
		this.textArea.setMaxLength(100);
		
		this.textResearch = new TextArea("Áreas de Pesquisa");
		this.textResearch.setWidth("800px");
		
		this.textLattes = new TextField("Link do Lattes");
		this.textLattes.setWidth("400px");
		this.textLattes.setMaxLength(100);
		
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
		
		VerticalLayout tabData = new VerticalLayout(h1, h2);
		tabData.setSpacing(true);
		
		this.tab.addTab(tabData, "Informações Pessoais");
		
		VerticalLayout tabProfessional = new VerticalLayout(this.textLattes, this.textArea, this.textResearch);
		tabProfessional.setSpacing(true);
		
		this.tab.addTab(tabProfessional, "Profissional");
		
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
		this.textLattes.setValue(this.user.getLattes());
		this.textArea.setValue(this.user.getArea());
		this.textResearch.setValue(this.user.getResearch());
		this.comboCampus.setCampus(this.profile.getDepartment().getCampus());
		this.comboDepartment.setDepartment(this.profile.getDepartment());
		
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
			this.user.setLattes(this.textLattes.getValue());
			this.user.setArea(this.textArea.getValue());
			this.user.setResearch(this.textResearch.getValue());
			
			new UserBO().save(Session.getIdUserLog(), this.user);
			
			this.profile.setUser(this.user);
			this.profile.setDepartment(this.comboDepartment.getDepartment());
			
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
