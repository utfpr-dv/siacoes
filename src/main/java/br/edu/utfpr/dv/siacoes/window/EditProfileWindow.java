package br.edu.utfpr.dv.siacoes.window;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class EditProfileWindow extends EditWindow {
	
	private final User user;
	private UserProfile profile;
	private final boolean add;
	private final EditUserWindow parentWindow;
	
	private final NativeSelect comboProfile;
	private final TextField textStudentCode;
	private final TextField textInstitution;
	private final TextField textArea;
	private final TextArea textResearch;
	private final TextField textLattes;
	
	private final TabSheet tab;
	
	public EditProfileWindow(User user, UserProfile profile, boolean add, EditUserWindow parentWindow) {
		super("Editar Perfil", null);
		
		this.user = user;
		this.profile = profile;
		this.add = add;
		this.parentWindow = parentWindow;
		
		this.comboProfile = new NativeSelect("Perfil");
		this.comboProfile.setWidth("200px");
		this.comboProfile.setNullSelectionAllowed(false);
		if(this.user.isExternal()) {
			this.comboProfile.addItem(UserProfile.SUPERVISOR);
			this.comboProfile.addItem(UserProfile.COMPANYSUPERVISOR);
			this.comboProfile.addItem(UserProfile.ADMINISTRATOR);
		} else {
			this.comboProfile.addItem(UserProfile.STUDENT);
			this.comboProfile.addItem(UserProfile.PROFESSOR);
			this.comboProfile.addItem(UserProfile.COMPANYSUPERVISOR);
			this.comboProfile.addItem(UserProfile.ADMINISTRATIVE);
			this.comboProfile.addItem(UserProfile.ADMINISTRATOR);
		}
		this.comboProfile.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				configureProfile();
			}
		});
		
		this.textStudentCode = new TextField("R.A.");
		this.textStudentCode.setWidth("200px");
		this.textStudentCode.setMaxLength(45);
		
		VerticalLayout tab1 = new VerticalLayout(this.comboProfile, this.textStudentCode);
		tab1.setSpacing(true);
		
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
		
		VerticalLayout tab2 = new VerticalLayout(this.textInstitution, this.textLattes, this.textArea, this.textResearch);
		tab2.setSpacing(true);
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.setHeight("350px");
		this.tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		this.tab.addTab(tab1, "Perfil");
		this.tab.addTab(tab2, "Informações Profissionais");
		
		this.addField(this.tab);
		
		this.loadProfile();
	}
	
	private void loadProfile() {
		this.comboProfile.setValue(this.profile);
		this.textStudentCode.setValue(this.user.getStudentCode());
		this.textInstitution.setValue(this.user.getInstitution());
		this.textLattes.setValue(this.user.getLattes());
		this.textArea.setValue(this.user.getArea());
		this.textResearch.setValue(this.user.getResearch());
		
		if(!this.add) {
			this.comboProfile.setEnabled(false);
		}
	}
	
	private void configureProfile() {
		this.textStudentCode.setVisible(((UserProfile)this.comboProfile.getValue()) == UserProfile.STUDENT);
		
		this.tab.getTab(1).setVisible((((UserProfile)this.comboProfile.getValue()) == UserProfile.PROFESSOR) || (((UserProfile)this.comboProfile.getValue()) == UserProfile.SUPERVISOR));
		this.textInstitution.setEnabled(((UserProfile)this.comboProfile.getValue()) != UserProfile.PROFESSOR);
	}

	@Override
	public void save() {
		try {
			this.profile = (UserProfile) this.comboProfile.getValue();
			
			if(this.add) {
				for(UserProfile p : this.user.getProfiles()) {
					if(p == this.profile) {
						throw new Exception("O perfil " + this.profile.toString() + " já foi adicionado ao usuário.");
					}
				}
			}
			
			this.user.setStudentCode(this.textStudentCode.getValue());
			this.user.setInstitution(this.textInstitution.getValue());
			this.user.setLattes(this.textLattes.getValue());
			this.user.setArea(this.textArea.getValue());
			this.user.setResearch(this.textResearch.getValue());
			
			this.parentWindow.saveProfile(this.user, this.profile, this.add);
			
			this.close();
		} catch(Exception e) {
			this.showErrorNotification("Editar Perfil", e.getMessage());
		}
	}

}
