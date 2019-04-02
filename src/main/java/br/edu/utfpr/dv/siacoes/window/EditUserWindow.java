package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditUserWindow extends EditWindow {
	
	private final User user;
	
	private final TextField textLogin;
	private final TextField textName;
	private final TextField textEmail;
	private final CheckBox checkExternal;
	private final CheckBox checkActive;
	private final CompanyComboBox comboCompany;
	
	private final TabSheet tab;
	private final VerticalLayout tabData;
	private final VerticalLayout tabProfile;
	private final VerticalLayout tabDepartment;
	
	private Grid gridProfiles;
	private final HorizontalLayout layoutProfiles;
	private final Button buttonAddProfile;
	private final Button buttonEditProfile;
	private final Button buttonRemoveProfile;
	
	private Grid gridDepartments;
	private final HorizontalLayout layoutDepartments;
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
		
		this.checkExternal = new CheckBox("Usuário externo");
		this.checkExternal.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureExternal(checkExternal.getValue());
			}
		});
		
		this.checkActive = new CheckBox("Ativo");
		
		this.tabData = new VerticalLayout();
		this.tabData.setSpacing(true);
		
		HorizontalLayout h = new HorizontalLayout(this.textLogin, new VerticalLayout(this.checkActive, this.checkExternal));
		h.setSpacing(true);
		this.tabData.addComponent(h);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textName, this.textEmail);
		h1.setSpacing(true);
		this.tabData.addComponent(h1);
		
		this.tabData.addComponent(this.comboCompany);
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.setHeight("350px");
		this.tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		this.tab.addTab(this.tabData, "Dados Gerais");
		
		this.layoutProfiles = new HorizontalLayout();
		
		this.buttonAddProfile = new Button("Adicionar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addProfile();
            }
        });
		this.buttonAddProfile.setIcon(FontAwesome.PLUS);
		this.buttonAddProfile.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonAddProfile.setWidth("150px");
		
		this.buttonEditProfile = new Button("Editar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editProfile();
            }
        });
		this.buttonEditProfile.setIcon(FontAwesome.EDIT);
		this.buttonEditProfile.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.buttonEditProfile.setWidth("150px");
		
		this.buttonRemoveProfile = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeProfile();
            }
        });
		this.buttonRemoveProfile.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveProfile.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonRemoveProfile.setWidth("150px");
		
		HorizontalLayout h2 = new HorizontalLayout(this.buttonAddProfile, this.buttonEditProfile, this.buttonRemoveProfile);
		h2.setSpacing(true);
		
		this.tabProfile = new VerticalLayout(this.layoutProfiles, h2);
		this.tabProfile.setSpacing(true);
		
		this.tab.addTab(this.tabProfile, "Perfis");
		
		this.layoutDepartments = new HorizontalLayout();
		
		this.buttonAddDepartment = new Button("Adicionar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addDepartment();
            }
        });
		this.buttonAddDepartment.setIcon(FontAwesome.PLUS);
		this.buttonAddDepartment.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonAddDepartment.setWidth("150px");
		
		this.buttonEditDepartment = new Button("Editar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editDepartment();
            }
        });
		this.buttonEditDepartment.setIcon(FontAwesome.EDIT);
		this.buttonEditDepartment.addStyleName(ValoTheme.BUTTON_PRIMARY);
		this.buttonEditDepartment.setWidth("150px");
		
		this.buttonRemoveDepartment = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeDepartment();
            }
        });
		this.buttonRemoveDepartment.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveDepartment.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonRemoveDepartment.setWidth("150px");
		
		HorizontalLayout h3 = new HorizontalLayout(this.buttonAddDepartment, this.buttonEditDepartment, this.buttonRemoveDepartment);
		h3.setSpacing(true);
		
		this.tabDepartment = new VerticalLayout(this.layoutDepartments, h3);
		this.tabDepartment.setSpacing(true);
		
		this.tab.addTab(this.tabDepartment, "Departamentos");
		
		this.addField(this.tab);
		
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Usuário", e.getMessage());
		}
	}
	
	private void loadGridProfiles() {
		this.gridProfiles = new Grid();
		this.gridProfiles.addColumn("Perfil", String.class);
		this.gridProfiles.setWidth("810px");
		this.gridProfiles.setHeight("270px");
		
		for(UserProfile profile : this.user.getProfiles()) {
			this.gridProfiles.addRow(profile.toString());
		}
		
		this.layoutProfiles.removeAllComponents();
		this.layoutProfiles.addComponent(this.gridProfiles);
	}
	
	private void addProfile() {
		UI.getCurrent().addWindow(new EditProfileWindow(this.user, UserProfile.STUDENT, true, this));
	}
	
	private void editProfile() {
		int index = this.getProfileSelectedIndex();
		
		if(index == -1) {
			this.showWarningNotification("Editar Perfil", "Selecione o perfil para editar.");
		} else {
			UserProfile profile = this.user.getProfiles().get(index);
			
			UI.getCurrent().addWindow(new EditProfileWindow(this.user, profile, false, this));
		}
	}
	
	private void removeProfile() {
		int index = this.getProfileSelectedIndex();
		
		if(index == -1) {
			this.showWarningNotification("Remover Perfil", "Selecione o perfil para remover.");
		} else {
			try {
				ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do perfil do usuário?", new ConfirmDialog.Listener() {
	                public void onClose(ConfirmDialog dialog) {
	                    if (dialog.isConfirmed()) {
	                    	user.getProfiles().remove(index);
	                    	loadGridProfiles();
	                    }
	                }
	            });
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Remover Perfil", e.getMessage());
			}
		}
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
	
	private int getProfileSelectedIndex() {
    	Object itemId = this.gridProfiles.getSelectedRow();

    	if(itemId == null) {
    		return -1;
    	} else {
    		return ((int)itemId) - 1;	
    	}
    }
	
	private void loadGridDepartments() {
		this.gridDepartments = new Grid();
		this.gridDepartments.addColumn("Departamento", String.class);
		this.gridDepartments.addColumn("Câmpus", String.class);
		this.gridDepartments.addColumn("Perfil", String.class);
		this.gridDepartments.setWidth("810px");
		this.gridDepartments.setHeight("270px");
		
		if(this.user.getDepartments() == null) {
			try {
				this.user.setDepartments(new UserDepartmentBO().list(this.user.getIdUser()));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Departamentos", e.getMessage());
			}
		}
		
		for(UserDepartment department : this.user.getDepartments()) {
			this.gridDepartments.addRow(department.getDepartment().getName(), department.getDepartment().getCampus().getName(), department.getProfile().toString());
		}
		
		this.layoutDepartments.removeAllComponents();
		this.layoutDepartments.addComponent(this.gridDepartments);
	}
	
	private void addDepartment() {
		UserDepartment department = new UserDepartment();
		
		department.setUser(this.user);
		
		UI.getCurrent().addWindow(new EditUserDepartmentWindow(department, this));
	}
	
	private void editDepartment() {
		int index = this.getDepartmentSelectedIndex();
		
		if(index == -1) {
			this.showWarningNotification("Editar Departamento", "Selecione o departamento para editar.");
		} else {
			UserDepartment department = this.user.getDepartments().get(index);
			
			UI.getCurrent().addWindow(new EditUserDepartmentWindow(department, this));
		}
	}
	
	private void removeDepartment() {
		int index = this.getDepartmentSelectedIndex();
		
		if(index == -1) {
			this.showWarningNotification("Remover Departamento", "Selecione o departamento para remover.");
		} else {
			try {
				ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do departamento do perfil do usuário?", new ConfirmDialog.Listener() {
	                public void onClose(ConfirmDialog dialog) {
	                    if (dialog.isConfirmed()) {
	                    	user.getDepartments().remove(index);
	                    	loadGridDepartments();
	                    }
	                }
	            });
			} catch(Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Remover Departamentos", e.getMessage());
			}
		}
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
	
	private int getDepartmentSelectedIndex() {
    	Object itemId = this.gridDepartments.getSelectedRow();

    	if(itemId == null) {
    		return -1;
    	} else {
    		return ((int)itemId) - 1;	
    	}
    }
	
}
