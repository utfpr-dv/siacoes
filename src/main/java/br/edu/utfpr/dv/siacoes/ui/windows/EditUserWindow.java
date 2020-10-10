package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.ui.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.UserDepartmentDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditUserWindow extends EditWindow {
	
	private final User user;
	
	private final TextField textLogin;
	private final TextField textName;
	private final TextField textEmail;
	private final Checkbox checkExternal;
	private final Checkbox checkActive;
	private final CompanyComboBox comboCompany;
	
	private final Tabs tab;
	private final VerticalLayout tabData;
	private final VerticalLayout tabProfile;
	private final VerticalLayout tabDepartment;
	
	private Grid<UserProfile> gridProfiles;
	private final Button buttonAddProfile;
	private final Button buttonEditProfile;
	private final Button buttonRemoveProfile;
	
	private Grid<UserDepartmentDataSource> gridDepartments;
	private final Button buttonAddDepartment;
	private final Button buttonEditDepartment;
	private final Button buttonRemoveDepartment;

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
		
		this.comboCompany = new CompanyComboBox();
		
		this.checkExternal = new Checkbox("Usuário externo");
		this.checkExternal.addValueChangeListener(event -> {
			configureExternal(checkExternal.getValue());
		});
		
		this.checkActive = new Checkbox("Ativo");
		
		this.tabData = new VerticalLayout();
		this.tabData.setSpacing(false);
		this.tabData.setMargin(false);
		this.tabData.setPadding(false);
		
		HorizontalLayout h = new HorizontalLayout(this.textLogin, new VerticalLayout(this.checkActive, this.checkExternal));
		h.setSpacing(true);
		h.setMargin(false);
		h.setPadding(false);
		this.tabData.add(h);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textName, this.textEmail);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		this.tabData.add(h1);
		
		this.tabData.add(this.comboCompany);
		
		this.buttonAddProfile = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addProfile();
        });
		this.buttonAddProfile.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddProfile.setWidth("150px");
		
		this.buttonEditProfile = new Button("Editar", new Icon(VaadinIcon.EDIT), event -> {
            editProfile();
        });
		this.buttonEditProfile.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.buttonEditProfile.setWidth("150px");
		
		this.buttonRemoveProfile = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeProfile();
        });
		this.buttonRemoveProfile.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveProfile.setWidth("150px");
		
		HorizontalLayout h2 = new HorizontalLayout(this.buttonAddProfile, this.buttonEditProfile, this.buttonRemoveProfile);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		this.gridProfiles = new Grid<UserProfile>();
    	this.gridProfiles.setSelectionMode(SelectionMode.SINGLE);
		this.gridProfiles.setSizeFull();
		this.gridProfiles.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridProfiles.addItemDoubleClickListener(event -> {
			if(this.buttonEditProfile.isVisible()){
				this.editProfile();
			}
		});
		this.gridProfiles.addColumn(UserProfile::toString).setHeader("Perfil");
		
		this.tabProfile = new VerticalLayout(this.gridProfiles, h2);
		this.tabProfile.setSpacing(true);
		this.tabProfile.setMargin(false);
		this.tabProfile.setPadding(false);
		this.tabProfile.setVisible(false);
		
		this.buttonAddDepartment = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addDepartment();
        });
		this.buttonAddDepartment.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddDepartment.setWidth("150px");
		
		this.buttonEditDepartment = new Button("Editar", new Icon(VaadinIcon.EDIT), event -> {
            editDepartment();
        });
		this.buttonEditDepartment.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		this.buttonEditDepartment.setWidth("150px");
		
		this.buttonRemoveDepartment = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeDepartment();
        });
		this.buttonRemoveDepartment.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveDepartment.setWidth("150px");
		
		HorizontalLayout h3 = new HorizontalLayout(this.buttonAddDepartment, this.buttonEditDepartment, this.buttonRemoveDepartment);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		
		this.gridDepartments = new Grid<UserDepartmentDataSource>();
    	this.gridDepartments.setSelectionMode(SelectionMode.SINGLE);
		this.gridDepartments.setSizeFull();
		this.gridDepartments.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridDepartments.addItemDoubleClickListener(event -> {
			if(this.buttonEditDepartment.isVisible()){
				this.editDepartment();
			}
		});
		this.gridDepartments.addColumn(UserDepartmentDataSource::getDepartment).setHeader("Departamento");
		this.gridDepartments.addColumn(UserDepartmentDataSource::getCampus).setHeader("Câmpus");
		this.gridDepartments.addColumn(UserDepartmentDataSource::getProfile).setHeader("Perfil");
		
		this.tabDepartment = new VerticalLayout(this.gridDepartments, h3);
		this.tabDepartment.setSpacing(true);
		this.tabDepartment.setMargin(false);
		this.tabDepartment.setPadding(false);
		this.tabDepartment.setVisible(false);
		
		Tab tab1 = new Tab("Dados Gerais");
		Tab tab2 = new Tab("Perfis");
		Tab tab3 = new Tab("Departamentos");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, this.tabData);
		tabsToPages.put(tab2, this.tabProfile);
		tabsToPages.put(tab3, this.tabDepartment);
		Div pages = new Div(this.tabData, this.tabProfile, this.tabDepartment);
		
		this.tab = new Tabs(tab1, tab2, tab3);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tab1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setMargin(false);
		layout.setPadding(false);
		layout.setSpacing(false);
		layout.setWidth("820px");
		layout.setHeight("370px");
		
		this.addField(layout);
		
		this.loadUser();
		this.textLogin.focus();
	}
	
	private void loadUser(){
		this.textName.setValue(this.user.getName());
		this.textEmail.setValue(this.user.getEmail());
		this.comboCompany.setCompany(this.user.getCompany());
		this.textLogin.setValue(this.user.getLogin());
		this.checkExternal.setValue(this.user.isExternal());
		this.checkActive.setValue(this.user.isActive());
		
		this.configureExternal(this.user.isExternal());
		
		this.loadGridProfiles();
		this.loadGridDepartments();
	}
	
	private void configureExternal(boolean external){
		this.textName.setEnabled(external);
	}
	
	@Override
	public void save() {
		try {
			UserBO bo = new UserBO();
			
			this.user.setEmail(this.textEmail.getValue());
			this.user.setCompany(this.comboCompany.getCompany());
			this.user.setLogin(this.textLogin.getValue());
			this.user.setExternal(this.checkExternal.getValue());
			this.user.setActive(this.checkActive.getValue());
			this.user.setName(this.textName.getValue());
			
			bo.save(Session.getIdUserLog(), this.user);
			
			this.showSuccessNotification("Salvar Usuário", "Usuário salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Usuário", e.getMessage());
		}
	}
	
	private void loadGridProfiles() {
		this.gridProfiles.setItems(this.user.getProfiles());
	}
	
	private void addProfile() {
		EditProfileWindow window = new EditProfileWindow(this.user, UserProfile.STUDENT, true, this);
		window.open();
	}
	
	private void editProfile() {
		UserProfile profile = this.gridProfiles.asSingleSelect().getValue();
		
		if(profile == null) {
			this.showWarningNotification("Editar Perfil", "Selecione o perfil para editar.");
		} else {
			EditProfileWindow window = new EditProfileWindow(this.user, profile, false, this);
			window.open();
		}
	}
	
	private void removeProfile() {
		UserProfile profile = this.gridProfiles.asSingleSelect().getValue();
		
		if(profile == null) {
			this.showWarningNotification("Remover Perfil", "Selecione o perfil para remover.");
		} else {
			try {
				ConfirmDialog.createQuestion()
	    			.withIcon(new Icon(VaadinIcon.TRASH))
	    	    	.withCaption("Confirma a Exclusão?")
	    	    	.withMessage("Confirma a remoção do perfil do usuário?")
	    	    	.withOkButton(() -> {
	    	    		deleteProfile(profile);
	    	    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
	    	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
	    	    	.open();
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Remover Perfil", e.getMessage());
			}
		}
	}
	
	private void deleteProfile(UserProfile profile) {
		for(int i = this.user.getProfiles().size() - 1; i >= 0; i--) {
			if(this.user.getProfiles().get(i) == profile) {
				this.user.getProfiles().remove(i);
				break;
			}
		}
		
    	loadGridProfiles();
	}
	
	public void saveProfile(User user, UserProfile profile, boolean add) {
		if(add) {
			this.user.getProfiles().add(profile);
		}
		
		if(profile == UserProfile.STUDENT) {
			this.user.setStudentCode(user.getStudentCode());
		} else if((profile == UserProfile.PROFESSOR) || (profile == UserProfile.SUPERVISOR)) {
			this.user.setLattes(user.getLattes());
			this.user.setArea(user.getArea());
			this.user.setResearch(user.getResearch());
			
			if(profile == UserProfile.SUPERVISOR) {
				this.user.setInstitution(user.getInstitution());
			}
		}
		
		this.loadGridProfiles();
	}
	
	private void loadGridDepartments() {
		if(this.user.getDepartments() == null) {
			try {
				this.user.setDepartments(new UserDepartmentBO().list(this.user.getIdUser()));
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Departamentos", e.getMessage());
			}
		}
		
		this.gridDepartments.setItems(UserDepartmentDataSource.load(this.user.getDepartments()));
	}
	
	private void addDepartment() {
		UserDepartment department = new UserDepartment();
		
		department.setUser(this.user);
		
		EditUserDepartmentWindow window = new EditUserDepartmentWindow(department, this);
		window.open();
	}
	
	private void editDepartment() {
		UserDepartmentDataSource dep = this.gridDepartments.asSingleSelect().getValue();
		
		if(dep == null) {
			this.showWarningNotification("Editar Departamento", "Selecione o departamento para editar.");
		} else {
			UserDepartment department = this.user.getDepartments().get(getDepartmentIndex(dep));
			
			EditUserDepartmentWindow window = new EditUserDepartmentWindow(department, this);
			window.open();
		}
	}
	
	private void removeDepartment() {
		UserDepartmentDataSource dep = this.gridDepartments.asSingleSelect().getValue();
		
		if(dep == null) {
			this.showWarningNotification("Remover Departamento", "Selecione o departamento para remover.");
		} else {
			try {
				ConfirmDialog.createQuestion()
	    			.withIcon(new Icon(VaadinIcon.TRASH))
	    	    	.withCaption("Confirma a Exclusão?")
	    	    	.withMessage("Confirma a remoção do departamento do perfil do usuário?")
	    	    	.withOkButton(() -> {
	    	    		deleteDepartment(getDepartmentIndex(dep));
	    	    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
	    	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
	    	    	.open();
			} catch(Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Remover Departamentos", e.getMessage());
			}
		}
	}
	
	private void deleteDepartment(int index) {
		this.user.getDepartments().remove(index);
    	loadGridDepartments();
	}
	
	private int getDepartmentIndex(UserDepartmentDataSource dep) {
		for(int i = 0; i < this.user.getDepartments().size(); i++) {
			if((this.user.getDepartments().get(i).getDepartment().getIdDepartment() == dep.getIdDepartment()) && (this.user.getDepartments().get(i).getProfile() == dep.getIdProfile())) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void saveDepartment(UserDepartment department) {
		int index = -1;
		
		for(int i = 0; i < this.user.getDepartments().size(); i++) {
			if((this.user.getDepartments().get(i).getDepartment().getIdDepartment() == department.getDepartment().getIdDepartment()) && (this.user.getDepartments().get(i).getProfile() == department.getProfile())) {
				index = i;
				break;
			}
		}
		
		if(index < 0) {
			this.user.getDepartments().add(department);
		} else {
			this.user.getDepartments().set(index, department);
		}
		
		this.loadGridDepartments();
	}
	
}
