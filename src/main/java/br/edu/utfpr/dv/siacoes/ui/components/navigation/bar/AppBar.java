package br.edu.utfpr.dv.siacoes.ui.components.navigation.bar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.FlexBoxLayout;
import br.edu.utfpr.dv.siacoes.ui.components.navigation.tab.NaviTab;
import br.edu.utfpr.dv.siacoes.ui.components.navigation.tab.NaviTabs;
import br.edu.utfpr.dv.siacoes.ui.util.LumoStyles;
import br.edu.utfpr.dv.siacoes.ui.util.UIUtils;
import br.edu.utfpr.dv.siacoes.ui.views.Home;
import br.edu.utfpr.dv.siacoes.ui.views.LoginView;
import br.edu.utfpr.dv.siacoes.ui.windows.EditPasswordWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProfessorProfileWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditStudentProfileWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditUserProfileWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/components/app-bar.css")
public class AppBar extends FlexBoxLayout {

	private String CLASS_NAME = "app-bar";

	private FlexBoxLayout container;

	private Button menuIcon;
	private Button contextIcon;

	private H4 title;
	private FlexBoxLayout actionItems;
	private HorizontalLayout avatar;

	private FlexBoxLayout tabContainer;
	private NaviTabs tabs;
	private ArrayList<Registration> tabSelectionListeners;
	private Button addTab;

	private TextField search;
	private Registration searchRegistration;

	public enum NaviMode {
		MENU, CONTEXTUAL
	}

	public AppBar(String title, NaviTab... tabs) {
		setClassName(CLASS_NAME);
		setHeight("55px");

		initMenuIcon();
		initContextIcon();
		initTitle(title);
		initSearch();
		initAvatar();
		initActionItems();
		initContainer();
		initTabs(tabs);
	}

	public void setNaviMode(NaviMode mode) {
		if (mode.equals(NaviMode.MENU)) {
			menuIcon.setVisible(true);
			contextIcon.setVisible(false);
		} else {
			menuIcon.setVisible(false);
			contextIcon.setVisible(true);
		}
	}

	private void initMenuIcon() {
		menuIcon = UIUtils.createTertiaryInlineButton(VaadinIcon.MENU);
		menuIcon.addClassName(CLASS_NAME + "__navi-icon");
		menuIcon.addClickListener(e -> MainLayout.get().getNaviDrawer().toggle());
		UIUtils.setAriaLabel("Menu", menuIcon);
		UIUtils.setLineHeight("1", menuIcon);
	}

	private void initContextIcon() {
		contextIcon = UIUtils
				.createTertiaryInlineButton(VaadinIcon.ARROW_LEFT);
		contextIcon.addClassNames(CLASS_NAME + "__context-icon");
		contextIcon.setVisible(false);
		UIUtils.setAriaLabel("Back", contextIcon);
		UIUtils.setLineHeight("1", contextIcon);
	}

	private void initTitle(String title) {
		this.title = new H4(title);
		this.title.setClassName(CLASS_NAME + "__title");
	}

	private void initSearch() {
		search = new TextField();
		search.setPlaceholder("Search");
		search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		search.setVisible(false);
	}

	private void initAvatar() {
		final User user = Session.getUser();
		
		H6 userName = new H6(user.getName());
		userName.getElement().getStyle().set("margin", "0");
		
		Image avatarImage = new Image();
		avatarImage.setClassName(CLASS_NAME + "__avatar");
		avatarImage.setAlt("User menu");
		
		if(user.getPhoto() != null) {
			StreamResource resource = new StreamResource("avatar" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(DateUtils.getNow().getTime()) + ".png", () -> new ByteArrayInputStream(user.getPhoto()));
			resource.setCacheTime(0);
			avatarImage.setSrc(resource);
		} else {
			avatarImage.setSrc(UIUtils.IMG_PATH + "avatar.png");
		}
		
		this.avatar = new HorizontalLayout(userName, avatarImage);
		this.avatar.setSpacing(true);
		this.avatar.setMargin(false);
		this.avatar.setPadding(false);
		this.avatar.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, userName);
		this.avatar.getStyle().set("cursor", "pointer");

