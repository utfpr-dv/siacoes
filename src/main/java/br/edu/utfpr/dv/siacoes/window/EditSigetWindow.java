package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.ByteSizeField;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.AttendanceFrequency;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigetWindow extends EditWindow {
	
	private final SigetConfig config;
	
	private final TextField textMinimumScore;
	private final CheckBox checkRegisterProposal;
	private final CheckBox checkShowGradesToStudent;
	private final NativeSelect comboSupervisorFilter;
	private final NativeSelect comboCosupervisorFilter;
	private final TextField textSupervisorIndication;
	private final TextField textMaxTutoredStage1;
	private final TextField textMaxTutoredStage2;
	private final CheckBox checkRequestFinalDocumentStage1;
	private final TextField textRepositoryLink;
	private final CheckBox checkSupervisorJuryRequest;
	private final CheckBox checkSupervisorAgreement;
	private final CheckBox checkSupervisorJuryAgreement;
	private final CheckBox checkValidateAttendances;
	private final NativeSelect comboAttendanceFrequency;
	private final ByteSizeField textMaxFileSize;
	private final TextField textMinimumJuryMembers;
	private final TextField textMinimumJurySubstitutes;
	private final TextField textJuryTimeStage1;
	private final TextField textJuryTimeStage2;
	private final CheckBox checkSupervisorAssignsGrades;
	private final CheckBox checkUseDigitalSignature;
	private final CheckBox checkAppraiserFillsGrades;
	
	private final TabSheet tab;
	
	public EditSigetWindow(SigetConfig config, ListView parentView){
		super("Editar Configurações", parentView);
		
		this.config = config;
		
		this.tab = new TabSheet();
		this.tab.setHeight("315px");
		this.tab.setWidth("900px");
		
		this.textMinimumScore = new TextField("Nota mínima para aprovação");
		this.textMinimumScore.setWidth("100px");
		
		this.checkRegisterProposal = new CheckBox("O acadêmico deve registrar a proposta para TCC1");
		
		this.checkShowGradesToStudent = new CheckBox("Permitir que o acadêmico visualize as notas atribuídas pela banca");
		
		this.comboSupervisorFilter = new NativeSelect("Orientador deve pertencer ao");
		this.comboSupervisorFilter.setWidth("400px");
		this.comboSupervisorFilter.setNullSelectionAllowed(false);
		this.comboSupervisorFilter.addItem(SupervisorFilter.DEPARTMENT);
		this.comboSupervisorFilter.addItem(SupervisorFilter.CAMPUS);
		this.comboSupervisorFilter.addItem(SupervisorFilter.INSTITUTION);
		this.comboSupervisorFilter.addItem(SupervisorFilter.EVERYONE);
		
		this.comboCosupervisorFilter = new NativeSelect("Coorientador deve pertencer ao");
		this.comboCosupervisorFilter.setWidth("400px");
		this.comboCosupervisorFilter.setNullSelectionAllowed(false);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.DEPARTMENT);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.CAMPUS);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.INSTITUTION);
		this.comboCosupervisorFilter.addItem(SupervisorFilter.EVERYONE);
		
		this.textSupervisorIndication = new TextField("Número de indicações do orientador para Proposta de TCC 1");
		this.textSupervisorIndication.setWidth("100px");
		
		this.textMaxTutoredStage1 = new TextField("Número máximo de orientados para TCC 1");
		this.textMaxTutoredStage1.setWidth("100px");
		
		this.textMaxTutoredStage2 = new TextField("Número máximo de orientados para TCC 2");
		this.textMaxTutoredStage2.setWidth("100px");
		
		this.checkRequestFinalDocumentStage1 = new CheckBox("Exigir envio da versão final para TCC 1");
		
		this.textRepositoryLink = new TextField("Link para o repositório de TCCs");
		this.textRepositoryLink.setWidth("810px");
		this.textRepositoryLink.setMaxLength(255);
		
		this.checkSupervisorJuryRequest = new CheckBox("Permitir que o orientador monte a composição da banca");
		
		this.checkSupervisorAgreement = new CheckBox("Exigir termo de concordância de orientação");
		
		this.checkSupervisorJuryAgreement = new CheckBox("Exigir parecer do orientador para a apresentação do trabalho em banca pública");
		
		this.checkValidateAttendances = new CheckBox("Validar reuniões de orientação para submissão de TCC 1 e TCC 2");
		
		this.comboAttendanceFrequency = new NativeSelect("Frequência de Reuniões");
		this.comboAttendanceFrequency.setWidth("400px");
		this.comboAttendanceFrequency.setNullSelectionAllowed(false);
		this.comboAttendanceFrequency.addItem(AttendanceFrequency.WEEKLY);
		this.comboAttendanceFrequency.addItem(AttendanceFrequency.BIWEEKLY);
		this.comboAttendanceFrequency.addItem(AttendanceFrequency.MONTHLY);
		this.comboAttendanceFrequency.addItem(AttendanceFrequency.BIMONTHLY);
		this.comboAttendanceFrequency.addItem(AttendanceFrequency.QUARTERLY);
		
		this.textMaxFileSize = new ByteSizeField("Tamanho máximo para submissão de arquivos");
		
		this.textMinimumJuryMembers = new TextField("Número mínimo de membros titulares na banca (exceto o presidente)");
		this.textMinimumJuryMembers.setWidth("100px");
		
		this.textMinimumJurySubstitutes = new TextField("Número mínimo de suplentes na banca");
		this.textMinimumJurySubstitutes.setWidth("100px");
		
		this.textJuryTimeStage1 = new TextField("Duração da banca de TCC 1 (minutos)");
		this.textJuryTimeStage1.setWidth("100px");
		
		this.textJuryTimeStage2 = new TextField("Duração da banca de TCC 2 (minutos)");
		this.textJuryTimeStage2.setWidth("100px");
		
		this.checkAppraiserFillsGrades = new CheckBox("Permitir que os membros da banca cadastrem as notas no sistema");
		
		this.checkSupervisorAssignsGrades = new CheckBox("Presidente da banca atribui nota");
		
		this.checkUseDigitalSignature = new CheckBox("Usar assinatura digital");
		
		GridLayout g1 = new GridLayout(2, 2);
		g1.setSpacing(true);
		g1.addComponent(this.comboSupervisorFilter, 0, 0);
		g1.addComponent(this.comboCosupervisorFilter, 1, 0);
		g1.addComponent(this.textMaxTutoredStage1, 0, 1);
		g1.addComponent(this.textMaxTutoredStage2, 1, 1);
		VerticalLayout v4 = new VerticalLayout(g1);
		v4.setMargin(true);
		
		this.tab.addTab(v4, "Orientação");
		
		VerticalLayout v2 = new VerticalLayout(this.checkSupervisorAgreement, this.checkRegisterProposal, this.checkRequestFinalDocumentStage1);
		v2.setSpacing(true);
		HorizontalLayout h2 = new HorizontalLayout(v2, this.textSupervisorIndication);
		h2.setSpacing(true);
		h2.setMargin(true);
		
		this.tab.addTab(h2, "TCC 1");
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboAttendanceFrequency, this.checkValidateAttendances);
		h3.setSpacing(true);
		VerticalLayout v5 = new VerticalLayout(h3, this.textMaxFileSize);
		v5.setSpacing(true);
		v5.setMargin(true);
		
		this.tab.addTab(v5, "Submissão de TCC 1 e TCC 2");
		
		HorizontalLayout h1 = new HorizontalLayout(this.textMinimumScore, this.textJuryTimeStage1, this.textJuryTimeStage2);
		h1.setSpacing(true);
		HorizontalLayout h4 = new HorizontalLayout(this.textMinimumJuryMembers, this.textMinimumJurySubstitutes);
		h4.setSpacing(true);
		VerticalLayout v1 = new VerticalLayout(h1, h4, this.checkShowGradesToStudent, this.checkSupervisorJuryAgreement, this.checkSupervisorJuryRequest, this.checkAppraiserFillsGrades, this.checkSupervisorAssignsGrades);
		v1.setSpacing(true);
		v1.setMargin(true);
		
		this.tab.addTab(v1, "Banca");
		
		VerticalLayout v3 = new VerticalLayout(this.textRepositoryLink);
		v3.setSpacing(true);
		v3.setMargin(true);
		
		this.tab.addTab(v3, "Biblioteca");
		
		VerticalLayout v6 = new VerticalLayout(this.checkUseDigitalSignature);
		v6.setSpacing(true);
		v6.setMargin(true);
		
		this.tab.addTab(v6, "Documentos");
		
		this.addField(this.tab);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.format("%.2f", this.config.getMinimumScore()));
		this.checkRegisterProposal.setValue(this.config.isRegisterProposal());
		this.checkShowGradesToStudent.setValue(this.config.isShowGradesToStudent());
		this.comboSupervisorFilter.setValue(this.config.getSupervisorFilter());
		this.comboCosupervisorFilter.setValue(this.config.getCosupervisorFilter());
		this.textSupervisorIndication.setValue(String.valueOf(this.config.getSupervisorIndication()));
		this.textMaxTutoredStage1.setValue(String.valueOf(this.config.getMaxTutoredStage1()));
		this.textMaxTutoredStage2.setValue(String.valueOf(this.config.getMaxTutoredStage2()));
		this.checkRequestFinalDocumentStage1.setValue(this.config.isRequestFinalDocumentStage1());
		this.textRepositoryLink.setValue(this.config.getRepositoryLink());
		this.checkSupervisorJuryRequest.setValue(this.config.isSupervisorJuryRequest());
		this.checkSupervisorAgreement.setValue(this.config.isSupervisorAgreement());
		this.checkSupervisorJuryAgreement.setValue(this.config.isSupervisorJuryAgreement());
		this.checkValidateAttendances.setValue(this.config.isValidateAttendances());
		this.comboAttendanceFrequency.setValue(this.config.getAttendanceFrequency());
		this.textMaxFileSize.setValue(this.config.getMaxFileSize());
		this.textMinimumJuryMembers.setValue(String.valueOf(this.config.getMinimumJuryMembers()));
		this.textMinimumJurySubstitutes.setValue(String.valueOf(this.config.getMinimumJurySubstitutes()));
		this.textJuryTimeStage1.setValue(String.valueOf(this.config.getJuryTimeStage1()));
		this.textJuryTimeStage2.setValue(String.valueOf(this.config.getJuryTimeStage2()));
		this.checkSupervisorAssignsGrades.setValue(this.config.isSupervisorAssignsGrades());
		this.checkUseDigitalSignature.setValue(this.config.isUseDigitalSignature());
		this.checkAppraiserFillsGrades.setValue(this.config.isAppraiserFillsGrades());
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
			this.config.setSupervisorIndication(Integer.parseInt(this.textSupervisorIndication.getValue()));
			this.config.setMaxTutoredStage1(Integer.parseInt(this.textMaxTutoredStage1.getValue()));
			this.config.setMaxTutoredStage2(Integer.parseInt(this.textMaxTutoredStage2.getValue()));
			this.config.setRequestFinalDocumentStage1(this.checkRequestFinalDocumentStage1.getValue());
			this.config.setRepositoryLink(this.textRepositoryLink.getValue());
			this.config.setSupervisorJuryRequest(this.checkSupervisorJuryRequest.getValue());
			this.config.setSupervisorAgreement(this.checkSupervisorAgreement.getValue());
			this.config.setSupervisorJuryAgreement(this.checkSupervisorJuryAgreement.getValue());
			this.config.setValidateAttendances(this.checkValidateAttendances.getValue());
			this.config.setAttendanceFrequency((AttendanceFrequency)this.comboAttendanceFrequency.getValue());
			this.config.setMaxFileSize((int)this.textMaxFileSize.getValue());
			this.config.setMinimumJuryMembers(Integer.parseInt(this.textMinimumJuryMembers.getValue()));
			this.config.setMinimumJurySubstitutes(Integer.parseInt(this.textMinimumJurySubstitutes.getValue()));
			this.config.setJuryTimeStage1(Integer.parseInt(this.textJuryTimeStage1.getValue()));
			this.config.setJuryTimeStage2(Integer.parseInt(this.textJuryTimeStage2.getValue()));
			this.config.setSupervisorAssignsGrades(this.checkSupervisorAssignsGrades.getValue());
			this.config.setUseDigitalSignature(this.checkUseDigitalSignature.getValue());
			this.config.setAppraiserFillsGrades(this.checkAppraiserFillsGrades.getValue());
			
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
