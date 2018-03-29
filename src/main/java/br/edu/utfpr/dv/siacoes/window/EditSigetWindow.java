package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigetWindow extends EditWindow {
	
	private final SigetConfig config;
	
	private final TextField textMinimumScore;
	private final CheckBox checkRegisterProposal;
	private final CheckBox checkShowGradesToStudent;
	private final NativeSelect comboSupervisorFilter;
	private final NativeSelect comboCosupervisorFilter;
	private final YearField textSupervisorIndicator;
	private final YearField textMaxTutoredStage1;
	private final YearField textMaxTutoredStage2;
	
	public EditSigetWindow(SigetConfig config, ListView parentView){
		super("Editar Configurações", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.checkRegisterProposal = new CheckBox("O aluno deve registrar a proposta para TCC1");
		
		this.checkShowGradesToStudent = new CheckBox("Permitir que o acadêmico visualize as notas atribuídas pela banca");
		
		this.comboSupervisorFilter = new NativeSelect("Orientador deve pertencer ao");
		this.comboSupervisorFilter.setWidth("400px");
		this.comboSupervisorFilter.setNullSelectionAllowed(false);
		this.comboSupervisorFilter.addItem(SupervisorFilter.DEPARTMENT);
		this.comboSupervisorFilter.addItem(SupervisorFilter.CAMPUS);
		this.comboSupervisorFilter.addItem(SupervisorFilter.INSTITUTION);
		this.comboSupervisorFilter.addItem(SupervisorFilter.EVERYONE);
		
		this.comboCosupervisorFilter = new NativeSelect("Co-orientador deve pertencer ao");
		this.comboCosupervisorFilter.setWidth("400px");
		this.comboCosupervisorFilter.setNullSelectionAllowed(false);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.DEPARTMENT);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.CAMPUS);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.INSTITUTION);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.EVERYONE);
		
		this.textSupervisorIndicator = new YearField();
		this.textSupervisorIndicator.setCaption("Número de máximo indicações do orientador");
		
		this.textMaxTutoredStage1 = new YearField();
		this.textMaxTutoredStage1.setCaption("Número máximo de orientados para TCC 1");
		
		this.textMaxTutoredStage2 = new YearField();
		this.textMaxTutoredStage2.setCaption("Número máximo de orientados para TCC 2");
		
		this.addField(this.textMinimumScore);
		this.addField(this.checkRegisterProposal);
		this.addField(this.checkShowGradesToStudent);
		this.addField(this.comboSupervisorFilter);
		this.addField(this.comboCosupervisorFilter);
		this.addField(this.textSupervisorIndicator);
		this.addField(this.textMaxTutoredStage1);
		this.addField(this.textMaxTutoredStage2);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.format("%.2f", this.config.getMinimumScore()));
		this.checkRegisterProposal.setValue(this.config.isRegisterProposal());
		this.checkShowGradesToStudent.setValue(this.config.isShowGradesToStudent());
		this.comboSupervisorFilter.setValue(this.config.getSupervisorFilter());
		this.comboCosupervisorFilter.setValue(this.config.getCosupervisorFilter());
		this.textSupervisorIndicator.setYear(this.config.getSupervisorIndication());
		this.textMaxTutoredStage1.setYear(this.config.getMaxTutoredStage1());
		this.textMaxTutoredStage2.setYear(this.config.getMaxTutoredStage2());
	}

	@Override
	public void save() {
		try{
			SigetConfigBO bo = new SigetConfigBO();
			
			this.config.setMinimumScore(Double.parseDouble(this.textMinimumScore.getValue().replace(",", ".")));
			this.config.setRegisterProposal(this.checkRegisterProposal.getValue());
			this.config.setShowGradesToStudent(this.checkShowGradesToStudent.getValue());
			this.config.setSupervisorFilter((SupervisorFilter)this.comboSupervisorFilter.getValue());
			this.config.setCosupervisorFilter((SupervisorFilter)this.comboCosupervisorFilter.getValue());
			this.config.setSupervisorIndication(this.textSupervisorIndicator.getYear());
			this.config.setMaxTutoredStage1(this.textMaxTutoredStage1.getYear());
			this.config.setMaxTutoredStage2(this.textMaxTutoredStage2.getYear());
			
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