		ContextMenu contextMenu = new ContextMenu(avatar);
		contextMenu.setOpenOnClick(true);
		contextMenu.addItem("Meus Dados", event -> { 
			if(Session.getSelectedProfile() == UserProfile.STUDENT) {
				EditStudentProfileWindow window = new EditStudentProfileWindow(Session.getUser(), Session.getSelectedDepartment());
				window.open();
			} else if(Session.getSelectedProfile() == UserProfile.PROFESSOR) {
				EditProfessorProfileWindow window = new EditProfessorProfileWindow(Session.getUser(), Session.getSelectedDepartment());
				window.open();
			} else {
				EditUserProfileWindow window = new EditUserProfileWindow(Session.getUser(), Session.getSelectedProfile());
				window.open();
			}
		});
		if(Session.getUser().isExternal() && !Session.isLoggedAs()) {
			contextMenu.addItem("Alterar Senha", event -> { 
				EditPasswordWindow window = new EditPasswordWindow();
				window.open();
			});
		}
		if(Session.getUser().getProfiles().size() > 1) {
			SubMenu profile = contextMenu.addItem("Perfil").getSubMenu();
			
			for(UserProfile p : Session.getUser().getProfiles()) {
				if(p != Session.getSelectedProfile()) {
					profile.addItem(p.toString(), event -> { 
						Session.setSelectedProfile(p);
                        
						MainLayout.reloadNaviItems();
						this.getUI().ifPresent(ui -> ui.navigate(Home.class));
					});
				}
			}
		}
		if((Session.getSelectedProfile() == UserProfile.PROFESSOR) || (Session.getSelectedProfile() == UserProfile.STUDENT)) {
			try {
				List<UserDepartment> departments = Session.getListDepartments();
				
				if(departments.size() > 1) {
					SubMenu department = contextMenu.addItem("Departamento").getSubMenu();
					
					for(UserDepartment d : departments) {
						if(d.getDepartment().getIdDepartment() != Session.getSelectedDepartment().getDepartment().getIdDepartment()) {
							department.addItem(d.getDepartment().getName() + " - " + d.getDepartment().getCampus().getName(), event -> { 
								Session.setSelectedDepartment(d);
		                        
								MainLayout.reloadNaviItems();
								this.getUI().ifPresent(ui -> ui.navigate(Home.class));
							});
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(Session.isLoggedAs()) {
			contextMenu.addItem("Logoff de " + Session.getUser().getName(), event -> { 
				Session.logoffAs();
				
				MainLayout.reloadNaviItems();
				this.getUI().ifPresent(ui -> ui.navigate(Home.class));
			});
		}
		contextMenu.addItem("Logoff", event -> { 
			Session.logoff();
			
			this.getUI().ifPresent(ui -> ui.navigate(LoginView.class));
		});
	}

	private void initActionItems() {
		actionItems = new FlexBoxLayout();
		actionItems.addClassName(CLASS_NAME + "__action-items");
		actionItems.setVisible(false);
	}

	private void initContainer() {
		container = new FlexBoxLayout(menuIcon, contextIcon, this.title, search,
				actionItems, avatar);
		container.addClassName(CLASS_NAME + "__container");
		container.setAlignItems(FlexComponent.Alignment.CENTER);
		container.setFlexGrow(1, search);
		add(container);
	}

	private void initTabs(NaviTab... tabs) {
		addTab = UIUtils.createSmallButton(VaadinIcon.PLUS);
		addTab.addClickListener(e -> this.tabs
				.setSelectedTab(addClosableNaviTab("New Tab", Home.class)));
		addTab.setVisible(false);

		this.tabs = tabs.length > 0 ? new NaviTabs(tabs) : new NaviTabs();
		this.tabs.setClassName(CLASS_NAME + "__tabs");
		this.tabs.setVisible(false);
		for (NaviTab tab : tabs) {
			configureTab(tab);
		}

		this.tabSelectionListeners = new ArrayList<>();

		tabContainer = new FlexBoxLayout(this.tabs, addTab);
		tabContainer.addClassName(CLASS_NAME + "__tab-container");
		tabContainer.setAlignItems(FlexComponent.Alignment.CENTER);
		add(tabContainer);
	}

	/* === MENU ICON === */

	public Button getMenuIcon() {
		return menuIcon;
	}

	/* === CONTEXT ICON === */

	public Button getContextIcon() {
		return contextIcon;
	}

	public void setContextIcon(Icon icon) {
		contextIcon.setIcon(icon);
	}

	/* === TITLE === */

	public String getTitle() {
		return this.title.getText();
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	/* === ACTION ITEMS === */

	public Component addActionItem(Component component) {
		actionItems.add(component);
		updateActionItemsVisibility();
		return component;
	}

	public Button addActionItem(VaadinIcon icon) {
		Button button = UIUtils.createButton(icon, ButtonVariant.LUMO_SMALL,
				ButtonVariant.LUMO_TERTIARY);
		addActionItem(button);
		return button;
	}

	public void removeAllActionItems() {
		actionItems.removeAll();
		updateActionItemsVisibility();
	}

	/* === AVATAR == */

	/*public Image getAvatar() {
		return avatar;
	}*/

	/* === TABS === */

	public void centerTabs() {
		tabs.addClassName(LumoStyles.Margin.Horizontal.AUTO);
	}

	private void configureTab(Tab tab) {
		tab.addClassName(CLASS_NAME + "__tab");
		updateTabsVisibility();
	}

	public Tab addTab(String text) {
		Tab tab = tabs.addTab(text);
		configureTab(tab);
		return tab;
	}

	public Tab addTab(String text,
	                  Class<? extends Component> navigationTarget) {
		Tab tab = tabs.addTab(text, navigationTarget);
		configureTab(tab);
		return tab;
	}

	public Tab addClosableNaviTab(String text,
	                              Class<? extends Component> navigationTarget) {
		Tab tab = tabs.addClosableTab(text, navigationTarget);
		configureTab(tab);
		return tab;
	}

	public Tab getSelectedTab() {
		return tabs.getSelectedTab();
	}

	public void setSelectedTab(Tab selectedTab) {
		tabs.setSelectedTab(selectedTab);
	}

	public void updateSelectedTab(String text,
	                              Class<? extends Component> navigationTarget) {
		tabs.updateSelectedTab(text, navigationTarget);
	}

	public void navigateToSelectedTab() {
		tabs.navigateToSelectedTab();
	}

	public void addTabSelectionListener(
			ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
		Registration registration = tabs.addSelectedChangeListener(listener);
		tabSelectionListeners.add(registration);
	}

	public int getTabCount() {
		return tabs.getTabCount();
	}

	public void removeAllTabs() {
		tabSelectionListeners.forEach(registration -> registration.remove());
		tabSelectionListeners.clear();
		tabs.removeAll();
		updateTabsVisibility();
	}

	/* === ADD TAB BUTTON === */

	public void setAddTabVisible(boolean visible) {
		addTab.setVisible(visible);
	}

	/* === SEARCH === */

	public void searchModeOn() {
		menuIcon.setVisible(false);
		title.setVisible(false);
		actionItems.setVisible(false);
		tabContainer.setVisible(false);

		contextIcon.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
		contextIcon.setVisible(true);
		searchRegistration = contextIcon
				.addClickListener(e -> searchModeOff());

		search.setVisible(true);
		search.focus();
	}

	public void addSearchListener(HasValue.ValueChangeListener listener) {
		search.addValueChangeListener(listener);
	}

	public void setSearchPlaceholder(String placeholder) {
		search.setPlaceholder(placeholder);
	}

	private void searchModeOff() {
		menuIcon.setVisible(true);
		title.setVisible(true);
		tabContainer.setVisible(true);

		updateActionItemsVisibility();
		updateTabsVisibility();

		contextIcon.setVisible(false);
		searchRegistration.remove();

		search.clear();
		search.setVisible(false);
	}

	/* === RESET === */

	public void reset() {
		title.setText("");
		setNaviMode(AppBar.NaviMode.MENU);
		removeAllActionItems();
		removeAllTabs();
	}

	/* === UPDATE VISIBILITY === */

	private void updateActionItemsVisibility() {
		actionItems.setVisible(actionItems.getComponentCount() > 0);
	}

	private void updateTabsVisibility() {
		tabs.setVisible(tabs.getComponentCount() > 0);
	}
}
