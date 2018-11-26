package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.components.ByteSizeField;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigesWindow extends EditWindow {
	
	private final SigesConfig config;
	
	private final TextField textMinimumScore;
	private final TextField textSupervisorPonderosity;
	private final TextField textCompanySupervisorPonderosity;
	private final CheckBox checkShowGradesToStudent;
	private final NativeSelect comboSupervisorFilter;
	private final CheckBox checkSupervisorFillJuryForm;
	private final ByteSizeField textMaxFileSize;
	
	private final TabSheet tab;
	
	public EditSigesWindow(SigesConfig config, ListView parentView){
		super("Editar Configurações", parentView);
		
		this.config = config;
		
		this.tab = new TabSheet();
		this.tab.setHeight("250px");
		this.tab.setWidth("900px");
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.textSupervisorPonderosity = new TextField("Peso da Nota do Orientador");
		this.textSupervisorPonderosity.setWidth("100px");
		
		this.textCompanySupervisorPonderosity = new TextField("Peso da Nota do Supervisor na Empresa");
		this.textCompanySupervisorPonderosity.setWidth("100px");
		
		this.checkShowGradesToStudent = new CheckBox("Permitir que o acadêmico visualize as notas atribuídas pela banca");
		
		this.checkSupervisorFillJuryForm = new CheckBox("O orientador deverá preencher a mesma ficha de avaliação da banca");
		
		this.comboSupervisorFilter = new NativeSelect("Orientador deve pertencer ao");
		this.comboSupervisorFilter.setWidth("400px");
		this.comboSupervisorFilter.setNullSelectionAllowed(false);
		this.comboSupervisorFilter.addItem(SupervisorFilter.DEPARTMENT);
		this.comboSupervisorFilter.addItem(SupervisorFilter.CAMPUS);
		this.comboSupervisorFilter.addItem(SupervisorFilter.INSTITUTION);
		this.comboSupervisorFilter.addItem(SupervisorFilter.EVERYONE);
		
		this.textMaxFileSize = new ByteSizeField("Tamanho máximo para submissão de arquivos");
		
		VerticalLayout v1 = new VerticalLayout(this.comboSupervisorFilter);
		v1.setSpacing(true);
		v1.setMargin(true);
		
		this.tab.addTab(v1, "Orientação");
		
		HorizontalLayout h1 = new HorizontalLayout(this.textMinimumScore, this.textSupervisorPonderosity, this.textCompanySupervisorPonderosity);
		h1.setSpacing(true);
		VerticalLayout v2 = new VerticalLayout(h1, this.checkSupervisorFillJuryForm, this.checkShowGradesToStudent);
		v2.setSpacing(true);
		v2.setMargin(true);
		
		this.tab.addTab(v2, "Banca");
		
		VerticalLayout v3 = new VerticalLayout(this.textMaxFileSize);
		v3.setSpacing(true);
		v3.setMargin(true);
		
		this.tab.addTab(v3, "Submissão de Arquivos");
		
		this.addField(this.tab);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.format("%.2f", this.config.getMinimumScore()));
		this.textSupervisorPonderosity.setValue(String.format("%.2f", this.config.getSupervisorPonderosity()));
		this.textCompanySupervisorPonderosity.setValue(String.format("%.2f", this.config.getCompanySupervisorPonderosity()));
		this.checkShowGradesToStudent.setValue(this.config.isShowGradesToStudent());
		this.comboSupervisorFilter.setValue(this.config.getSupervisorFilter());
		this.checkSupervisorFillJuryForm.setValue(this.config.isSupervisorFillJuryForm());
		this.textMaxFileSize.setValue(this.config.getMaxFileSize());
	}

	@Override
	public void save() {
		try{
			SigesConfigBO bo = new SigesConfigBO();
			
			this.config.setMinimumScore(Double.parseDouble(this.textMinimumScore.getValue().replace(",", ".")));
			this.config.setSupervisorPonderosity(Double.parseDouble(this.textSupervisorPonderosity.getValue().replace(",", ".")));
			this.config.setCompanySupervisorPonderosity(Double.parseDouble(this.textCompanySupervisorPonderosity.getValue().replace(",", ".")));
			this.config.setShowGradesToStudent(this.checkShowGradesToStudent.getValue());
			this.config.setSupervisorFilter((SupervisorFilter)this.comboSupervisorFilter.getValue());
			this.config.setSupervisorFillJuryForm(this.checkSupervisorFillJuryForm.getValue());
			this.config.setMaxFileSize((int)this.textMaxFileSize.getValue());
			
			bo.save(this.config);
			
			this.showSuccessNotification("Salvar Configurações", "Configurações salvas com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Configurações", e.getMessage());
		}
	}

}
