package br.edu.utfpr.dv.siacoes.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class LoginView extends CustomComponent implements View {
	
	public static final String NAME = "login";
	
	private final Label label;
	private final Label info;
	private final Label infoAluno;
	private final Label infoServidor;
    private final TextField user;
    private final PasswordField password;
    private final Button loginButton;
    private final Panel panelLogin;
    
    private String redirect;
    
    public LoginView(){
    	this.setCaption(SystemModule.GENERAL.getDescription());
    	this.setResponsive(true);
    	
    	this.label = new Label("Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	this.label.setWidth("800px");
    	this.label.setStyleName("Title");
    	
    	this.info = new Label("Para acessar o SIACOES, efetue o login.");
    	
    	this.infoAluno = new Label("- Se você for aluno, no campo usuário coloque a letra \"a\" e o número do seu R.A. (por exemplo: a1234567 ou a1423599) e no campo senha insira a mesma senha do Sistema Acadêmico.");
    	
    	this.infoServidor = new Label("- Se você for servidor, no campo usuário coloque o seu nome de usuário utilizado para acessar os sistemas da UTFPR, e no campo senha informe a sua senha utilizada para acessar os sistemas da UTFPR.");
    	
    	VerticalLayout layoutInfo = new VerticalLayout(this.info, this.infoAluno, this.infoServidor);
    	layoutInfo.setSizeFull();
    	layoutInfo.setWidth("800px");
    	layoutInfo.setSpacing(true);
    	
    	this.user = new TextField("Usuário");
    	this.user.setWidth("300px");
    	this.user.setInputPrompt("Informe seu nome de usuário");
    	this.user.setInvalidAllowed(false);
    	this.user.setNullRepresentation("");
    	this.user.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    	this.user.setIcon(FontAwesome.USER);

    	this.password = new PasswordField("Senha");
    	this.password.setWidth("300px");
    	this.password.setInputPrompt("Informe sua senha");
    	//this.password.setValue("");
    	this.password.setNullRepresentation("");
    	this.password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    	this.password.setIcon(FontAwesome.LOCK);
        
    	this.loginButton = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	login();
            }
        });
    	this.loginButton.setWidth("300px");
    	this.loginButton.setClickShortcut(KeyCode.ENTER);
        
        VerticalLayout layoutLogin = new VerticalLayout(this.user, this.password, this.loginButton);
        layoutLogin.setSizeUndefined();
        layoutLogin.setResponsive(true);
        layoutLogin.setSpacing(true);
        layoutLogin.setMargin(true);
        
        this.panelLogin = new Panel("Acesso ao Sistema");
        this.panelLogin.setSizeUndefined();
        this.panelLogin.setContent(layoutLogin);
                        
        VerticalLayout fields = new VerticalLayout(this.label, this.panelLogin, layoutInfo);
        fields.setComponentAlignment(this.label, Alignment.MIDDLE_CENTER);
        fields.setComponentAlignment(layoutInfo, Alignment.MIDDLE_CENTER);
        fields.setComponentAlignment(this.panelLogin, Alignment.MIDDLE_CENTER);
        fields.setSizeFull();
        fields.setResponsive(true);
        fields.setSpacing(true);

        this.setCompositionRoot(fields);
        
        this.redirect = "";
    }
	
    @Override
    public void enter(ViewChangeEvent event) {
    	if(event.getParameters() != null){
			String page = event.getParameters();
			
			if((page != null) && (!page.trim().isEmpty())) {
				this.redirect = page.trim();
			}
		}
    	
        user.focus();
    }
    
    private void login(){
    	String username = this.user.getValue();
        String password = this.password.getValue();
        
        try{
        	UserBO bo = new UserBO();
        	User user = bo.validateLogin(username, password);
        	
        	if((user.getDepartment() != null) && (user.getDepartment().getIdDepartment() != 0)){
        		DepartmentBO dbo = new DepartmentBO();
            	user.setDepartment(dbo.findById(user.getDepartment().getIdDepartment()));	
        	}else{
        		user.setDepartment(new Department());
        	}
        	
        	Session.setUser(user);
        	
        	if(!this.redirect.isEmpty()) {
        		getUI().getNavigator().navigateTo(this.redirect);
        	} else {
        		getUI().getNavigator().navigateTo(MainView.NAME);
        	}
        }catch(Exception e){
        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
        	
        	this.password.setValue("");
            this.user.setValue("");
            this.user.focus();
            
            Notification.show("Login", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
    
}