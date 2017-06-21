package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.SigacConfigBO;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigacWindow extends EditWindow {
	
	private final SigacConfig config;
	
	private final TextField textMinimumScore;
	
	public EditSigacWindow(SigacConfig config, ListView parentView){
		super("Configurações SIGAC", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.addField(this.textMinimumScore);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.valueOf(this.config.getMinimumScore()));
	}

	@Override
	public void save() {
		try{
			SigacConfigBO bo = new SigacConfigBO();
			
			this.config.setMinimumScore(Double.parseDouble(this.textMinimumScore.getValue()));
			
			bo.save(this.config);
			
			Notification.show("Salvar Configurações", "Configurações salvas com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Configurações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
