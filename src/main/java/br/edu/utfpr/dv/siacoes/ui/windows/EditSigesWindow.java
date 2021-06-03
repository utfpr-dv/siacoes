package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ReminderConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ReminderConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.model.SigesConfig.JuryFormat;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.ui.components.ByteSizeField;
import br.edu.utfpr.dv.siacoes.ui.components.ReminderConfigField;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditSigesWindow extends EditWindow {
	
	private final SigesConfig config;
	
	private final NumberField textMinimumScore;
	private final NumberField textSupervisorPonderosity;
	private final NumberField textCompanySupervisorPonderosity;
	private final Checkbox checkShowGradesToStudent;
	private final Select<SupervisorFilter> comboSupervisorFilter;
	private final Checkbox checkSupervisorFillJuryForm;
	private final ByteSizeField textMaxFileSize;
	private final IntegerField textJuryTime;
	private final Checkbox checkFillOnlyTotalHours;
	private final Select<JuryFormat> comboJuryFormat;
	private final Checkbox checkUseDigitalSignature;
	private final Checkbox checkAppraiserFillsGrades;
	private final IntegerField textMinimumJuryMembers;
	private final IntegerField textMinimumJurySubstitutes;
	private final Checkbox checkUseSei;
	private final Checkbox checkStudentRequestJury;
	private final Checkbox checkUseEvaluationItems;
	private final VerticalLayout layoutReminders;
	
	private final Tabs tab;
	
	public EditSigesWindow(SigesConfig config, ListView parentView){
		super("Editar Configurações", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new NumberField("Pontuação mínima para aprovação");
		this.textMinimumScore.setWidth("250px");
		
		this.textSupervisorPonderosity = new NumberField("Peso da Nota do Orientador");
		this.textSupervisorPonderosity.setWidth("200px");
		
		this.textCompanySupervisorPonderosity = new NumberField("Peso da Nota do Supervisor na Empresa");
		this.textCompanySupervisorPonderosity.setWidth("270px");
		
		this.checkShowGradesToStudent = new Checkbox("Permitir que o acadêmico visualize as notas atribuídas pela banca");
		
		this.checkSupervisorFillJuryForm = new Checkbox("O orientador deverá preencher a mesma ficha de avaliação da banca");
		
		this.comboSupervisorFilter = new Select<SupervisorFilter>();
		this.comboSupervisorFilter.setLabel("Orientador deve pertencer ao");
		this.comboSupervisorFilter.setWidth("400px");
		this.comboSupervisorFilter.setItems(SupervisorFilter.DEPARTMENT, SupervisorFilter.CAMPUS, SupervisorFilter.INSTITUTION, SupervisorFilter.EVERYONE);
		
		this.textMaxFileSize = new ByteSizeField("Tamanho máximo para submissão de arquivos");
		
		this.comboJuryFormat = new Select<JuryFormat>();
		this.comboJuryFormat.setLabel("Formato da Banca");
		this.comboJuryFormat.setWidth("320px");
		this.comboJuryFormat.setItems(JuryFormat.INDIVIDUAL, JuryFormat.SESSION);
		this.comboJuryFormat.setValue(JuryFormat.INDIVIDUAL);
		
		this.textJuryTime = new IntegerField("Duração da banca (minutos)");
		this.textJuryTime.setWidth("200px");
		
		this.checkFillOnlyTotalHours = new Checkbox("Informar apenas o total de horas do estágio");
		
		this.checkAppraiserFillsGrades = new Checkbox("Permitir que os membros da banca cadastrem as notas no sistema");
		
		this.checkUseDigitalSignature = new Checkbox("Usar assinatura digital");
		
		this.checkUseSei = new Checkbox("Indicar processo no SEI");
		
		this.checkStudentRequestJury = new Checkbox("Acadêmico pode solicitar banca");
		
		this.checkUseEvaluationItems = new Checkbox("Utilizar quesitos de avaliação para compor a nota");
		
		this.textMinimumJuryMembers = new IntegerField("Número mínimo de membros titulares na banca (exceto o presidente)");
		this.textMinimumJuryMembers.setWidth("450px");
		
		this.textMinimumJurySubstitutes = new IntegerField("Número mínimo de suplentes na banca");
		this.textMinimumJurySubstitutes.setWidth("260px");
		
		VerticalLayout v1 = new VerticalLayout(this.comboSupervisorFilter, this.checkFillOnlyTotalHours, this.checkUseSei);
		v1.setSpacing(false);
		v1.setMargin(false);
		v1.setPadding(false);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textMinimumScore, this.textSupervisorPonderosity, this.textCompanySupervisorPonderosity);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		HorizontalLayout h2 = new HorizontalLayout(this.comboJuryFormat, this.textJuryTime);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		HorizontalLayout h3 = new HorizontalLayout(this.textMinimumJuryMembers, this.textMinimumJurySubstitutes);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		VerticalLayout v2 = new VerticalLayout(h1, h2, h3, this.checkUseEvaluationItems, this.checkSupervisorFillJuryForm, this.checkAppraiserFillsGrades, this.checkShowGradesToStudent, this.checkStudentRequestJury);
		v2.setSpacing(false);
		v2.setMargin(false);
		v2.setPadding(false);
		v2.setVisible(false);
		
		VerticalLayout v3 = new VerticalLayout(this.textMaxFileSize);
		v3.setSpacing(false);
		v3.setMargin(false);
		v3.setPadding(false);
		v3.setVisible(false);
		
		VerticalLayout v4 = new VerticalLayout(this.checkUseDigitalSignature);
		v4.setSpacing(false);
		v4.setMargin(false);
		v4.setPadding(false);
		v4.setVisible(false);
		
		this.layoutReminders = new VerticalLayout();
		this.layoutReminders.setSpacing(false);
		this.layoutReminders.setMargin(false);
		this.layoutReminders.setPadding(false);
		this.layoutReminders.setVisible(false);
		
		Tab tab1 = new Tab("Registro de Estágio");
		Tab tab2 = new Tab("Banca");
		Tab tab3 = new Tab("Submissão de Arquivos");
		Tab tab4 = new Tab("Documentos");
		Tab tab5 = new Tab("Lembretes");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, v1);
		tabsToPages.put(tab2, v2);
		tabsToPages.put(tab3, v3);
		tabsToPages.put(tab4, v4);
		tabsToPages.put(tab5, this.layoutReminders);
		Div pages = new Div(v1, v2, v3, v4, this.layoutReminders);
		
		this.tab = new Tabs(tab1, tab2, tab3, tab4, tab5);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(tab1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("900px");
		layout.setHeight("460px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(this.config.getMinimumScore());
		this.textSupervisorPonderosity.setValue(this.config.getSupervisorPonderosity());
		this.textCompanySupervisorPonderosity.setValue(this.config.getCompanySupervisorPonderosity());
		this.checkShowGradesToStudent.setValue(this.config.isShowGradesToStudent());
		this.comboSupervisorFilter.setValue(this.config.getSupervisorFilter());
		this.checkSupervisorFillJuryForm.setValue(this.config.isSupervisorFillJuryForm());
		this.textMaxFileSize.setValue(this.config.getMaxFileSize());
		this.textJuryTime.setValue(this.config.getJuryTime());
		this.checkFillOnlyTotalHours.setValue(this.config.isFillOnlyTotalHours());
		this.comboJuryFormat.setValue(this.config.getJuryFormat());
		this.checkUseDigitalSignature.setValue(this.config.isUseDigitalSignature());
		this.checkAppraiserFillsGrades.setValue(this.config.isAppraiserFillsGrades());
		this.textMinimumJuryMembers.setValue(this.config.getMinimumJuryMembers());
		this.textMinimumJurySubstitutes.setValue(this.config.getMinimumJurySubstitutes());
		this.checkUseSei.setValue(this.config.isUseSei());
		this.checkStudentRequestJury.setValue(this.config.isStudentRequestJury());
		this.checkUseEvaluationItems.setValue(this.config.isUseEvaluationItems());
		
		this.loadReminders();
	}
	
	private void loadReminders() {
		try {
			List<ReminderConfig> list = new ReminderConfigBO().list(this.config.getDepartment().getIdDepartment(), SystemModule.SIGES);
			this.layoutReminders.removeAll();
			
			for(ReminderConfig config : list) {
				ReminderConfigField field = new ReminderConfigField(config);
				
				this.layoutReminders.add(field);
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Lembretes", e.getMessage());
		}
	}

	@Override
	public void save() {
		try{
			SigesConfigBO bo = new SigesConfigBO();
			
			this.config.setMinimumScore(this.textMinimumScore.getValue());
			this.config.setSupervisorPonderosity(this.textSupervisorPonderosity.getValue());
			this.config.setCompanySupervisorPonderosity(this.textCompanySupervisorPonderosity.getValue());
			this.config.setShowGradesToStudent(this.checkShowGradesToStudent.getValue());
			this.config.setSupervisorFilter(this.comboSupervisorFilter.getValue());
			this.config.setSupervisorFillJuryForm(this.checkSupervisorFillJuryForm.getValue());
			this.config.setMaxFileSize(this.textMaxFileSize.getValue().intValue());
			this.config.setJuryTime(this.textJuryTime.getValue());
			this.config.setFillOnlyTotalHours(this.checkFillOnlyTotalHours.getValue());
			this.config.setJuryFormat(this.comboJuryFormat.getValue());
			this.config.setUseDigitalSignature(this.checkUseDigitalSignature.getValue());
			this.config.setAppraiserFillsGrades(this.checkAppraiserFillsGrades.getValue());
			this.config.setMinimumJuryMembers(this.textMinimumJuryMembers.getValue());
			this.config.setMinimumJurySubstitutes(this.textMinimumJurySubstitutes.getValue());
			this.config.setUseSei(this.checkUseSei.getValue());
			this.config.setStudentRequestJury(this.checkStudentRequestJury.getValue());
			this.config.setUseEvaluationItems(this.checkUseEvaluationItems.getValue());
			
			bo.save(Session.getIdUserLog(), this.config);
			
			this.saveReminders();
			
			this.showSuccessNotification("Salvar Configurações", "Configurações salvas com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Configurações", e.getMessage());
		}
	}
	
	private void saveReminders() throws Exception {
		Object fields[] = this.layoutReminders.getChildren().filter(child -> {
			if(child instanceof ReminderConfigField) {
				return true;
			} else {
				return false;
			}
		}).toArray();
		
		for(Object field : fields) {
			ReminderConfig config = ((ReminderConfigField)field).getValue();
			
			new ReminderConfigBO().save(Session.getIdUserLog(), config);
		}
	}

}
