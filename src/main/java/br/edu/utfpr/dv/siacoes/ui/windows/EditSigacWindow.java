package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.SigacConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.SigacConfig;
import br.edu.utfpr.dv.siacoes.ui.components.ByteSizeField;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditSigacWindow extends EditWindow {
	
	private final SigacConfig config;
	
	private final TextField textMinimumScore;
	private final ByteSizeField textMaxFileSize;
	private final Checkbox checkUseDigitalSignature;
	private final Checkbox checkNotifyActivityFeedback;
	
	public EditSigacWindow(SigacConfig config, ListView parentView){
		super("Configurações SIGAC", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("150px");
		
		this.textMaxFileSize = new ByteSizeField("Tamanho máximo para submissão de arquivos");
		
		this.checkNotifyActivityFeedback = new Checkbox("Notificar os acadêmicos a cada atividade validada");
		
		this.checkUseDigitalSignature = new Checkbox("Usar assinatura digital");
		
		this.addField(this.textMinimumScore);
		this.addField(this.textMaxFileSize);
		this.addField(this.checkNotifyActivityFeedback);
		this.addField(this.checkUseDigitalSignature);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.valueOf(this.config.getMinimumScore()));
		this.textMaxFileSize.setValue(this.config.getMaxFileSize());
		this.checkUseDigitalSignature.setValue(this.config.isUseDigitalSignature());
		this.checkNotifyActivityFeedback.setValue(this.config.isNotifyActivityFeedback());
	}

	@Override
	public void save() {
		try{
			SigacConfigBO bo = new SigacConfigBO();
			
			this.config.setMinimumScore(Double.parseDouble(this.textMinimumScore.getValue().replace(",", ".")));
			this.config.setMaxFileSize(this.textMaxFileSize.getValue().intValue());
			this.config.setUseDigitalSignature(this.checkUseDigitalSignature.getValue());
			this.config.setNotifyActivityFeedback(this.checkNotifyActivityFeedback.getValue());
			
			bo.save(Session.getIdUserLog(), this.config);
			
			this.showSuccessNotification("Salvar Configurações", "Configurações salvas com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Configurações", e.getMessage());
		}
	}

}
