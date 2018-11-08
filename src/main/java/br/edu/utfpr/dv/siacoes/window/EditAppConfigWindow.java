package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.AppConfig.AppTheme;

public class EditAppConfigWindow extends EditWindow {
	
	private final NativeSelect comboTheme;
	private final TextField textHost;
	private final CheckBox checkSigacEnabled;
	private final CheckBox checkSigesEnabled;
	private final CheckBox checkSigetEnabled;
	private final CheckBox checkMobileEnabled;
	
	public EditAppConfigWindow(){
		super("Configurações do Sistema", null);
		
		this.comboTheme = new NativeSelect("Tema da Aplicação");
		this.comboTheme.setWidth("400px");
		this.comboTheme.addItems(AppTheme.values());
		this.comboTheme.select(AppTheme.DEFAULT);
		this.comboTheme.setNullSelectionAllowed(false);
		
		this.textHost = new TextField("Endereço da Aplicação");
		this.textHost.setWidth("400px");
		this.textHost.setMaxLength(255);
		
		this.checkSigacEnabled = new CheckBox("Habilitar módulo de Atividades Complementares");
		
		this.checkSigesEnabled = new CheckBox("Habilitar módulo de Estágio");
		
		this.checkSigetEnabled = new CheckBox("Habilitar módulo de Trabalho de Conclusão de Curso");
		
		this.checkMobileEnabled = new CheckBox("Habilitar o acesso pelo aplicativo SIACOES Mobile");
		
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
			boolean themeChanged = (((AppTheme)this.comboTheme.getValue()).name().toLowerCase() != UI.getCurrent().getTheme());
			
			AppConfig.getInstance().setTheme((AppTheme) this.comboTheme.getValue());
			AppConfig.getInstance().setHost(this.textHost.getValue());
			AppConfig.getInstance().setSigacEnabled(this.checkSigacEnabled.getValue());
			AppConfig.getInstance().setSigesEnabled(this.checkSigesEnabled.getValue());
			AppConfig.getInstance().setSigetEnabled(this.checkSigetEnabled.getValue());
			AppConfig.getInstance().setMobileEnabled(this.checkMobileEnabled.getValue());
			
			AppConfig.getInstance().save();
			
			if(themeChanged){
				UI.getCurrent().setTheme(AppConfig.getInstance().getTheme().name().toLowerCase());
			}
			
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Configurações", e.getMessage());
		}
		
	}

}
