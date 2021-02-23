package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.FileUploader.AcceptedDocumentType;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditProposalAppraiserWindow extends EditWindow {

	private final ProposalAppraiser appraiser;
	private final EditProposalWindow editProposalWindow;
	
	private final SupervisorComboBox comboAppraiser;
	private final Select<ProposalFeedback> comboFeedback;
	private final TextArea textComments;
	private final Checkbox checkAllowEditing;
	private final FileUploader uploadFile;
	private final Button buttonDownloadFile;
	
	private SigetConfig config;
	
	public EditProposalAppraiserWindow(ProposalAppraiser appraiser, EditProposalWindow editProposalWindow,  ListView parentView) {
		super("Editar Avaliador", parentView);
		
		if(parentView != null) {
			this.setTitle("Emitir Parecer");
		}
		
		if(appraiser == null){
			this.appraiser = new ProposalAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		try {
			this.config = new SigetConfigBO().findByDepartment(new ProposalBO().findIdDepartment(appraiser.getProposal().getIdProposal()));
		} catch (Exception e1) {
			this.config = new SigetConfig();
		}
		
		this.editProposalWindow = editProposalWindow;
		
		this.comboAppraiser = new SupervisorComboBox("Avaliador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		this.comboAppraiser.setWidth("800px");
		
		this.comboFeedback = new Select<ProposalFeedback>();
		this.comboFeedback.setLabel("Parecer");
		this.comboFeedback.setWidth("800px");
		this.comboFeedback.setItems(ProposalFeedback.NONE, ProposalFeedback.APPROVED, ProposalFeedback.APPROVEDWITHRESERVATIONS, ProposalFeedback.DISAPPROVED);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("200px");
		
		this.checkAllowEditing = new Checkbox("Permite edição");
		
		this.uploadFile = new FileUploader("Enviar arquivo comentado (PDF, 2 MB)");
		this.uploadFile.setAcceptedType(AcceptedDocumentType.PDF);
		this.uploadFile.setMaxBytesLength(this.config.getMaxFileSize());
		this.uploadFile.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadFile.getUploadedFile() != null) {
					appraiser.setFile(uploadFile.getUploadedFile());
					
					buttonDownloadFile.setVisible(true);
				}
			}
		});
		
		this.buttonDownloadFile = new Button("Download do Arquivo", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFeedback();
        });
		this.buttonDownloadFile.setVisible(this.appraiser.getFile() != null);
		
		this.addField(this.comboAppraiser);
		this.addField(this.comboFeedback);
		this.addField(this.textComments);
		this.addField(this.uploadFile);
		
		if(Session.isUserManager(SystemModule.SIGET)){
			this.addField(this.checkAllowEditing);
		}else if(!this.appraiser.isAllowEditing()){
			this.setSaveButtonEnabled(false);
		}
		
		this.addButton(this.buttonDownloadFile);
		this.buttonDownloadFile.setWidth("250px");
		
		if(config.isUseDigitalSignature()) {
			this.setSignButtonVisible(true);
			this.checkAllowEditing.setVisible(false);
		}
		
		this.loadAppraiser();
	}
	
	public EditProposalAppraiserWindow(ProposalAppraiser appraiser, EditProposalWindow editProposalWindow) {
		this(appraiser, editProposalWindow, null);
	}
	
	public EditProposalAppraiserWindow(ProposalAppraiser appraiser, ListView parentView) {
		this(appraiser, null, parentView);
	}
	
	private void loadAppraiser(){
		this.comboAppraiser.setProfessor(this.appraiser.getAppraiser());
		this.comboFeedback.setValue(this.appraiser.getFeedback());
		this.textComments.setValue(this.appraiser.getComments());
		this.checkAllowEditing.setValue(this.appraiser.isAllowEditing());
		
		if(!this.appraiser.isAllowEditing() || (this.appraiser.getAppraiser().getIdUser() != Session.getUser().getIdUser())){
			this.comboFeedback.setEnabled(false);
			this.textComments.setEnabled(false);
			this.setSignButtonEnabled(false);
		}
		
		if(this.appraiser.getFeedback() == ProposalFeedback.NONE) {
			this.setSignButtonEnabled(false);
		}
		
		if((this.appraiser.getIdProposalAppraiser() != 0) || (this.appraiser.getAppraiser().getIdUser() != 0)){
			this.comboAppraiser.setEnabled(false);
		}
		
		try {
			if(Document.hasSignature(br.edu.utfpr.dv.siacoes.sign.Document.DocumentType.APPRAISERFEEDBACK, this.appraiser.getIdProposalAppraiser())) {
				this.disableButtons();
				this.setSaveButtonEnabled(true);
				this.comboFeedback.setEnabled(false);
				this.textComments.setEnabled(false);
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
	}
	
	@Override
	public void sign() {
		try {
			List<User> users = new ArrayList<User>();
			
			users.add(this.appraiser.getAppraiser());
			
			this.appraiser.setProposal(new ProposalBO().findById(this.appraiser.getProposal().getIdProposal()));
			this.appraiser.setAppraiser(new UserBO().findById(this.appraiser.getAppraiser().getIdUser()));
			
			SignatureWindow window = new SignatureWindow(br.edu.utfpr.dv.siacoes.sign.Document.DocumentType.APPRAISERFEEDBACK, this.appraiser.getIdProposalAppraiser(), SignDatasetBuilder.build(this.appraiser), users, this, null);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Assinar Parecer", e.getMessage());
		}
	}
	
	@Override
	public void save() {
		try {
			if(Session.isUserManager(SystemModule.SIGET)) {
				this.appraiser.setAllowEditing(this.checkAllowEditing.getValue());
				
				if(this.appraiser.getAppraiser().getIdUser() == 0) {
					this.appraiser.setAppraiser(this.comboAppraiser.getProfessor());
				}
			}
			
			if(this.appraiser.isAllowEditing() && (this.appraiser.getAppraiser().getIdUser() == Session.getUser().getIdUser()) && (!this.config.isUseDigitalSignature() || !Document.hasSignature(br.edu.utfpr.dv.siacoes.sign.Document.DocumentType.APPRAISERFEEDBACK, this.appraiser.getIdProposalAppraiser()))) {
				this.appraiser.setFeedback((ProposalFeedback)this.comboFeedback.getValue());
				this.appraiser.setComments(this.textComments.getValue());
			}
			
			if(this.editProposalWindow == null) {
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				
				bo.save(Session.getIdUserLog(), this.appraiser);
				
				if(!this.config.isUseDigitalSignature()) {
					this.showReport(bo.getFeedbackReport(this.appraiser.getProposal().getIdProposal(), this.appraiser.getAppraiser().getIdUser()));
				}
			} else {
				this.editProposalWindow.setAppraiser(this.appraiser);
			}
			
			if(this.appraiser.getAppraiser().getIdUser() == Session.getUser().getIdUser()) {
				this.showSuccessNotification("Salvar Parecer", "Parecer salvo com sucesso.");
			} else {
				this.showSuccessNotification("Salvar Avaliador", "Avaliador salvo com sucesso.");
			}
			
			this.parentViewRefreshGrid();
			
			if((this.appraiser.getAppraiser().getIdUser() == Session.getUser().getIdUser()) && this.config.isUseDigitalSignature() && (this.appraiser.getFeedback() != ProposalFeedback.NONE)) {
				this.sign();
				this.setSignButtonEnabled(true);
			} else {
				this.close();	
			}
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Avaliador", e.getMessage());
		}
	}
	
	private void downloadFeedback() {
		if(this.appraiser.getFile() != null) {
			this.showReport(this.appraiser.getFile());
		} else {
			this.showWarningNotification("Download do Arquivo", "Nenhum arquivo foi enviado.");
		}
	}

}
