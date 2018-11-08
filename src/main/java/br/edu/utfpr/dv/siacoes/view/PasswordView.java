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
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.bo.EmailMessageBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.components.Notification;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User;

public class PasswordView extends CustomComponent implements View {
	
	public static final String NAME = "password";

	private final Label label;
	private final TextField textLogin;
    private final PasswordField textPassword;
    private final PasswordField textConfirmPassword;
    private final Button buttonSendUser;
    private final Button buttonSendPassword;
    private final Button buttonLogin;
    private final Button buttonStudentPortal;
    private final VerticalLayout mainLayout;
	
	public PasswordView() {
		this.setCaption(SystemModule.GENERAL.getDescription());
    	this.setResponsive(true);
    	
    	this.label = new Label("SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	this.label.setStyleName("Title");
    	
    	this.textLogin = new TextField("Usuário");
    	this.textLogin.setWidth("300px");
    	this.textLogin.setInputPrompt("Informe seu nome de usuário");
    	this.textLogin.setInvalidAllowed(false);
    	this.textLogin.setNullRepresentation("");
    	this.textLogin.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    	this.textLogin.setIcon(FontAwesome.USER);
    	
    	this.buttonSendUser = new Button("Enviar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	requestNewPassword();
            }
        });
    	this.buttonSendUser.setWidth("300px");
    	this.buttonSendUser.setClickShortcut(KeyCode.ENTER);

    	this.textPassword = new PasswordField("Senha");
    	this.textPassword.setWidth("300px");
    	this.textPassword.setInputPrompt("Informe sua nova senha");
    	this.textPassword.setNullRepresentation("");
    	this.textPassword.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    	this.textPassword.setIcon(FontAwesome.LOCK);

    	this.textConfirmPassword = new PasswordField("Senha");
    	this.textConfirmPassword.setWidth("300px");
    	this.textConfirmPassword.setInputPrompt("Confirme sua nova senha");
    	this.textConfirmPassword.setNullRepresentation("");
    	this.textConfirmPassword.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    	this.textConfirmPassword.setIcon(FontAwesome.LOCK);
    	
    	this.buttonSendPassword = new Button("Redefinir Senha");
    	this.buttonSendPassword.setWidth("300px");
    	this.buttonSendPassword.setClickShortcut(KeyCode.ENTER);
    	
    	this.buttonLogin = new Button("Retornar ao Login", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().getNavigator().navigateTo(LoginView.NAME);
            }
        });
    	this.buttonLogin.setWidth("300px");
    	
    	this.buttonStudentPortal = new Button("Portal do Aluno", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().getPage().open("http://portal.utfpr.edu.br/alunos/portal-do-aluno", "_blank");
            }
        });
    	this.buttonStudentPortal.setWidth("300px");
    	
    	this.mainLayout = new VerticalLayout(this.label);
    	this.mainLayout.setSpacing(true);
    	this.mainLayout.setSizeFull();
        this.mainLayout.setMargin(true);
        this.mainLayout.setComponentAlignment(this.label, Alignment.MIDDLE_CENTER);

        this.setCompositionRoot(this.mainLayout);
	}
	
	private void buildWindow(String token) {
		Label label = new Label("Informe a sua nova senha.");
		label.setWidth("300px");
		
		VerticalLayout layout = new VerticalLayout(label, this.textPassword, this.textConfirmPassword, this.buttonSendPassword, this.buttonLogin);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		Panel panel = new Panel("Redefinição de Senha");
		panel.setWidth("325px");
		panel.setContent(layout);
		
		this.buttonSendPassword.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	redefinePassword(token);
            }
        });
		
		this.mainLayout.addComponent(panel);
		this.mainLayout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		this.textPassword.focus();
	}
	
	private void buildWindow() {
		Label label = new Label("Informe seu nome de usuário para redefinir a sua senha.");
		label.setWidth("300px");
		
		VerticalLayout layout = new VerticalLayout(label, this.textLogin, this.buttonSendUser, this.buttonLogin);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		Panel panel = new Panel("Redefinição de Senha");
		panel.setWidth("325px");
		panel.setContent(layout);
		
		this.mainLayout.addComponent(panel);
		this.mainLayout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		this.textLogin.focus();
	}
	
	private void buildWindow(User user, boolean confirmEmail, boolean success) {
		this.mainLayout.removeAllComponents();
		this.mainLayout.addComponent(this.label);
		
		Label label = new Label();
		label.setCaptionAsHtml(true);
		
		VerticalLayout layout = new VerticalLayout(label, this.buttonLogin);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		Panel panel = new Panel("Redefinição de Senha");
		panel.setWidth("325px");
		panel.setContent(layout);
		
		if(success) {
			label.setCaption("Sua senha foi redefinida com sucesso.");
		} else if(confirmEmail) {
			label.setCaption("O link para redefinição de senha foi enviado para o<br/>e-mail " + user.getEmail() + ".");
		} else if(new UserBO().loginIsStudent(user.getLogin())) {
			label.setCaption("Utilize o seu Registro Acadêmico (RA) e proceda a<br/>Recuperação de sua senha no Portal do Aluno.");
			layout.addComponent(this.buttonStudentPortal);
		} else {
			label.setCaption("A senha utilizada para seu acesso é a mesma utilizada<br/>para acessar os sistemas da UTFPR. Caso tenha<br/>esquecido, entre em contato com a Coordenadoria de<br/>Gestão de Tecnologia da Informação (COGETI) do seu<br/>câmpus.");
		}
		
		label.setWidth("300px");
		
		this.mainLayout.addComponent(panel);
		this.mainLayout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
	}
	
	private void requestNewPassword() {
		try {
			UserBO bo = new UserBO();
			User user = bo.findByLogin(this.textLogin.getValue().trim());
			
			if((user == null) || (user.getIdUser() == 0)) {
				Notification.showErrorNotification("Redefinição de Senha", "Usuário não encontrado na base de dados.");
			} else if(user.isExternal()) {
				new EmailMessageBO().sendForgotPasswordEmail(user);
				this.buildWindow(user, true, false);
			} else if(bo.loginIsStudent(this.textLogin.getValue().trim())) {
				this.buildWindow(user, false, false);
			} else {
				this.buildWindow(user, false, false);
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Redefinição de Senha", e.getMessage());
		}
	}
	
	private void redefinePassword(String token) {
		try {
			if(!this.textPassword.getValue().equals(this.textConfirmPassword.getValue())){
				this.textPassword.focus();
				throw new Exception("As senhas não conferem.");
			}
			
			new UserBO().changePassword(token, this.textPassword.getValue());
			
			this.buildWindow(null, false, true);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.showErrorNotification("Redefinição de Senha", e.getMessage());
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters() != null){
			String token = event.getParameters();
			
			if((token != null) && (!token.trim().isEmpty())) {
				this.buildWindow(token);
			} else {
				this.buildWindow();
			}
		} else {
			this.buildWindow();
		}
	}

}
