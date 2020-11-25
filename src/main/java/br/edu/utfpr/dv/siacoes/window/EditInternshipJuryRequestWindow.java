package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryRequest;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipJuryRequestWindow extends EditWindow {

	private final InternshipJuryRequest jury;
	private final List<InternshipJuryAppraiserRequest> members;
	private final List<InternshipJuryAppraiserRequest> substitutes;
	private SigesConfig config;
	
	private final TabSheet tabContainer;
	private final TextField textStudent;
	private final TextField textCompany;
	private final DateField textDate;
	private final TextField textLocal;
	private final TextArea textComments;
	private final SupervisorComboBox comboChair;
	private final HorizontalLayout layoutAppraisers;
	private Grid gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonRemoveAppraiser;
	private final Button buttonAppraiserSchedule;
	private final HorizontalLayout layoutSubstitutes;
	private Grid gridSubstitutes;
	private final Button buttonAddSubstitute;
	private final Button buttonRemoveSubstitute;
	private final Button buttonSubstituteSchedule;
	
	public EditInternshipJuryRequestWindow(InternshipJuryRequest jury,  ListView parentView) {
		this(jury, true, parentView);
	}
	
	public EditInternshipJuryRequestWindow(InternshipJuryRequest jury, boolean allowEdit, ListView parentView) {
		super("Requisição de Banca", parentView);
		
		if(jury == null){
			this.jury = new InternshipJuryRequest();
		}else{
			this.jury = jury;
		}
		this.members = new ArrayList<InternshipJuryAppraiserRequest>();
		this.substitutes = new ArrayList<InternshipJuryAppraiserRequest>();
		try {
			if(this.jury.getIdInternshipJuryRequest() == 0) {
				this.config = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} else {
				this.config = new SigesConfigBO().findByDepartment(new InternshipJuryRequestBO().findIdDepartment(this.jury.getIdInternshipJuryRequest()));
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigesConfig();
		}
		
		this.tabContainer = new TabSheet();
		this.tabContainer.setWidth("810px");
		this.tabContainer.setHeight("520px");
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
		this.tabContainer.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		this.textStudent.setRequired(true);
		
		this.textCompany = new TextField("Empresa");
		this.textCompany.setWidth("800px");
		this.textCompany.setEnabled(false);
		this.textCompany.setRequired(true);
		
		this.textDate = new DateField("Data");
		this.textDate.setDateFormat("dd/MM/yyyy HH:mm");
		this.textDate.setResolution(Resolution.MINUTE);
		this.textDate.setRequired(true);
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("800px");
		this.textLocal.setMaxLength(100);
		this.textLocal.setRequired(true);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("150px");
		this.textComments.addStyleName("textscroll");
		
		HorizontalLayout h1 = new HorizontalLayout(this.textDate);
		h1.setSpacing(true);
		VerticalLayout tab1 = new VerticalLayout(this.textStudent, this.textCompany, h1, this.textLocal, this.textComments);
		tab1.setSpacing(true);
		this.tabContainer.addTab(tab1, "Informações da Banca");
		
		this.comboChair = new SupervisorComboBox("Presidente da Banca", Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.config.getSupervisorFilter());
		this.comboChair.setWidth("800px");
		this.comboChair.setRequired(true);
		this.comboChair.setEnabled(false);
		
		this.layoutAppraisers = new HorizontalLayout();
		this.layoutAppraisers.setSizeFull();
		
		this.buttonAddAppraiser = new Button("Adicionar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addAppraiser();
            }
        });
		this.buttonAddAppraiser.setIcon(FontAwesome.PLUS);
		this.buttonAddAppraiser.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonAddAppraiser.setWidth("100px");
		
		this.buttonAppraiserSchedule = new Button("Agenda", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	appraiserSchedule();
            }
        });
		this.buttonAppraiserSchedule.setIcon(FontAwesome.CALENDAR_O);
		this.buttonAppraiserSchedule.setWidth("100px");
		
		this.buttonRemoveAppraiser = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeAppraiser();
            }
        });
		this.buttonRemoveAppraiser.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveAppraiser.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonRemoveAppraiser.setWidth("100px");
		
		VerticalLayout v1 = new VerticalLayout(this.buttonAddAppraiser, this.buttonAppraiserSchedule, this.buttonRemoveAppraiser);
		v1.setSpacing(true);
		v1.setWidth("100px");
		v1.setVisible(allowEdit);
		HorizontalLayout h2 = new HorizontalLayout(this.layoutAppraisers, v1);
		h2.setSpacing(true);
		h2.setMargin(true);
		h2.setExpandRatio(this.layoutAppraisers, 1f);
		h2.setHeight("120px");
		h2.setWidth("800px");
		Panel panelAppraisers = new Panel("Membros Titulares");
		panelAppraisers.setContent(h2);
		
		this.layoutSubstitutes = new HorizontalLayout();
		this.layoutSubstitutes.setSizeFull();
		
		this.buttonAddSubstitute = new Button("Adicionar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addSubstitute();
            }
        });
		this.buttonAddSubstitute.setIcon(FontAwesome.PLUS);
		this.buttonAddSubstitute.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonAddSubstitute.setWidth("100px");
		
		this.buttonSubstituteSchedule = new Button("Agenda", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	substituteSchedule();
            }
        });
		this.buttonSubstituteSchedule.setIcon(FontAwesome.CALENDAR_O);
		this.buttonSubstituteSchedule.setWidth("100px");
		
		this.buttonRemoveSubstitute = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeSubstitute();
            }
        });
		this.buttonRemoveSubstitute.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveSubstitute.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonRemoveSubstitute.setWidth("100px");
		
		VerticalLayout v2 = new VerticalLayout(this.buttonAddSubstitute, this.buttonSubstituteSchedule, this.buttonRemoveSubstitute);
		v2.setSpacing(true);
		v2.setWidth("100px");
		v2.setVisible(allowEdit);
		HorizontalLayout h3 = new HorizontalLayout(this.layoutSubstitutes, v2);
		h3.setSpacing(true);
		h3.setMargin(true);
		h3.setExpandRatio(this.layoutSubstitutes, 1f);
		h3.setHeight("120px");
		h3.setWidth("800px");
		Panel panelSubstitutes = new Panel("Suplentes");
		panelSubstitutes.setContent(h3);
		
		VerticalLayout tab2 = new VerticalLayout(this.comboChair, panelAppraisers, panelSubstitutes);
		tab2.setSpacing(true);
		this.tabContainer.addTab(tab2, "Membros");
		
		this.addField(this.tabContainer);
		
		if(allowEdit && config.isUseDigitalSignature()) {
			this.setSignButtonVisible(true);
		}
		if(!allowEdit) {
			this.disableButtons();
		}
		
		this.loadJury();
	}
	
	private void loadJury() {
		this.textStudent.setValue(this.jury.getInternship().getStudent().getName());
		this.textCompany.setValue(this.jury.getInternship().getCompany().getName());
		this.textDate.setValue(this.jury.getDate());
		this.textLocal.setValue(this.jury.getLocal());
		this.textComments.setValue(this.jury.getComments());
		
		if(this.jury.getAppraisers() == null){
			try {
				InternshipJuryAppraiserRequestBO bo = new InternshipJuryAppraiserRequestBO();
				
				this.jury.setAppraisers(bo.listAppraisers(this.jury.getIdInternshipJuryRequest()));
			} catch (Exception e) {
				this.jury.setAppraisers(null);
				
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Banca", e.getMessage());
			}
		}
		
		for(InternshipJuryAppraiserRequest appraiser : this.jury.getAppraisers()) {
			if(appraiser.isChair()) {
				this.comboChair.setProfessor(appraiser.getAppraiser());
			} else if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
		}
		
		this.loadGridAppraisers();
		this.loadGridSubstitutes();
		
		try {
			if(Document.hasSignature(DocumentType.INTERNSHIPJURYREQUEST, this.jury.getIdInternshipJuryRequest())) {
				this.disableButtons();
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
		
		if(this.jury.isConfirmed()) {
			this.disableButtons();
		}
	}
	
	@Override
	public void disableButtons() {
		super.disableButtons();
		this.buttonAddAppraiser.setEnabled(false);
		this.buttonRemoveAppraiser.setEnabled(false);
		this.buttonAddSubstitute.setEnabled(false);
		this.buttonRemoveSubstitute.setEnabled(false);
	}
	
	@Override
	public void sign() {
		if(this.jury.getIdInternshipJuryRequest() == 0) {
			this.showWarningNotification("Assinar Requisição", "É necessário salvar a requisição antes de assinar.");
		} else {
			try {
				InternshipJuryFormReport report = new InternshipJuryRequestBO().getInternshipJuryRequestFormReport(this.jury.getIdInternshipJuryRequest());
				
				UI.getCurrent().addWindow(new SignatureWindow(DocumentType.INTERNSHIPJURYREQUEST, this.jury.getIdInternshipJuryRequest(), SignDatasetBuilder.buildRequest(report), SignDatasetBuilder.getSignaturesList(report), this, null));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Requisição", e.getMessage());
			}
		}
	}
	
	@Override
	public void save() {
		try{
			InternshipJuryRequestBO bo = new InternshipJuryRequestBO();
			boolean foundChair = false;
			
			this.jury.setLocal(this.textLocal.getValue());
			this.jury.setComments(this.textComments.getValue());
			this.jury.setDate(this.textDate.getValue());
			
			for(int i = 0; i < this.jury.getAppraisers().size(); i++) {
				if(this.jury.getAppraisers().get(i).isChair()) {
					this.jury.getAppraisers().get(i).setAppraiser(this.comboChair.getProfessor());
					foundChair = true;
				}
			}
			
			if(!foundChair) {
				InternshipJuryAppraiserRequest chair = new InternshipJuryAppraiserRequest();
				
				chair.setAppraiser(this.comboChair.getProfessor());
				chair.setChair(true);
				chair.setSubstitute(false);
				
				this.jury.getAppraisers().add(chair);
			}
			
			bo.save(Session.getIdUserLog(), this.jury);
			
			this.showSuccessNotification("Salvar Agendamento de Banca", "Agendamento de banca salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			
			if(this.config.isUseDigitalSignature()) {
				this.sign();
			} else {
				this.showReport(bo.getInternshipJuryRequestForm(this.jury.getIdInternshipJuryRequest()));
				
				this.close();	
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Agendamento de Banca", e.getMessage());
		}
	}
	
	private void loadGridAppraisers() {
		this.gridAppraisers = new Grid();
		this.gridAppraisers.addColumn("Membro", String.class);
		this.gridAppraisers.addColumn("Nome", String.class);
		this.gridAppraisers.setSizeFull();
		this.gridAppraisers.getColumns().get(0).setWidth(100);
		
		if(this.jury.getAppraisers() != null) {
			int member = 1;
			
			for(InternshipJuryAppraiserRequest appraiser : this.members) {
				if(!appraiser.isSubstitute() && !appraiser.isChair()) {
					this.gridAppraisers.addRow("Membro " + String.valueOf(member), appraiser.getAppraiser().getName());
					member = member + 1;
				}
			}
		}
		
		this.layoutAppraisers.removeAllComponents();
		this.layoutAppraisers.addComponent(this.gridAppraisers);
	}
	
	private void loadGridSubstitutes() {
		this.gridSubstitutes = new Grid();
		this.gridSubstitutes.addColumn("Suplente", String.class);
		this.gridSubstitutes.addColumn("Nome", String.class);
		this.gridSubstitutes.setSizeFull();
		this.gridSubstitutes.getColumns().get(0).setWidth(100);
		
		if(this.jury.getAppraisers() != null) {
			int member = 1;
			
			for(InternshipJuryAppraiserRequest appraiser : this.substitutes) {
				if(appraiser.isSubstitute()) {
					this.gridSubstitutes.addRow("Suplente " + String.valueOf(member), appraiser.getAppraiser().getName());
					member = member + 1;
				}
			}
		}
		
		this.layoutSubstitutes.removeAllComponents();
		this.layoutSubstitutes.addComponent(this.gridSubstitutes);
	}
	
	private void addAppraiser() {
		UI.getCurrent().addWindow(new EditInternshipJuryAppraiserWindow(this, false));
	}
	
	public void addAppraiser(InternshipJuryAppraiserRequest appraiser) throws Exception {
		InternshipJuryRequestBO bo = new InternshipJuryRequestBO();
		
		if(bo.canAddAppraiser(this.jury, appraiser.getAppraiser())) {
			this.jury.getAppraisers().add(appraiser);
			
			if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
			
			this.loadGridAppraisers();
			this.loadGridSubstitutes();
		}
	}
	
	private void removeAppraiser() {
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Membro", "Selecione o membro para remover.");
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do membro?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	for(int i = 0; i < jury.getAppraisers().size(); i++) {
                    		if(jury.getAppraisers().get(i).getAppraiser().getIdUser() == members.get(index).getAppraiser().getIdUser()) {
                    			jury.getAppraisers().remove(i);
                    			break;
                    		}
                    	}
                    	
                    	members.remove(index);
                    	loadGridAppraisers();
                    }
                }
            });
		}
	}
	
	private void appraiserSchedule() {
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Membro", "Selecione o membro para visualizar a agenda.");
		}else{
			UI.getCurrent().addWindow(new ProfessorScheculeWindow(members.get(index).getAppraiser()));
		}
	}
	
	private int getAppraiserSelectedIndex() {
    	Object itemId = this.gridAppraisers.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }
	
	private void addSubstitute() {
		UI.getCurrent().addWindow(new EditInternshipJuryAppraiserWindow(this, true));
	}
	
	private void removeSubstitute() {
		int index = this.getSubstituteSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Suplente", "Selecione o suplente para remover.");
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do suplente?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	for(int i = 0; i < jury.getAppraisers().size(); i++) {
                    		if(jury.getAppraisers().get(i).getAppraiser().getIdUser() == substitutes.get(index).getAppraiser().getIdUser()) {
                    			jury.getAppraisers().remove(i);
                    			break;
                    		}
                    	}
                    	
                    	substitutes.remove(index);
                    	loadGridSubstitutes();
                    }
                }
            });
		}
	}
	
	private void substituteSchedule() {
		int index = this.getSubstituteSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Suplente", "Selecione o suplente para visualizar a agenda.");
		}else{
			UI.getCurrent().addWindow(new ProfessorScheculeWindow(substitutes.get(index).getAppraiser()));
		}
	}
	
	private int getSubstituteSelectedIndex() {
    	Object itemId = this.gridSubstitutes.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }
	
}
