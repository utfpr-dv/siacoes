package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class EditProfileWindow extends EditWindow {
	
	private final User user;
	private UserProfile profile;
	private final boolean add;
	private final EditUserWindow parentWindow;
	
	private final Select<UserProfile> comboProfile;
	private final TextField textStudentCode;
	private final TextField textInstitution;
	private final TextField textArea;
	private final TextArea textResearch;
	private final TextField textLattes;
	
	private final Tabs tab;
	private final Tab tabPersonal;
	
	public EditProfileWindow(User user, UserProfile profile, boolean add, EditUserWindow parentWindow) {
		super("Editar Perfil", null);
		
		this.user = user;
		this.profile = profile;
		this.add = add;
		this.parentWindow = parentWindow;
		
		this.comboProfile = new Select<UserProfile>();
		this.comboProfile.setLabel("Perfil");
		this.comboProfile.setWidth("200px");
		if(this.user.isExternal()) {
			this.comboProfile.setItems(UserProfile.SUPERVISOR, UserProfile.COMPANYSUPERVISOR, UserProfile.ADMINISTRATOR);
		} else {
			this.comboProfile.setItems(UserProfile.STUDENT, UserProfile.PROFESSOR, UserProfile.COMPANYSUPERVISOR, UserProfile.ADMINISTRATIVE, UserProfile.ADMINISTRATOR);
		}
		this.comboProfile.addValueChangeListener(event -> {
			configureProfile();
		});
		
		this.textStudentCode = new TextField("R.A.");
		this.textStudentCode.setWidth("200px");
		this.textStudentCode.setMaxLength(45);
		
		VerticalLayout tab1 = new VerticalLayout(this.comboProfile, this.textStudentCode);
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
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
		tab2.setSpacing(false);
		tab2.setMargin(false);
		tab2.setPadding(false);
		tab2.setVisible(false);
		
		Tab tabProfile = new Tab("Perfil");
		this.tabPersonal = new Tab("Informações Profissionais");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tabProfile, tab1);
		tabsToPages.put(this.tabPersonal, tab2);
		Div pages = new Div(tab1, tab2);
		
		this.tab = new Tabs(tabProfile, this.tabPersonal);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("820px");
		layout.setHeight("370px");
		
		this.addField(layout);
		
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
		
		this.tabPersonal.setVisible((((UserProfile)this.comboProfile.getValue()) == UserProfile.PROFESSOR) || (((UserProfile)this.comboProfile.getValue()) == UserProfile.SUPERVISOR));
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
