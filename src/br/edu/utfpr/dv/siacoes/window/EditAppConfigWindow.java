package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.AppConfig.AppTheme;

public class EditAppConfigWindow extends EditWindow {
	
	private final NativeSelect comboTheme;
	
	public EditAppConfigWindow(){
		super("Configurações do Sistema", null);
		
		this.comboTheme = new NativeSelect("Tema da Aplicação");
		this.comboTheme.setWidth("400px");
		this.comboTheme.addItem(AppTheme.DEFAULT);
		this.comboTheme.addItem(AppTheme.FACEBOOK);
		this.comboTheme.addItem(AppTheme.FLAT);
		this.comboTheme.addItem(AppTheme.LIGHT);
		this.comboTheme.addItem(AppTheme.METRO);
		this.comboTheme.addItem(AppTheme.PINK);
		this.comboTheme.select(AppTheme.DEFAULT);
		this.comboTheme.setNullSelectionAllowed(false);
		
		this.addField(this.comboTheme);
	}
	
	private void loadConfig(){
		this.comboTheme.setValue(AppConfig.getInstance().getTheme());
	}

	@Override
	public void save() {
		try{
			AppConfig.getInstance().setTheme((AppTheme) this.comboTheme.getValue());
			
			AppConfig.getInstance().save();
			
			UI.getCurrent().setTheme(AppConfig.getInstance().getTheme().name().toLowerCase());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Configurações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
		
	}

}
