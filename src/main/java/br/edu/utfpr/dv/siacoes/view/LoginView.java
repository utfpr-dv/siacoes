package br.edu.utfpr.dv.siacoes.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.CompanyBO;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
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
    private final GridLayout layoutStats;
    
    private String redirect;
    
    public LoginView(){
    	this.setCaption(SystemModule.GENERAL.getDescription());
    	this.setResponsive(true);
    	
    	this.label = new Label("SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	this.label.setStyleName("Title");
    	
    	this.info = new Label("Para acessar o SIACOES, efetue o login.");
    	this.info.setWidth("300px");
    	
    	this.infoAluno = new Label("- Se você for acadêmico, no campo usuário coloque a letra \"a\" e o número do seu R.A. (por exemplo: a1234567 ou a1423599) e no campo senha insira a mesma senha do Sistema Acadêmico.");
    	this.infoAluno.setWidth("300px");
    	
    	this.infoServidor = new Label("- Se você for servidor, no campo usuário coloque o seu nome de usuário utilizado para acessar os sistemas da UTFPR, e no campo senha informe a sua senha utilizada para acessar os sistemas da UTFPR.");
    	this.infoServidor.setWidth("300px");
    	
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
        
        VerticalLayout layoutLogin = new VerticalLayout(this.user, this.password, this.loginButton, this.info, this.infoAluno, this.infoServidor);
        layoutLogin.setSizeUndefined();
        layoutLogin.setResponsive(true);
        layoutLogin.setSpacing(true);
        layoutLogin.setMargin(true);
        
        this.panelLogin = new Panel("Acesso ao Sistema");
        this.panelLogin.setSizeFull();
        this.panelLogin.setWidth("325px");
        this.panelLogin.setContent(layoutLogin);
        
        this.layoutStats = new GridLayout();
        this.layoutStats.setColumns(3);
        this.layoutStats.setSizeFull();
        this.layoutStats.setSpacing(true);
        
        HorizontalLayout h = new HorizontalLayout(this.layoutStats, this.panelLogin);
        h.setSpacing(true);
        h.setSizeFull();
        h.setExpandRatio(this.layoutStats, 1);
        
        VerticalLayout mainLayout = new VerticalLayout(this.label, h);
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(h, 1);
        mainLayout.setMargin(true);

        this.setCompositionRoot(mainLayout);
        
        this.loadStats();
        
        this.redirect = "";
    }
    
    private void loadStats() {
    	try {
    		UserBO ubo = new UserBO();
    		
			this.layoutStats.addComponent(this.createPanelStat("Acadêmicos Ativos", String.valueOf(ubo.getActiveStudents())));
			this.layoutStats.addComponent(this.createPanelStat("Professores Ativos", String.valueOf(ubo.getActiveProfessors())));
			
			ActivitySubmissionBO abo = new ActivitySubmissionBO();
			
			this.layoutStats.addComponent(this.createPanelStat("Atividades Complementares Submetidas", String.valueOf(abo.getTotalSubmissions())));
			
			CompanyBO cbo = new CompanyBO();
			
			this.layoutStats.addComponent(this.createPanelStat("Empresas Concedentes de Estágio", String.valueOf(cbo.getActiveCompanies())));
			this.layoutStats.addComponent(this.createPanelStat("UCE's Conveniadas", String.valueOf(cbo.getActiveCompaniesWithAgreement())));
			
			InternshipBO ibo = new InternshipBO();
			
			this.layoutStats.addComponent(this.createPanelStat("Estágios em Andamento", String.valueOf(ibo.getCurrentInternships())));
			this.layoutStats.addComponent(this.createPanelStat("Estágios Finalizados", String.valueOf(ibo.getFinishedInternships())));
			
			ProposalBO pbo = new ProposalBO();
			
			this.layoutStats.addComponent(this.createPanelStat("TCC's em Andamento", String.valueOf(pbo.getCurrentProposals())));
			
			FinalDocumentBO fbo = new FinalDocumentBO();
			
			this.layoutStats.addComponent(this.createPanelStat("TCC's Publicados", String.valueOf(fbo.getTotalFinalThesis())));
			
			JuryBO jbo = new JuryBO();
			
			this.layoutStats.addComponent(this.createPanelStat("Bancas Realizadas (Estágio Obrigatório e TCC)", String.valueOf(jbo.getTotalJury())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private Panel createPanelStat(String title, String value) {
    	Panel panel = new Panel(title);
    	Label label = new Label(value);
    	
    	label.setStyleName("Title");
    	
    	panel.setContent(label);
    	
    	return panel;
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