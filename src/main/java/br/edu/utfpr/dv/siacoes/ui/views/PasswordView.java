package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.logging.Level;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.EmailMessageBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;

@PageTitle("Recuperar Senha")
@Route(value = "password")
public class PasswordView extends ViewFrame implements HasUrlParameter<String> {
	
	public static final String NAME = "password";

	private final H1 label;
	private final TextField textLogin;
    private final PasswordField textPassword;
    private final PasswordField textConfirmPassword;
    private final Button buttonSendUser;
    private final Button buttonSendPassword;
    private final Button buttonLogin;
    private final Button buttonStudentPortal;
    private final VerticalLayout mainLayout;
	
	public PasswordView() {
		this.label = new H1("SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	
    	this.textLogin = new TextField("Usuário");
    	this.textLogin.setWidth("300px");
    	
    	this.buttonSendUser = new Button("Enviar", event -> {
            requestNewPassword();
        });
    	this.buttonSendUser.setWidth("300px");    	

    	this.textPassword = new PasswordField("Senha");
    	this.textPassword.setWidth("300px");

    	this.textConfirmPassword = new PasswordField("Senha");
    	this.textConfirmPassword.setWidth("300px");
    	
    	this.buttonSendPassword = new Button("Redefinir Senha");
    	this.buttonSendPassword.setWidth("300px");
    	
    	this.buttonLogin = new Button("Retornar ao Login", event -> {
    		this.getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
    	this.buttonLogin.setWidth("300px");
    	
    	this.buttonStudentPortal = new Button("Portal do Aluno", event -> {
    		UI.getCurrent().getPage().open("http://portal.utfpr.edu.br/alunos/portal-do-aluno", "_blank");
        });
    	this.buttonStudentPortal.setWidth("300px");
    	
    	this.mainLayout = new VerticalLayout(this.label);
    	this.mainLayout.setSpacing(false);
    	this.mainLayout.setSizeFull();
        this.mainLayout.setMargin(false);
        this.mainLayout.setPadding(false);
        this.mainLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        this.setViewContent(this.mainLayout);
	}
	
	private void buildWindow(String token) {
		Label label = new Label("Informe a sua nova senha.");
		label.setWidth("300px");
		
		VerticalLayout layout = new VerticalLayout(label, this.textPassword, this.textConfirmPassword, this.buttonSendPassword, this.buttonLogin);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		Details panel = new Details();
		panel.setSummaryText("Redefinição de Senha");
		panel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panel.setOpened(true);
		panel.getElement().getStyle().set("width", "325px");
		panel.setContent(layout);
		
		this.buttonSendPassword.addClickListener(event -> {
            redefinePassword(token);
        });
		
		this.mainLayout.add(panel);
		this.mainLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		this.textPassword.focus();
	}
	
	private void buildWindow() {
		Label label = new Label("Informe seu nome de usuário para redefinir a sua senha.");
		label.setWidth("300px");
		
		VerticalLayout layout = new VerticalLayout(label, this.textLogin, this.buttonSendUser, this.buttonLogin);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		Details panel = new Details();
		panel.setSummaryText("Redefinição de Senha");
		panel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panel.setOpened(true);
		panel.getElement().getStyle().set("width", "325px");
		panel.setContent(layout);
		
		this.mainLayout.add(panel);
		this.mainLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		this.textLogin.focus();
	}
	
	private void buildWindow(User user, boolean confirmEmail, boolean success) {
		this.mainLayout.removeAll();
		this.mainLayout.add(this.label);
		
		Label label = new Label();
		
		VerticalLayout layout = new VerticalLayout(label, this.buttonLogin);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		Details panel = new Details();
		panel.setSummaryText("Redefinição de Senha");
		panel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panel.setOpened(true);
		panel.getElement().getStyle().set("width", "325px");
		panel.setContent(layout);
		
		if(success) {
			label.setText("Sua senha foi redefinida com sucesso.");
		} else if(confirmEmail) {
			label.setText("O link para redefinição de senha foi enviado para o<br/>e-mail " + user.getEmail() + ".");
		} else if(new UserBO().loginIsStudent(user.getLogin())) {
			label.setText("Utilize o seu Registro Acadêmico (RA) e proceda a<br/>Recuperação de sua senha no Portal do Aluno.");
			layout.add(this.buttonStudentPortal);
		} else {
			label.setText("A senha utilizada para seu acesso é a mesma utilizada<br/>para acessar os sistemas da UTFPR. Caso tenha<br/>esquecido, entre em contato com a Coordenadoria de<br/>Gestão de Tecnologia da Informação (COGETI) do seu<br/>câmpus.");
		}
		
		label.setWidth("300px");
		
		this.mainLayout.add(panel);
		this.mainLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}
	
	private void requestNewPassword() {
		try {
			UserBO bo = new UserBO();
			User user = bo.findByLogin(this.textLogin.getValue().trim());
			
			if((user == null) || (user.getIdUser() == 0)) {
				this.showErrorNotification("Redefinição de Senha", "Usuário não encontrado na base de dados.");
			} else if(user.isExternal()) {
				new EmailMessageBO().sendForgotPasswordEmail(user);
				this.buildWindow(user, true, false);
			} else if(bo.loginIsStudent(this.textLogin.getValue().trim())) {
				this.buildWindow(user, false, false);
			} else {
				this.buildWindow(user, false, false);
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Redefinição de Senha", e.getMessage());
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Redefinição de Senha", e.getMessage());
		}
	}
	
	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if((parameter != null) && (!parameter.trim().isEmpty())){
			this.buildWindow(parameter);
		} else {
			this.buildWindow();
		}
	}

}
