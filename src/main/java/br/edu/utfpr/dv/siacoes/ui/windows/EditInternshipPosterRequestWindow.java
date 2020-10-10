package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequestForm;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryAppraiserDataSource;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class EditInternshipPosterRequestWindow extends EditWindow {
	
	private final InternshipPosterRequest request;
	private final List<InternshipPosterAppraiserRequest> members;
	private final List<InternshipPosterAppraiserRequest> substitutes;
	private SigesConfig config;
	
	private final TextField textStudent;
	private final TextField textCompany;
	private final TextField textSupervisor;
	private Grid<JuryAppraiserDataSource> gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonRemoveAppraiser;
	private Grid<JuryAppraiserDataSource> gridSubstitutes;
	private final Button buttonAddSubstitute;
	private final Button buttonRemoveSubstitute;
	
	public EditInternshipPosterRequestWindow(InternshipPosterRequest request, ListView parentView) {
		this(request, true, parentView);
	}
	
	public EditInternshipPosterRequestWindow(InternshipPosterRequest request, boolean allowEdit, ListView parentView) {
		super("Requisição de Banca", parentView);
		
		if(request == null) {
			this.request = new InternshipPosterRequest();
		} else {
			this.request = request;
		}
		this.members = new ArrayList<InternshipPosterAppraiserRequest>();
		this.substitutes = new ArrayList<InternshipPosterAppraiserRequest>();
		try {
			if(this.request.getIdInternshipPosterRequest() == 0) {
				this.config = new SigesConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			} else {
				this.config = new SigesConfigBO().findByDepartment(this.request.getInternship().getDepartment().getIdDepartment());
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigesConfig();
		}
		
		this.textStudent = new TextField("Acadêmico(a)");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		
		this.textCompany = new TextField("Empresa");
		this.textCompany.setWidth("800px");
		this.textCompany.setEnabled(false);
		
		this.textSupervisor = new TextField("Professor(a) Orientador(a)");
		this.textSupervisor.setWidth("800px");
		this.textSupervisor.setEnabled(false);
		
		this.gridAppraisers = new Grid<JuryAppraiserDataSource>();
    	this.gridAppraisers.setSelectionMode(SelectionMode.SINGLE);
		this.gridAppraisers.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridAppraisers.addColumn(JuryAppraiserDataSource::getMember).setHeader("Membro");
		this.gridAppraisers.addColumn(JuryAppraiserDataSource::getAppraiser).setHeader("Nome");
		this.gridAppraisers.setSizeFull();
		
		this.buttonAddAppraiser = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addAppraiser();
        });
		this.buttonAddAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddAppraiser.setWidth("100px");
		
		this.buttonRemoveAppraiser = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeAppraiser();
        });
		this.buttonRemoveAppraiser.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveAppraiser.setWidth("100px");
		
		VerticalLayout v1 = new VerticalLayout(this.buttonAddAppraiser, this.buttonRemoveAppraiser);
		v1.setSpacing(false);
		v1.setMargin(false);
		v1.setPadding(false);
		v1.setWidth("100px");
		v1.setVisible(allowEdit);
		HorizontalLayout h2 = new HorizontalLayout(this.gridAppraisers, v1);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		h2.expand(this.gridAppraisers);
		h2.setHeight("120px");
		h2.setWidth("800px");
		Details panelAppraisers = new Details();
		panelAppraisers.setSummaryText("Membros Titulares");
		panelAppraisers.setOpened(true);
		panelAppraisers.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelAppraisers.getElement().getStyle().set("width", "100%");
		panelAppraisers.setContent(h2);
		
		this.gridSubstitutes = new Grid<JuryAppraiserDataSource>();
    	this.gridSubstitutes.setSelectionMode(SelectionMode.SINGLE);
		this.gridSubstitutes.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		this.gridSubstitutes.addColumn(JuryAppraiserDataSource::getMember).setHeader("Suplente");
		this.gridSubstitutes.addColumn(JuryAppraiserDataSource::getAppraiser).setHeader("Nome");
		this.gridSubstitutes.setSizeFull();
		
		this.buttonAddSubstitute = new Button("Adicionar", new Icon(VaadinIcon.PLUS), event -> {
            addSubstitute();
        });
		this.buttonAddSubstitute.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonAddSubstitute.setWidth("100px");
		
		this.buttonRemoveSubstitute = new Button("Remover", new Icon(VaadinIcon.TRASH), event -> {
            removeSubstitute();
        });
		this.buttonAddSubstitute.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		this.buttonRemoveSubstitute.setWidth("100px");
		
		VerticalLayout v2 = new VerticalLayout(this.buttonAddSubstitute, this.buttonRemoveSubstitute);
		v2.setSpacing(false);
		v2.setMargin(false);
		v2.setPadding(false);
		v2.setWidth("100px");
		v2.setVisible(allowEdit);
		HorizontalLayout h3 = new HorizontalLayout(this.gridSubstitutes, v2);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		h3.expand(this.gridSubstitutes);
		h3.setHeight("120px");
		h3.setWidth("800px");
		Details panelSubstitutes = new Details();
		panelSubstitutes.setSummaryText("Suplentes");
		panelSubstitutes.setOpened(true);
		panelSubstitutes.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
		panelSubstitutes.getElement().getStyle().set("width", "100%");
		panelSubstitutes.setContent(h3);
		
		this.addField(this.textStudent);
		this.addField(this.textCompany);
		this.addField(this.textSupervisor);
		this.addField(panelAppraisers);
		this.addField(panelSubstitutes);
		
		if(allowEdit && this.config.isUseDigitalSignature()) {
			this.setSignButtonVisible(true);
		}
		if(!allowEdit) {
			this.disableButtons();
		}
		
		this.loadRequest();
	}
	
	private void loadRequest() {
		this.textStudent.setValue(this.request.getInternship().getStudent().getName());
		this.textCompany.setValue(this.request.getInternship().getCompany().getName());
		this.textSupervisor.setValue(this.request.getInternship().getSupervisor().getName());
		
		if(this.request.getAppraisers() == null) {
			try {
				this.request.setAppraisers(new InternshipPosterAppraiserRequestBO().listAppraisers(this.request.getIdInternshipPosterRequest()));
			} catch (Exception e) {
				this.request.setAppraisers(null);
				
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Carregar Banca", e.getMessage());
			}
		}
		
		for(InternshipPosterAppraiserRequest appraiser : this.request.getAppraisers()) {
			if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
		}
		
		this.loadGridAppraisers();
		this.loadGridSubstitutes();
		
		try {
			if(Document.hasSignature(DocumentType.INTERNSHIPPOSTERREQUEST, this.request.getIdInternshipPosterRequest())) {
				this.disableButtons();
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
		
		if(this.request.isConfirmed()) {
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
		if(this.request.getIdInternshipPosterRequest() == 0) {
			this.showWarningNotification("Assinar Requisição", "É necessário salvar a requisição antes de assinar.");
		} else {
			try {
				InternshipPosterRequestForm report = new InternshipPosterRequestBO().getPosterRequestFormReport(this.request.getIdInternshipPosterRequest());
				
				SignatureWindow window = new SignatureWindow(DocumentType.INTERNSHIPPOSTERREQUEST, this.request.getIdInternshipPosterRequest(), SignDatasetBuilder.build(report), SignDatasetBuilder.getSignaturesList(report), this, null);
				window.open();
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Requisição", e.getMessage());
			}
		}
	}

	@Override
	public void save() {
		try {
			new InternshipPosterRequestBO().save(Session.getIdUserLog(), this.request);
			
			this.showSuccessNotification("Salvar Solicitação de Banca", "Solicitação de banca salva com sucesso.");
			
			this.parentViewRefreshGrid();
			
			if((Session.getUser().getIdUser() == this.request.getInternship().getStudent().getIdUser()) && this.config.isUseDigitalSignature()) {
				this.sign();
			} else {
				this.showReport(new InternshipPosterRequestBO().getPosterRequestForm(this.request.getIdInternshipPosterRequest()));
				
				this.close();	
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Solicitação de Banca", e.getMessage());
		}
	}
	
	private void loadGridAppraisers() {
		this.gridAppraisers.setItems(new ArrayList<JuryAppraiserDataSource>());
		
		if(this.request.getAppraisers() != null) {
			this.gridAppraisers.setItems(JuryAppraiserDataSource.loadInternshipPosterRequest(this.request.getAppraisers(), true, false));
		}
	}
	
	private void loadGridSubstitutes() {
		this.gridSubstitutes.setItems(new ArrayList<JuryAppraiserDataSource>());
		
		if(this.request.getAppraisers() != null) {
			this.gridSubstitutes.setItems(JuryAppraiserDataSource.loadInternshipPosterRequest(this.request.getAppraisers(), false, true));
		}
	}
	
	private void addAppraiser() {
		EditInternshipJuryAppraiserWindow window = new EditInternshipJuryAppraiserWindow(this, false);
		window.open();
	}
	
	public void addAppraiser(InternshipPosterAppraiserRequest appraiser) throws Exception {
		InternshipPosterRequestBO bo = new InternshipPosterRequestBO();
		
		if(bo.canAddAppraiser(this.request, appraiser.getAppraiser())) {
			this.request.getAppraisers().add(appraiser);
			
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
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.TRASH))
		    	.withCaption("Confirma a Exclusão?")
		    	.withMessage("Confirma a exclusão do membro da banca?")
		    	.withOkButton(() -> {
		    		for(int i = 0; i < this.request.getAppraisers().size(); i++) {
                		if(this.request.getAppraisers().get(i).getAppraiser().getIdUser() == this.members.get(index).getAppraiser().getIdUser()) {
                			this.request.getAppraisers().remove(i);
                			break;
                		}
                	}
                	
                	this.members.remove(index);
                	loadGridAppraisers();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
		}
	}
	
	private int getAppraiserSelectedIndex() {
    	JuryAppraiserDataSource appraiser = this.gridAppraisers.asSingleSelect().getValue();

    	if(appraiser == null){
    		return -1;
    	}else{
    		for(int i = 0; i < this.members.size(); i++) {
    			if(this.members.get(i).getAppraiser().getIdUser() == appraiser.getIdUser()) {
    				return i;
    			}
    		}
    		
    		return -1;
    	}
    }
	
	private void addSubstitute() {
		EditInternshipJuryAppraiserWindow window = new EditInternshipJuryAppraiserWindow(this, true);
		window.open();
	}
	
	private void removeSubstitute() {
		int index = this.getSubstituteSelectedIndex();
		
		if(index == -1){
			this.showWarningNotification("Selecionar Suplente", "Selecione o suplente para remover.");
		}else{
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.TRASH))
		    	.withCaption("Confirma a Exclusão?")
		    	.withMessage("CConfirma a remoção do suplente?")
		    	.withOkButton(() -> {
		    		for(int i = 0; i < this.request.getAppraisers().size(); i++) {
                		if(this.request.getAppraisers().get(i).getAppraiser().getIdUser() == this.substitutes.get(index).getAppraiser().getIdUser()) {
                			this.request.getAppraisers().remove(i);
                			break;
                		}
                	}
                	
                	this.substitutes.remove(index);
                	loadGridSubstitutes();
		    	}, ButtonOption.caption("Excluir"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
		}
	}
	
	private int getSubstituteSelectedIndex() {
		JuryAppraiserDataSource appraiser = this.gridSubstitutes.asSingleSelect().getValue();

    	if(appraiser == null){
    		return -1;
    	}else{
    		for(int i = 0; i < this.substitutes.size(); i++) {
    			if(this.substitutes.get(i).getAppraiser().getIdUser() == appraiser.getIdUser()) {
    				return i;
    			}
    		}
    		
    		return -1;
    	}
    }

}
