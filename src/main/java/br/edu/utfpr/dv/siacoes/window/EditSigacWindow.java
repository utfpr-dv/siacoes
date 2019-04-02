package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.SigacConfigBO;
import br.edu.utfpr.dv.siacoes.components.ByteSizeField;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigacWindow extends EditWindow {
	
	private final SigacConfig config;
	
	private final TextField textMinimumScore;
	private final ByteSizeField textMaxFileSize;
	
	public EditSigacWindow(SigacConfig config, ListView parentView){
		super("Configurações SIGAC", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.textMaxFileSize = new ByteSizeField("Tamanho máximo para submissão de arquivos");
		
		this.addField(this.textMinimumScore);
		this.addField(this.textMaxFileSize);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.valueOf(this.config.getMinimumScore()));
		this.textMaxFileSize.setValue(this.config.getMaxFileSize());
	}

	@Override
	public void save() {
		try{
			SigacConfigBO bo = new SigacConfigBO();
			
			this.config.setMinimumScore(Double.parseDouble(this.textMinimumScore.getValue().replace(",", ".")));
			this.config.setMaxFileSize((int)this.textMaxFileSize.getValue());
			
			bo.save(Session.getIdUserLog(), this.config);
			
			this.showSuccessNotification("Salvar Configurações", "Configurações salvas com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Configurações", e.getMessage());
		}
	}

}
