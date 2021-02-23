package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.AppConfig.AppTheme;

public class EditAppConfigWindow extends EditWindow {
	
	private final Select<AppTheme> comboTheme;
	private final TextField textHost;
	private final Checkbox checkSigacEnabled;
	private final Checkbox checkSigesEnabled;
	private final Checkbox checkSigetEnabled;
	private final Checkbox checkMobileEnabled;
	
	public EditAppConfigWindow(){
		super("Configurações do Sistema", null);
		
		this.comboTheme = new Select<AppTheme>();
		this.comboTheme.setLabel("Tema da Aplicação");
		this.comboTheme.setWidth("400px");
		this.comboTheme.setItems(AppTheme.values());
		this.comboTheme.setValue(AppTheme.DEFAULT);
		
		this.textHost = new TextField("Endereço da Aplicação");
		this.textHost.setWidth("400px");
		this.textHost.setMaxLength(255);
		
		this.checkSigacEnabled = new Checkbox("Habilitar módulo de Atividades Complementares");
		
		this.checkSigesEnabled = new Checkbox("Habilitar módulo de Estágio");
		
		this.checkSigetEnabled = new Checkbox("Habilitar módulo de Trabalho de Conclusão de Curso");
		
		this.checkMobileEnabled = new Checkbox("Habilitar o acesso pelo aplicativo SIACOES Mobile");
		
		this.addField(this.comboTheme);
		this.addField(this.textHost);
		this.addField(this.checkSigacEnabled);
		this.addField(this.checkSigesEnabled);
		this.addField(this.checkSigetEnabled);
		this.addField(this.checkMobileEnabled);
		
		this.loadConfig();
	}
	
	private void loadConfig(){
		this.comboTheme.setValue(AppConfig.getInstance().getTheme());
		this.textHost.setValue(AppConfig.getInstance().getHost());
		this.checkSigacEnabled.setValue(AppConfig.getInstance().isSigacEnabled());
		this.checkSigesEnabled.setValue(AppConfig.getInstance().isSigesEnabled());
		this.checkSigetEnabled.setValue(AppConfig.getInstance().isSigetEnabled());
		this.checkMobileEnabled.setValue(AppConfig.getInstance().isMobileEnabled());
	}

	@Override
	public void save() {
		try{
			//boolean themeChanged = (((AppTheme)this.comboTheme.getValue()).name().toLowerCase() != UI.getCurrent().getTheme());
			
			AppConfig.getInstance().setTheme((AppTheme) this.comboTheme.getValue());
			AppConfig.getInstance().setHost(this.textHost.getValue());
			AppConfig.getInstance().setSigacEnabled(this.checkSigacEnabled.getValue());
			AppConfig.getInstance().setSigesEnabled(this.checkSigesEnabled.getValue());
			AppConfig.getInstance().setSigetEnabled(this.checkSigetEnabled.getValue());
			AppConfig.getInstance().setMobileEnabled(this.checkMobileEnabled.getValue());
			
			AppConfig.getInstance().save();
			
			/*if(themeChanged){
				UI.getCurrent().setTheme(AppConfig.getInstance().getTheme().name().toLowerCase());
			}*/
			
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Configurações", e.getMessage());
		}
		
	}

}
