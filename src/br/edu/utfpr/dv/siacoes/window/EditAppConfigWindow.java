package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.AppConfig.AppTheme;

public class EditAppConfigWindow extends EditWindow {
	
	private final NativeSelect comboTheme;
	private final TextField textHost;
	
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
		
		this.addField(this.comboTheme);
		this.addField(this.textHost);
		
		this.loadConfig();
	}
	
	private void loadConfig(){
		this.comboTheme.setValue(AppConfig.getInstance().getTheme());
		this.textHost.setValue(AppConfig.getInstance().getHost());
	}

	@Override
	public void save() {
		try{
			boolean themeChanged = (((AppTheme)this.comboTheme.getValue()).name().toLowerCase() != UI.getCurrent().getTheme());
			
			AppConfig.getInstance().setTheme((AppTheme) this.comboTheme.getValue());
			AppConfig.getInstance().setHost(this.textHost.getValue());
			
			AppConfig.getInstance().save();
			
			if(themeChanged){
				UI.getCurrent().setTheme(AppConfig.getInstance().getTheme().name().toLowerCase());
			}
			
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Configurações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
		
	}

}
