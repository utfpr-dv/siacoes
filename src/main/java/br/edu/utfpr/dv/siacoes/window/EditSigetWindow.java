package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigetWindow extends EditWindow {
	
	private final SigetConfig config;
	
	private final TextField textMinimumScore;
	private final CheckBox checkRegisterProposal;
	private final CheckBox checkShowGradesToStudent;
	
	public EditSigetWindow(SigetConfig config, ListView parentView){
		super("Editar Configurações", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.checkRegisterProposal = new CheckBox("O aluno deve registrar a proposta para TCC1");
		
		this.checkShowGradesToStudent = new CheckBox("Permitir que o acadêmico visualize as notas atribuídas pela banca");
		
		this.addField(this.textMinimumScore);
		this.addField(this.checkRegisterProposal);
		this.addField(this.checkShowGradesToStudent);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.format("%.2f", this.config.getMinimumScore()));
		this.checkRegisterProposal.setValue(this.config.isRegisterProposal());
		this.checkShowGradesToStudent.setValue(this.config.isShowGradesToStudent());
	}

	@Override
	public void save() {
		try{
			SigetConfigBO bo = new SigetConfigBO();
			
			this.config.setMinimumScore(Double.parseDouble(this.textMinimumScore.getValue()));
			this.config.setRegisterProposal(this.checkRegisterProposal.getValue());
			this.config.setShowGradesToStudent(this.checkShowGradesToStudent.getValue());
			
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
