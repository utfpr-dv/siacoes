package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigesWindow extends EditWindow {
	
	private final SigesConfig config;
	
	private final TextField textMinimumScore;
	private final TextField textSupervisorPonderosity;
	private final TextField textCompanySupervisorPonderosity;
	
	public EditSigesWindow(SigesConfig config, ListView parentView){
		super("Editar Configurações", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.textSupervisorPonderosity = new TextField("Peso da Nota do Orientador");
		this.textSupervisorPonderosity.setWidth("100px");
		
		this.textCompanySupervisorPonderosity = new TextField("Peso da Nota do Supervisor na Empresa");
		this.textCompanySupervisorPonderosity.setWidth("100px");
		
		this.addField(this.textMinimumScore);
		this.addField(this.textSupervisorPonderosity);
		this.addField(this.textCompanySupervisorPonderosity);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.valueOf(this.config.getMinimumScore()));
		this.textSupervisorPonderosity.setValue(String.valueOf(this.config.getSupervisorPonderosity()));
		this.textCompanySupervisorPonderosity.setValue(String.valueOf(this.config.getCompanySupervisorPonderosity()));
	}

	@Override
	public void save() {
		try{
			SigesConfigBO bo = new SigesConfigBO();
			
			this.config.setMinimumScore(Double.parseDouble(this.textMinimumScore.getValue()));
			this.config.setSupervisorPonderosity(Double.parseDouble(this.textSupervisorPonderosity.getValue()));
			this.config.setCompanySupervisorPonderosity(Double.parseDouble(this.textCompanySupervisorPonderosity.getValue()));
			
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
