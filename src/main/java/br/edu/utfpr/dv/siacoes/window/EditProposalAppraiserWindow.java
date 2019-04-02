package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditProposalAppraiserWindow extends EditWindow {

	private final ProposalAppraiser appraiser;
	private final EditProposalWindow editProposalWindow;
	
	private final SupervisorComboBox comboAppraiser;
	private final NativeSelect comboFeedback;
	private final TextArea textComments;
	private final CheckBox checkAllowEditing;
	private final FileUploader uploadFile;
	private final Button buttonDownloadFile;
	
	private SigetConfig config;
	
	public EditProposalAppraiserWindow(ProposalAppraiser appraiser, EditProposalWindow editProposalWindow,  ListView parentView) {
		super("Editar Avaliador", null);
		
		if(parentView != null) {
			this.setCaption("Emitir Parecer");
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
		
		this.comboFeedback = new NativeSelect("Parecer");
		this.comboFeedback.setNullSelectionAllowed(false);
		this.comboFeedback.setWidth("800px");
		this.comboFeedback.addItem(ProposalFeedback.NONE);
		this.comboFeedback.addItem(ProposalFeedback.APPROVED);
		this.comboFeedback.addItem(ProposalFeedback.APPROVEDWITHRESERVATIONS);
		this.comboFeedback.addItem(ProposalFeedback.DISAPPROVED);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("200px");
		this.textComments.addStyleName("textscroll");
		
		this.checkAllowEditing = new CheckBox("Permite edição");
		
		this.uploadFile = new FileUploader("Enviar arquivo comentado (PDF, 2 MB)");
		this.uploadFile.getAcceptedDocumentTypes().add(DocumentType.PDF);
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
		
		this.buttonDownloadFile = new Button("Download do Arquivo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFeedback();
            }
        });
		this.buttonDownloadFile.setIcon(FontAwesome.DOWNLOAD);
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
		}
		
		if((this.appraiser.getIdProposalAppraiser() != 0) || (this.appraiser.getAppraiser().getIdUser() != 0)){
			this.comboAppraiser.setEnabled(false);
		}
	}
	
	@Override
	public void save() {
		try{
			if(Session.isUserManager(SystemModule.SIGET)){
				this.appraiser.setAllowEditing(this.checkAllowEditing.getValue());
				
				if(this.appraiser.getAppraiser().getIdUser() == 0){
					this.appraiser.setAppraiser(this.comboAppraiser.getProfessor());
				}
			}
			
			if(this.appraiser.isAllowEditing() && (this.appraiser.getAppraiser().getIdUser() == Session.getUser().getIdUser())){
				this.appraiser.setFeedback((ProposalFeedback)this.comboFeedback.getValue());
				this.appraiser.setComments(this.textComments.getValue());
			}
			
			if(this.editProposalWindow == null){
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				
				bo.save(Session.getIdUserLog(), this.appraiser);
				
				this.showReport(bo.getFeedbackReport(this.appraiser.getProposal().getIdProposal(), this.appraiser.getAppraiser().getIdUser()));
			}else{
				this.editProposalWindow.setAppraiser(this.appraiser);
			}
			
			this.showSuccessNotification("Salvar Avaliador", "Avaliador salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
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
