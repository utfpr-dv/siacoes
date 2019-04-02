package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.EmailConfigBO;
import br.edu.utfpr.dv.siacoes.model.EmailConfig;

public class EditEmailConfigWindow extends EditWindow {

	private final EmailConfig config;
	
	private final TextField textHost;
	private final TextField textUser;
	private final PasswordField textPassword;
	private final TextField textPort;
	private final CheckBox checkEnableSsl;
	private final CheckBox checkAuthenticate;
	private final TextArea textSignature;
	
	public EditEmailConfigWindow(){
		super("Editar Configurações", null);
		
		this.textHost = new TextField("Servidor SMTP");
		this.textHost.setWidth("600px");
		this.textHost.setMaxLength(255);
		
		this.textUser = new TextField("Usuário");
		this.textUser.setWidth("600px");
		this.textUser.setMaxLength(255);
		
		this.textPassword = new PasswordField("Senha (em branco para manter a mesma)");
		this.textPassword.setWidth("600px");
		this.textPassword.setMaxLength(255);
		
		this.textPort = new TextField("Porta");
		this.textPort.setWidth("150px");
		this.textPort.setMaxLength(5);
		
		this.checkEnableSsl = new CheckBox("Habilitar SSL");
		
		this.checkAuthenticate = new CheckBox("Utilizar autenticação");
		
		this.textSignature = new TextArea("Assinatura do E-mail");
		this.textSignature.setWidth("600px");
		this.textSignature.setHeight("100px");
		
		this.addField(this.textHost);
		this.addField(this.textUser);
		this.addField(this.textPassword);
		this.addField(new HorizontalLayout(this.textPort, new VerticalLayout(this.checkEnableSsl, this.checkAuthenticate)));
		this.addField(this.textSignature);
		
		this.config = this.loadConfig();
		
		this.textHost.setValue(this.config.getHost());
		this.textUser.setValue(this.config.getUser());
		this.textPort.setValue(String.valueOf(this.config.getPort()));
		this.checkEnableSsl.setValue(this.config.isEnableSsl());
		this.checkAuthenticate.setValue(this.config.isAuthenticate());
		this.textSignature.setValue(this.config.getSignature());
		
		this.textHost.focus();
	}
	
	private EmailConfig loadConfig(){
		try{
			EmailConfigBO bo = new EmailConfigBO();
			
			EmailConfig c = bo.loadConfiguration();
			
			if(c == null){
				return new EmailConfig();
			}else{
				return c;
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Configurações", e.getMessage());
			
			return new EmailConfig();
		}
	}

	@Override
	public void save() {
		try{
			EmailConfigBO bo = new EmailConfigBO();
			
			this.config.setHost(this.textHost.getValue());
			this.config.setUser(this.textUser.getValue());
			if(!this.textPassword.getValue().isEmpty()){
				this.config.setPassword(this.textPassword.getValue());
			}
			this.config.setPort(Integer.parseInt(this.textPort.getValue()));
			this.config.setEnableSsl(this.checkEnableSsl.getValue());
			this.config.setAuthenticate(this.checkAuthenticate.getValue());
			this.config.setSignature(this.textSignature.getValue());
			
			bo.save(Session.getIdUserLog(), this.config);
			
			this.showSuccessNotification("Salvar Configurações", "Configurações salvas com sucesso.");
			this.close();
			this.parentViewRefreshGrid();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Configurações", e.getMessage());
		}
	}
	
}
