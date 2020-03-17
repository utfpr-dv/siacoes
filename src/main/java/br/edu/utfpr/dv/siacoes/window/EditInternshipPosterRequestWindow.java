package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequestForm;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditInternshipPosterRequestWindow extends EditWindow {
	
	private final InternshipPosterRequest request;
	private final List<InternshipPosterAppraiserRequest> members;
	private final List<InternshipPosterAppraiserRequest> substitutes;
	private SigesConfig config;
	
	private final TextField textStudent;
	private final TextField textCompany;
	private final TextField textSupervisor;
	private final HorizontalLayout layoutAppraisers;
	private Grid gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonRemoveAppraiser;
	private final HorizontalLayout layoutSubstitutes;
	private Grid gridSubstitutes;
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
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
		
		this.buttonRemoveAppraiser = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeAppraiser();
            }
        });
		this.buttonRemoveAppraiser.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveAppraiser.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonRemoveAppraiser.setWidth("100px");
		
		VerticalLayout v1 = new VerticalLayout(this.buttonAddAppraiser, this.buttonRemoveAppraiser);
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
		
		this.buttonRemoveSubstitute = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeSubstitute();
            }
        });
		this.buttonRemoveSubstitute.setIcon(FontAwesome.TRASH_O);
		this.buttonRemoveSubstitute.addStyleName(ValoTheme.BUTTON_DANGER);
		this.buttonRemoveSubstitute.setWidth("100px");
		
		VerticalLayout v2 = new VerticalLayout(this.buttonAddSubstitute, this.buttonRemoveSubstitute);
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
				
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
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
				
				UI.getCurrent().addWindow(new SignatureWindow(DocumentType.INTERNSHIPPOSTERREQUEST, this.request.getIdInternshipPosterRequest(), SignDatasetBuilder.build(report), SignDatasetBuilder.getSignaturesList(report), this, null));
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
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
			
			if(this.config.isUseDigitalSignature()) {
				this.sign();
			} else {
				//this.showReport(bo.getJuryRequestForm(this.jury.getIdJuryRequest()));
				
				this.close();	
			}
		} catch(Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Solicitação de Banca", e.getMessage());
		}
	}
	
	private void loadGridAppraisers() {
		this.gridAppraisers = new Grid();
		this.gridAppraisers.addColumn("Membro", String.class);
		this.gridAppraisers.addColumn("Nome", String.class);
		this.gridAppraisers.setSizeFull();
		this.gridAppraisers.getColumns().get(0).setWidth(100);
		
		if(this.request.getAppraisers() != null) {
			int member = 1;
			
			for(InternshipPosterAppraiserRequest appraiser : this.members) {
				if(!appraiser.isSubstitute()) {
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
		
		if(this.request.getAppraisers() != null) {
			int member = 1;
			
			for(InternshipPosterAppraiserRequest appraiser : this.substitutes) {
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
			ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do membro?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	for(int i = 0; i < request.getAppraisers().size(); i++) {
                    		if(request.getAppraisers().get(i).getAppraiser().getIdUser() == members.get(index).getAppraiser().getIdUser()) {
                    			request.getAppraisers().remove(i);
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
                    	for(int i = 0; i < request.getAppraisers().size(); i++) {
                    		if(request.getAppraisers().get(i).getAppraiser().getIdUser() == substitutes.get(index).getAppraiser().getIdUser()) {
                    			request.getAppraisers().remove(i);
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
	
	private int getSubstituteSelectedIndex() {
    	Object itemId = this.gridSubstitutes.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }

}
