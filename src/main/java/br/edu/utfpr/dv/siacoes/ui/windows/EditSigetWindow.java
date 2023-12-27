package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
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
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.AsyncJury;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.AttendanceFrequency;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.ui.components.ByteSizeField;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditSigetWindow extends EditWindow {
	
	private final SigetConfig config;
	
	private final NumberField textMinimumScore;
	private final Checkbox checkRegisterProposal;
	private final Checkbox checkShowGradesToStudent;
	private final Select<SupervisorFilter> comboSupervisorFilter;
	private final Select<SupervisorFilter> comboCosupervisorFilter;
	private final IntegerField textSupervisorIndication;
	private final IntegerField textMaxTutoredStage1;
	private final IntegerField textMaxTutoredStage2;
	private final Checkbox checkRequestFinalDocumentStage1;
	private final TextField textRepositoryLink;
	private final Checkbox checkSupervisorJuryRequest;
	private final Checkbox checkSupervisorAgreement;
	private final Checkbox checkSupervisorJuryAgreement;
	private final Checkbox checkValidateAttendances;
	private final Select<AttendanceFrequency> comboAttendanceFrequency;
	private final ByteSizeField textMaxFileSize;
	private final IntegerField textMinimumJuryMembers;
	private final IntegerField textMinimumJurySubstitutes;
	private final IntegerField textJuryTimeStage1;
	private final IntegerField textJuryTimeStage2;
	private final Checkbox checkSupervisorAssignsGrades;
	private final Checkbox checkUseDigitalSignature;
	private final Checkbox checkAppraiserFillsGrades;
	private final Checkbox checkUseSei;
	private final Checkbox checkAllowAsyncJury;
	private final StageComboBox comboAllowAsyncJury; 
	
	private final Tabs tab;
	
	public EditSigetWindow(SigetConfig config, ListView parentView){
		super("Editar Configurações", parentView);
		
		this.config = config;
		
		this.textMinimumScore = new NumberField("Nota mínima para aprovação");
		this.textMinimumScore.setWidth("250px");
		
		this.checkRegisterProposal = new Checkbox("O acadêmico deve registrar a proposta para TCC1");
		
		this.checkShowGradesToStudent = new Checkbox("Permitir que o acadêmico visualize as notas atribuídas pela banca");
		
		this.comboSupervisorFilter = new Select<SupervisorFilter>();
		this.comboSupervisorFilter.setLabel("Orientador deve pertencer ao");
		this.comboSupervisorFilter.setWidth("400px");
		this.comboSupervisorFilter.setItems(SupervisorFilter.DEPARTMENT, SupervisorFilter.CAMPUS, SupervisorFilter.INSTITUTION, SupervisorFilter.EVERYONE);
		
		this.comboCosupervisorFilter = new Select<SupervisorFilter>();
		this.comboCosupervisorFilter.setLabel("Coorientador deve pertencer ao");
		this.comboCosupervisorFilter.setWidth("400px");
		this.comboCosupervisorFilter.setItems(SupervisorFilter.DEPARTMENT, SupervisorFilter.CAMPUS, SupervisorFilter.INSTITUTION, SupervisorFilter.EVERYONE);
		
		this.textSupervisorIndication = new IntegerField("Número de indicações do orientador para Proposta de TCC 1");
		this.textSupervisorIndication.setWidth("400px");
		
		this.textMaxTutoredStage1 = new IntegerField("Número máximo de orientados para TCC 1");
		this.textMaxTutoredStage1.setWidth("280px");
		
		this.textMaxTutoredStage2 = new IntegerField("Número máximo de orientados para TCC 2");
		this.textMaxTutoredStage2.setWidth("280px");
		
		this.checkRequestFinalDocumentStage1 = new Checkbox("Exigir envio da versão final para TCC 1");
		
		this.textRepositoryLink = new TextField("Link para o repositório de TCCs");
		this.textRepositoryLink.setWidth("810px");
		this.textRepositoryLink.setMaxLength(255);
		
		this.checkSupervisorJuryRequest = new Checkbox("Permitir que o orientador monte a composição da banca");
		
		this.checkSupervisorAgreement = new Checkbox("Exigir termo de concordância de orientação");
		
		this.checkSupervisorJuryAgreement = new Checkbox("Exigir parecer do orientador para a apresentação do trabalho em banca pública");
		
		this.checkValidateAttendances = new Checkbox("Validar reuniões de orientação para submissão de TCC 1 e TCC 2");
		
		this.comboAttendanceFrequency = new Select<AttendanceFrequency>();
		this.comboAttendanceFrequency.setLabel("Frequência de Reuniões");
		this.comboAttendanceFrequency.setWidth("400px");
		this.comboAttendanceFrequency.setItems(AttendanceFrequency.WEEKLY, AttendanceFrequency.BIWEEKLY, AttendanceFrequency.MONTHLY, AttendanceFrequency.BIMONTHLY, AttendanceFrequency.QUARTERLY);
		
		this.textMaxFileSize = new ByteSizeField("Tamanho máximo para submissão de arquivos");
		
		this.textMinimumJuryMembers = new IntegerField("Número mínimo de membros titulares na banca (exceto o presidente)");
		this.textMinimumJuryMembers.setWidth("450px");
		
		this.textMinimumJurySubstitutes = new IntegerField("Número mínimo de suplentes na banca");
		this.textMinimumJurySubstitutes.setWidth("250px");
		
		this.textJuryTimeStage1 = new IntegerField("Duração da banca de TCC 1 (minutos)");
		this.textJuryTimeStage1.setWidth("250px");
		
		this.textJuryTimeStage2 = new IntegerField("Duração da banca de TCC 2 (minutos)");
		this.textJuryTimeStage2.setWidth("250px");
		
		this.checkAppraiserFillsGrades = new Checkbox("Permitir que os membros da banca cadastrem as notas no sistema");
		
		this.checkSupervisorAssignsGrades = new Checkbox("Presidente da banca atribui nota");
		
		this.checkUseDigitalSignature = new Checkbox("Usar assinatura digital");
		
		this.checkUseSei = new Checkbox("Indicar processo no SEI");
		
		this.checkAllowAsyncJury = new Checkbox("Permitir bancas assíncronas para TCC");
		
		this.comboAllowAsyncJury = new StageComboBox(true);
		this.comboAllowAsyncJury.setLabel("");
		
		HorizontalLayout layoutAsyncJury = new HorizontalLayout(this.checkAllowAsyncJury, this.comboAllowAsyncJury);
		
		VerticalLayout vl1 = new VerticalLayout(this.comboSupervisorFilter, this.textMaxTutoredStage1);
		vl1.setSpacing(false);
		vl1.setMargin(false);
		vl1.setPadding(false);
		VerticalLayout vl2 = new VerticalLayout(this.comboCosupervisorFilter, this.textMaxTutoredStage2);
		vl2.setSpacing(false);
		vl2.setMargin(false);
		vl2.setPadding(false);
		
		HorizontalLayout hl1 = new HorizontalLayout(vl1, vl2);
		hl1.setSpacing(true);
		hl1.setMargin(false);
		hl1.setPadding(false);
		
		VerticalLayout v2 = new VerticalLayout(this.checkSupervisorAgreement, this.checkRegisterProposal, this.checkRequestFinalDocumentStage1, this.textSupervisorIndication);
		v2.setSpacing(false);
		v2.setPadding(false);
		v2.setMargin(false);
		v2.setVisible(false);
		
		VerticalLayout v5 = new VerticalLayout(this.comboAttendanceFrequency, this.checkValidateAttendances, this.textMaxFileSize);
		v5.setSpacing(false);
		v5.setMargin(false);
		v5.setPadding(false);
		v5.setVisible(false);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textMinimumScore, this.textJuryTimeStage1, this.textJuryTimeStage2);
		h1.setSpacing(true);
		h1.setPadding(false);
		h1.setMargin(false);
		HorizontalLayout h4 = new HorizontalLayout(this.textMinimumJuryMembers, this.textMinimumJurySubstitutes);
		h4.setSpacing(true);
		h4.setPadding(false);
		h4.setMargin(false);
		VerticalLayout v1 = new VerticalLayout(h1, h4, layoutAsyncJury, this.checkShowGradesToStudent, this.checkSupervisorJuryAgreement, this.checkSupervisorJuryRequest, this.checkAppraiserFillsGrades, this.checkSupervisorAssignsGrades, this.checkUseSei);
		v1.setSpacing(false);
		v1.setMargin(false);
		v1.setPadding(false);
		v1.setVisible(false);
		
		VerticalLayout v3 = new VerticalLayout(this.textRepositoryLink);
		v3.setSpacing(false);
		v3.setMargin(false);
		v3.setPadding(false);
		v3.setVisible(false);
		
		VerticalLayout v6 = new VerticalLayout(this.checkUseDigitalSignature);
		v6.setSpacing(false);
		v6.setMargin(false);
		v6.setPadding(false);
		v6.setVisible(false);
		
		Tab tab1 = new Tab("Orientação");
		Tab tab2 = new Tab("TCC 1");
		Tab tab3 = new Tab("Submissão de TCC 1 e TCC 2");
		Tab tab4 = new Tab("Banca");
		Tab tab5 = new Tab("Biblioteca");
		Tab tab6 = new Tab("Documentos");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1, hl1);
		tabsToPages.put(tab2, v2);
		tabsToPages.put(tab3, v5);
		tabsToPages.put(tab4, v1);
		tabsToPages.put(tab5, v3);
		tabsToPages.put(tab6, v6);
		Div pages = new Div(hl1, v2, v5, v1, v3, v6);
		
		this.tab = new Tabs(tab1, tab2, tab3, tab4, tab5, tab6);
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
		layout.setHeight("435px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(layout);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(this.config.getMinimumScore());
		this.checkRegisterProposal.setValue(this.config.isRegisterProposal());
		this.checkShowGradesToStudent.setValue(this.config.isShowGradesToStudent());
		this.comboSupervisorFilter.setValue(this.config.getSupervisorFilter());
		this.comboCosupervisorFilter.setValue(this.config.getCosupervisorFilter());
		this.textSupervisorIndication.setValue(this.config.getSupervisorIndication());
		this.textMaxTutoredStage1.setValue(this.config.getMaxTutoredStage1());
		this.textMaxTutoredStage2.setValue(this.config.getMaxTutoredStage2());
		this.checkRequestFinalDocumentStage1.setValue(this.config.isRequestFinalDocumentStage1());
		this.textRepositoryLink.setValue(this.config.getRepositoryLink());
		this.checkSupervisorJuryRequest.setValue(this.config.isSupervisorJuryRequest());
		this.checkSupervisorAgreement.setValue(this.config.isSupervisorAgreement());
		this.checkSupervisorJuryAgreement.setValue(this.config.isSupervisorJuryAgreement());
		this.checkValidateAttendances.setValue(this.config.isValidateAttendances());
		this.comboAttendanceFrequency.setValue(this.config.getAttendanceFrequency());
		this.textMaxFileSize.setValue(this.config.getMaxFileSize());
		this.textMinimumJuryMembers.setValue(this.config.getMinimumJuryMembers());
		this.textMinimumJurySubstitutes.setValue(this.config.getMinimumJurySubstitutes());
		this.textJuryTimeStage1.setValue(this.config.getJuryTimeStage1());
		this.textJuryTimeStage2.setValue(this.config.getJuryTimeStage2());
		this.checkSupervisorAssignsGrades.setValue(this.config.isSupervisorAssignsGrades());
		this.checkUseDigitalSignature.setValue(this.config.isUseDigitalSignature());
		this.checkAppraiserFillsGrades.setValue(this.config.isAppraiserFillsGrades());
		this.checkUseSei.setValue(this.config.isUseSei());
		
		if(this.config.getAllowAsyncJury() == AsyncJury.NONE) {
			this.checkAllowAsyncJury.setValue(false);
			this.comboAllowAsyncJury.setStage(1);
		} else {
			this.checkAllowAsyncJury.setValue(true);
			if(this.config.getAllowAsyncJury() == AsyncJury.BOTH) {
				this.comboAllowAsyncJury.selectBoth();
			} else {
				this.comboAllowAsyncJury.setStage(this.config.getAllowAsyncJury().getValue());
			}
		}
	}

	@Override
	public void save() {
		try{
			SigetConfigBO bo = new SigetConfigBO();
			
			this.config.setMinimumScore(this.textMinimumScore.getValue());
			this.config.setRegisterProposal(this.checkRegisterProposal.getValue());
			this.config.setShowGradesToStudent(this.checkShowGradesToStudent.getValue());
			this.config.setSupervisorFilter(this.comboSupervisorFilter.getValue());
			this.config.setCosupervisorFilter(this.comboCosupervisorFilter.getValue());
			this.config.setSupervisorIndication(this.textSupervisorIndication.getValue());
			this.config.setMaxTutoredStage1(this.textMaxTutoredStage1.getValue());
			this.config.setMaxTutoredStage2(this.textMaxTutoredStage2.getValue());
			this.config.setRequestFinalDocumentStage1(this.checkRequestFinalDocumentStage1.getValue());
			this.config.setRepositoryLink(this.textRepositoryLink.getValue());
			this.config.setSupervisorJuryRequest(this.checkSupervisorJuryRequest.getValue());
			this.config.setSupervisorAgreement(this.checkSupervisorAgreement.getValue());
			this.config.setSupervisorJuryAgreement(this.checkSupervisorJuryAgreement.getValue());
			this.config.setValidateAttendances(this.checkValidateAttendances.getValue());
			this.config.setAttendanceFrequency(this.comboAttendanceFrequency.getValue());
			this.config.setMaxFileSize(this.textMaxFileSize.getValue().intValue());
			this.config.setMinimumJuryMembers(this.textMinimumJuryMembers.getValue());
			this.config.setMinimumJurySubstitutes(this.textMinimumJurySubstitutes.getValue());
			this.config.setJuryTimeStage1(this.textJuryTimeStage1.getValue());
			this.config.setJuryTimeStage2(this.textJuryTimeStage2.getValue());
			this.config.setSupervisorAssignsGrades(this.checkSupervisorAssignsGrades.getValue());
			this.config.setUseDigitalSignature(this.checkUseDigitalSignature.getValue());
			this.config.setAppraiserFillsGrades(this.checkAppraiserFillsGrades.getValue());
			this.config.setUseSei(this.checkUseSei.getValue());
			
			if(this.checkAllowAsyncJury.getValue()) {
				if(this.comboAllowAsyncJury.isBothSelected()) {
					this.config.setAllowAsyncJury(AsyncJury.BOTH);
				} else {
					this.config.setAllowAsyncJury(AsyncJury.valueOf(this.comboAllowAsyncJury.getStage()));
				}
			} else {
				this.config.setAllowAsyncJury(AsyncJury.NONE);
			}
			
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
