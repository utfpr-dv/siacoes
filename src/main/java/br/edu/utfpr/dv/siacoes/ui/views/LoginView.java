package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.Date;
import java.util.logging.Level;

import javax.servlet.http.Cookie;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.bo.CompanyBO;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Credential;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.service.LoginService;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends ViewFrame implements HasUrlParameter<String> {
	
	private final LoginForm login;
	private final H1 title;
	private final Button buttonAuthDocument;
	private final Button buttonAuthSignature;
    
    private String redirect = "";
    
    public LoginView(){
    	this.title = new H1("SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios");
    	
    	this.login = new LoginForm();
    	this.login.setI18n(this.createPortugueseI18n());
    	this.login.addLoginListener(e -> {
    	    boolean isAuthenticated = this.login(e.getUsername(), e.getPassword(), false);
    	    
    	    if (isAuthenticated) {
    	    	if(this.redirect.trim().isEmpty()) {
    	    		this.getUI().ifPresent(ui -> ui.navigate(Home.class));
    	    	} else {
    	    		this.getUI().ifPresent(ui -> ui.navigate(this.redirect));
    	    	}
    	    } else {
    	    	this.login.setError(true);
    	    }
    	});
    	this.login.addForgotPasswordListener(e -> {
    		this.getUI().ifPresent(ui -> ui.navigate(PasswordView.class));
    	});
    	
    	Details panelLogin = new Details();
    	panelLogin.setSummaryText("Login");
    	panelLogin.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
    	panelLogin.setOpened(true);
    	panelLogin.getElement().getStyle().set("width", "100%");
    	panelLogin.setContent(this.login);
    	
    	this.buttonAuthDocument = new Button("Autenticação de Documentos", event -> {
    		this.getUI().ifPresent(ui -> ui.navigate(AuthenticateView.class));
        });
    	this.buttonAuthDocument.setWidthFull();
    	
    	this.buttonAuthSignature = new Button("Autenticação de Assinatura Eletrônica", event -> {
    		this.getUI().ifPresent(ui -> ui.navigate(AuthDocumentView.class));
        });
    	this.buttonAuthSignature.setWidthFull();
    	
    	VerticalLayout vlAuth = new VerticalLayout(this.buttonAuthDocument, this.buttonAuthSignature);
    	vlAuth.setMargin(false);
    	vlAuth.setPadding(false);
    	vlAuth.setSpacing(false);
    	vlAuth.setSizeFull();
    	
    	Details panelAuth = new Details();
    	panelAuth.setSummaryText("Serviços de Autenticação");
    	panelAuth.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
    	panelAuth.setOpened(true);
    	panelAuth.getElement().getStyle().set("width", "100%");
    	panelAuth.setContent(vlAuth);
    	
    	VerticalLayout v1 = new VerticalLayout(panelLogin, panelAuth);
    	v1.setMargin(false);
    	v1.setPadding(false);
    	v1.setSpacing(false);
    	v1.setAlignItems(Alignment.START);
    	v1.setMaxWidth("360px");
    	v1.setMinWidth("300px");
    	
    	Component stats = this.loadStats();
    	
    	HorizontalLayout hl = new HorizontalLayout(stats, v1);
    	hl.setSizeFull();
    	hl.setAlignItems(Alignment.START);
    	hl.expand(stats);
    	hl.setFlexGrow(1, stats);
    	hl.setFlexGrow(0, v1);
    	
    	VerticalLayout vl = new VerticalLayout(this.title, hl);
    	vl.setAlignItems(Alignment.CENTER);
    	vl.setSizeFull();
    	vl.getElement().getStyle().set("overflow", "auto");
    	
    	this.setViewContent(vl);
    }
    
    private LoginI18n createPortugueseI18n() {
        final LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("SIACOES");
        i18n.getHeader().setDescription("");
        i18n.getForm().setUsername("Usuário");
        i18n.getForm().setTitle("Acesso ao Sistema");
        i18n.getForm().setSubmit("Entrar");
        i18n.getForm().setPassword("Senha");
        i18n.getForm().setForgotPassword("Esqueci minha senha");
        i18n.getErrorMessage().setTitle("Usuário/senha inválidos");
        i18n.getErrorMessage().setMessage("Confira seu usuário e senha e tente novamente.");
        i18n.setAdditionalInformation("- Se você for acadêmico, no campo usuário coloque a letra \"a\" e o número do seu R.A. (por exemplo: a1234567 ou a1423599) e no campo senha insira a mesma senha do Sistema Acadêmico." +
        		"\n\n- Se você for servidor, no campo usuário coloque o seu nome de usuário utilizado para acessar os sistemas da UTFPR, e no campo senha informe a sua senha utilizada para acessar os sistemas da UTFPR.");
        
        return i18n;
    }
    
    private Component loadStats() {
    	VerticalLayout layout = new VerticalLayout();
    	
    	try {
    		UserBO ubo = new UserBO();
    		
    		Details card1 = this.createPanelStat("Acadêmicos Ativos", String.valueOf(ubo.getActiveStudents()));
    		Details card2 = this.createPanelStat("Professores Ativos", String.valueOf(ubo.getActiveProfessors()));
    		Details card3 = this.createPanelStat("Avaliadores Externos", String.valueOf(ubo.getActiveSupervisors()));
			
			ActivitySubmissionBO abo = new ActivitySubmissionBO();
			
			Details card4 = this.createPanelStat("Atividades Complementares Submetidas", String.valueOf(abo.getTotalSubmissions()));
			
			CompanyBO cbo = new CompanyBO();
			
			Details card5 = this.createPanelStat("Empresas Concedentes de Estágio", String.valueOf(cbo.getActiveCompanies()));
			Details card6 = this.createPanelStat("UCE's Conveniadas", String.valueOf(cbo.getActiveCompaniesWithAgreement()));
			
			InternshipBO ibo = new InternshipBO();
			
			Details card7 = this.createPanelStat("Estágios em Andamento", String.valueOf(ibo.getCurrentInternships()));
			Details card8 = this.createPanelStat("Estágios Finalizados", String.valueOf(ibo.getFinishedInternships()));
			
			ProposalBO pbo = new ProposalBO();
			
			Details card9 = this.createPanelStat("TCC's em Andamento", String.valueOf(pbo.getCurrentProposals()));
			
			FinalDocumentBO fbo = new FinalDocumentBO();
			
			Details card10 = this.createPanelStat("TCC's Finalizados", String.valueOf(fbo.getTotalFinalThesis()));
			
			JuryBO jbo = new JuryBO();
			
			Details card11 = this.createPanelStat("Bancas Realizadas (Estágio Obrigatório e TCC)", String.valueOf(jbo.getTotalJury()));
			
			HorizontalLayout h1 = new HorizontalLayout();
			h1.setWidthFull();
			h1.setSpacing(true);
			h1.setMargin(false);
			h1.setPadding(false);
			h1.addAndExpand(card1);
			h1.addAndExpand(card2);
			h1.addAndExpand(card3);
			
			HorizontalLayout h2 = new HorizontalLayout();
			h2.setWidthFull();
			h2.setSpacing(true);
			h2.setMargin(false);
			h2.setPadding(false);
			h2.addAndExpand(card4);
			h2.addAndExpand(card5);
			h2.addAndExpand(card6);
			
			HorizontalLayout h3 = new HorizontalLayout();
			h3.setWidthFull();
			h3.setSpacing(true);
			h3.setMargin(false);
			h3.setPadding(false);
			h3.addAndExpand(card7);
			h3.addAndExpand(card8);
			h3.addAndExpand(card9);
			
			HorizontalLayout h4 = new HorizontalLayout();
			h4.setWidthFull();
			h4.setSpacing(true);
			h4.setMargin(false);
			h4.setPadding(false);
			h4.addAndExpand(card10);
			h4.addAndExpand(card11);
			
			layout.add(h1, h2, h3, h4);
			layout.setSizeFull();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return layout;
    }
    
    private Details createPanelStat(String title, String value) {
    	Details panel = new Details();
    	panel.setSummaryText(title);
    	panel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
    	panel.setOpened(true);
    	panel.setEnabled(false);
    	panel.getElement().getStyle().set("min-width", "300px");
    	
    	H1 label = new H1(value);
    	label.getElement().getStyle().set("margin-top", "30px");
    	label.getElement().getStyle().set("margin-left", "auto");
    	label.getElement().getStyle().set("margin-right", "auto");
    	
    	HorizontalLayout h = new HorizontalLayout(label);
    	h.setMargin(false);
    	h.setPadding(false);
    	h.setSpacing(false);
    	h.setSizeFull();
    	h.setAlignItems(Alignment.CENTER);
    	
    	VerticalLayout layout = new VerticalLayout(h);
    	layout.setMargin(false);
    	layout.setPadding(false);
    	layout.setSpacing(false);
    	layout.setSizeFull();
    	layout.setAlignItems(Alignment.CENTER);
    	
    	panel.setContent(layout);
    	
    	return panel;
    }
    
    private void saveCookieLogin(String username, String password) {
    	try {
			Date now = new Date();
			Algorithm algorithm = Algorithm.HMAC256(new LoginService().getSecret());
			
			String token = JWT.create().withIssuedAt(now).withIssuer(username).withKeyId(password).sign(algorithm);
			
			Cookie cookie = new Cookie("credentials", token);
			
			cookie.setMaxAge(60 * 24 * 3600);
			cookie.setPath("/");
			
			VaadinService.getCurrentResponse().addCookie(cookie);
		} catch (Exception e) { }
    }
    
    private Credential getCredentials() {
    	try {
	    	Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
			
			for (Cookie cookie : cookies) {
				if (("credentials".equals(cookie.getName())) && (cookie.getValue() != null)) {
					Date now = new Date();
					Algorithm algorithm = Algorithm.HMAC256(new LoginService().getSecret());
				    JWTVerifier verifier = JWT.require(algorithm).build();
				    DecodedJWT decodedToken = verifier.verify(cookie.getValue());
					
					if(decodedToken.getIssuedAt().before(now)) {
						Credential credentials = new Credential();
						
						credentials.setLogin(decodedToken.getIssuer());
						credentials.setPassword(decodedToken.getKeyId());
						
						return credentials;
					}
			    }
			}
    	} catch (Exception e) { }
		
		return null;
    }
    
    private boolean login(String username, String password, boolean stayConnected){
    	try{
        	UserBO bo = new UserBO();
        	User user = bo.validateLogin(username, password);
        	
        	if(stayConnected) {
        		this.saveCookieLogin(username, password);
        	}
        	
        	Session.setAdministrator(null);
        	Session.setUser(user);
        	if((user.getProfiles() != null) && (user.getProfiles().size() > 0)) {
        		Session.setSelectedProfile(user.getProfiles().get(0));
        	} else if(!user.isExternal()) {
        		Session.setSelectedProfile(UserProfile.STUDENT);
        	} else {
        		Session.setSelectedProfile(UserProfile.COMPANYSUPERVISOR);
        	}

        	return true;
        } catch(Exception e) {
        	Logger.log(Level.SEVERE, e.getMessage(), e);
        	
        	return false;
        }
    }

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if(parameter != null)
			this.redirect = parameter;
	}
    
}
