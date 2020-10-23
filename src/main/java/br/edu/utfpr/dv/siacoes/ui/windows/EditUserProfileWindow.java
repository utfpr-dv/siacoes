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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader.AcceptedDocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditUserProfileWindow extends EditWindow {

	private final User user;
	private final UserProfile profile;
	
	private final TextField textName;
	private final TextField textEmail;
	private final TextField textInstitution;
	private final TextField textArea;
	private final TextArea textResearch;
	private final TextField textLattes;
	private final FileUploader uploadPhoto;
	private final Image imagePhoto;
	private final Tabs tab;
	private final Tab tab2;
	
	public EditUserProfileWindow(User user, UserProfile profile) {
		super("Completar Perfil", null);
		
		this.user = user;
		this.profile = profile;
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("800px");
		this.textName.setMaxLength(100);
		
		this.textEmail = new TextField("E-mail");
		this.textEmail.setWidth("800px");
		this.textEmail.setMaxLength(100);
		
		this.textInstitution = new TextField("Instituição");
		this.textInstitution.setWidth("800px");
		this.textInstitution.setMaxLength(100);
		
		this.textArea = new TextField("Área/Subárea");
		this.textArea.setWidth("800px");
		this.textArea.setMaxLength(100);
		
		this.textResearch = new TextArea("Áreas de Pesquisa");
		this.textResearch.setWidth("800px");
		
		this.textLattes = new TextField("Link do Lattes");
		this.textLattes.setWidth("400px");
		this.textLattes.setMaxLength(100);
		
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
		
		VerticalLayout tabData = new VerticalLayout(this.textName, this.textEmail);
		tabData.setSpacing(false);
		tabData.setPadding(false);
		tabData.setMargin(false);
		
		Tab tab1 = new Tab("Informações Pessoais");
		
		VerticalLayout tabProfessional = new VerticalLayout(this.textInstitution, this.textLattes, this.textArea, this.textResearch);
		tabProfessional.setSpacing(false);
		tabProfessional.setPadding(false);
		tabProfessional.setMargin(false);
		tabProfessional.setVisible(false);
		
		this.tab2 = new Tab("Profissional");
		
		HorizontalLayout layoutPhoto = new HorizontalLayout(this.imagePhoto, this.uploadPhoto);
		layoutPhoto.setSpacing(true);
		
		VerticalLayout tabCustomization = new VerticalLayout(layoutPhoto);
		tabCustomization.setSpacing(false);
		tabCustomization.setPadding(false);
		tabCustomization.setMargin(false);
		tabCustomization.setVisible(false);
		
		Tab tab3 = new Tab("Personalização");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, tabData);
		tabsToPages.put(this.tab2, tabProfessional);
		tabsToPages.put(tab3, tabCustomization);
		Div pages = new Div(tabData, tabProfessional, tabCustomization);
		
		this.tab = new Tabs(tab1, this.tab2, tab3);
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
		this.textInstitution.setValue(this.user.getInstitution());
		this.textLattes.setValue(this.user.getLattes());
		this.textArea.setValue(this.user.getArea());
		this.textResearch.setValue(this.user.getResearch());
		
		if(!this.user.isExternal()) {
			this.textName.setEnabled(false);
			this.textInstitution.setEnabled(false);
		}
		
		if(this.profile != UserProfile.SUPERVISOR) {
			this.tab2.setVisible(false);
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
			
			if(this.user.isExternal()) {
				this.user.setName(this.textName.getValue());
				this.user.setInstitution(this.textInstitution.getValue());
			}
			
			new UserBO().save(Session.getIdUserLog(), this.user);
			
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
