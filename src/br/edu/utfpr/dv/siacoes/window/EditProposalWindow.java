package br.edu.utfpr.dv.siacoes.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.ProfessorComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditProposalWindow extends EditWindow {

	private final Proposal proposal;
	private final boolean submitProposal;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textTitle;
	private final TextField textSubarea;
	private final TextField textStudent;
	private final ProfessorComboBox comboSupervisor;
	private final ProfessorComboBox comboCosupervisor;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final DateField textSubmissionDate;
	private final Upload uploadFile;
	private final Image imageFileUploaded;
	private final HorizontalLayout layoutAppraisers;
	private Grid gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonViewAppraiser;
	private final Button buttonDeleteAppraiser;
	private final Button buttonDownloadProposal;
	private final TabSheet tab;
	
	public EditProposalWindow(Proposal proposal, ListView parentView, boolean submitProposal){
		super("Editar Proposta", parentView);
		
		this.submitProposal = submitProposal;
		
		if(!this.submitProposal){
			this.setCaption("Registrar Orientação");
		}
		
		if(proposal == null){
			this.proposal = new Proposal();
			this.proposal.setDepartment(Session.getUser().getDepartment());
		}else{
			this.proposal = proposal;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("400px");
		this.textTitle.setMaxLength(255);
		
		this.textSubarea = new TextField("Subárea");
		this.textSubarea.setWidth("400px");
		this.textSubarea.setMaxLength(255);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setEnabled(false);
		this.textStudent.setWidth("800px");
		
		this.comboSupervisor = new ProfessorComboBox("Orientador");
		
		this.comboCosupervisor = new ProfessorComboBox("Co-orientador");
		this.comboCosupervisor.setNullSelectionAllowed(true);
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setEnabled(false);
		
		this.textYear = new YearField();
		this.textYear.setEnabled(false);
		
		this.textSubmissionDate = new DateField("Data de Submissão");
		this.textSubmissionDate.setEnabled(false);
		this.textSubmissionDate.setDateFormat("dd/MM/yyyy");
		
		DocumentUploader listener = new DocumentUploader();
		this.uploadFile = new Upload("(Formato PDF, Tam. Máx. 5 MB)", listener);
		this.uploadFile.addSucceededListener(listener);
		this.uploadFile.setButtonCaption("Enviar Arquivo");
		this.uploadFile.setImmediate(true);
		
		this.imageFileUploaded = new Image("", new ThemeResource("images/ok.png"));
		this.imageFileUploaded.setVisible(false);
		
		VerticalLayout tab1 = new VerticalLayout();
		tab1.setSpacing(true);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboCampus, this.comboDepartment);
		h1.setSpacing(true);
		
		HorizontalLayout h2 = new HorizontalLayout(this.textTitle, this.textSubarea);
		h2.setSpacing(true);
		
		HorizontalLayout h3 = new HorizontalLayout(this.comboSupervisor, this.comboCosupervisor);
		h3.setSpacing(true);
		
		HorizontalLayout h4;
		if(this.submitProposal && Session.isUserStudent()){
			h4 = new HorizontalLayout(this.uploadFile, this.imageFileUploaded, this.comboSemester, this.textYear, this.textSubmissionDate);
		}else{
			h4 = new HorizontalLayout(this.comboSemester, this.textYear, this.textSubmissionDate);
		}
		h4.setSpacing(true);
		
		tab1.addComponent(h1);
		tab1.addComponent(this.textStudent);
		tab1.addComponent(h2);
		tab1.addComponent(h3);
		tab1.addComponent(h4);
		
		this.layoutAppraisers = new HorizontalLayout();
		
		this.buttonAddAppraiser = new Button("Adicionar Avaliador", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addAppraiser();
            }
        });
		
		this.buttonViewAppraiser = new Button("Visualizar Observações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	editAppraiser();
            }
        });
		
		this.buttonDeleteAppraiser = new Button("Remover Avaliador", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	deleteAppraiser();
            }
        });
		
		HorizontalLayout layoutButtons = new HorizontalLayout(this.buttonAddAppraiser, this.buttonViewAppraiser, this.buttonDeleteAppraiser);
		layoutButtons.setSpacing(true);
		
		VerticalLayout tab2 = new VerticalLayout(this.layoutAppraisers, layoutButtons);
		tab2.setSpacing(true);
		
		this.tab = new TabSheet();
		this.tab.setWidth("820px");
		this.tab.addTab(tab1, "Proposta");
		
		if(Session.isUserManager(SystemModule.SIGET)){
			this.tab.addTab(tab2, "Avaliadores");
			
			this.loadGridAppraisers();
		}else if(Session.isUserStudent()){
			try {
				DeadlineBO dbo = new DeadlineBO();
				Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getProposalDeadline())){
					this.setSaveButtonEnabled(false);	
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				this.setSaveButtonEnabled(false);
				Notification.show("Submeter Proposta", "Não foi possível determinar a data limite para entrega das propostas.", Notification.Type.ERROR_MESSAGE);
			}
		}
		
		this.addField(this.tab);
		
		this.buttonDownloadProposal = new Button("Download da Proposta");
		this.addButton(this.buttonDownloadProposal);
		this.buttonDownloadProposal.setWidth("250px");
		
		this.loadProposal();
		this.textTitle.focus();
	}
	
	private void loadProposal(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.proposal.getDepartment().getIdDepartment());
			
			if(campus != null){
				this.comboCampus.setCampus(campus);
				
				this.comboDepartment.setIdCampus(campus.getIdCampus());
				
				this.comboDepartment.setDepartment(this.proposal.getDepartment());
			}else{
				this.comboCampus.setCampus(Session.getUser().getDepartment().getCampus());
				
				this.comboDepartment.setIdCampus(Session.getUser().getDepartment().getCampus().getIdCampus());
				
				this.comboDepartment.setDepartment(Session.getUser().getDepartment());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textStudent.setValue(this.proposal.getStudent().getName());
		this.textTitle.setValue(this.proposal.getTitle());
		this.textSubarea.setValue(this.proposal.getSubarea());
		this.comboSemester.setSemester(this.proposal.getSemester());
		this.textYear.setYear(this.proposal.getYear());
		this.textSubmissionDate.setValue(this.proposal.getSubmissionDate());
		this.comboSupervisor.setProfessor(this.proposal.getSupervisor());
		this.comboCosupervisor.setProfessor(this.proposal.getCosupervisor());
		
		if(this.proposal.getIdProposal() != 0){
			this.comboSupervisor.setEnabled(false);
			
			if((this.proposal.getCosupervisor() != null) && (this.proposal.getCosupervisor().getIdUser() != 0)){
				this.comboCosupervisor.setEnabled(false);
			}
		}
		
		this.prepareDownloadProposal();
	}
	
	private void loadGridAppraisers(){
		this.gridAppraisers = new Grid();
		this.gridAppraisers.addColumn("Avaliador", String.class);
		this.gridAppraisers.addColumn("Parecer", String.class);
		this.gridAppraisers.setWidth("800px");
		this.gridAppraisers.setHeight("300px");
		
		try {
			if(this.proposal.getAppraisers() == null){
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				this.proposal.setAppraisers(bo.listAppraisers(this.proposal.getIdProposal()));
			}
			
	    	for(ProposalAppraiser p : this.proposal.getAppraisers()){
				this.gridAppraisers.addRow(p.getAppraiser().getName(), p.getFeedback().toString());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Carregar Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
		
		this.layoutAppraisers.removeAllComponents();
		this.layoutAppraisers.addComponent(this.gridAppraisers);
	}
	
	@Override
	public void save() {
		if(Session.isUserStudent()){
			if(this.submitProposal && (this.proposal.getFile() == null)){
				if(this.proposal.getFile() == null){
					Notification.show("Submeter Proposta", "É necessário enviar o arquivo da proposta.", Notification.Type.ERROR_MESSAGE);
					return;	
				}
				if(proposal.getFileType() == DocumentType.UNDEFINED){
					Notification.show("O arquivo enviado não está no formato correto. Envie um arquivo PDF.");
					return;
				}
			}
			
			try {
				DeadlineBO dbo = new DeadlineBO();
				Deadline d = dbo.findBySemester(Session.getUser().getDepartment().getIdDepartment(), DateUtils.getSemester(), DateUtils.getYear());
				
				if(DateUtils.getToday().getTime().after(d.getProposalDeadline())){
					Notification.show("Submeter Proposta", "O prazo para a submissão de propostas já foi encerrado.", Notification.Type.ERROR_MESSAGE);
					return;
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				Notification.show("Submeter Proposta", "Não foi possível determinar a data limite para entrega das propostas.", Notification.Type.ERROR_MESSAGE);
				return;
			}
		}
		
		try{
			ProposalBO bo = new ProposalBO();
		
			if(Session.isUserStudent()){
				this.proposal.setSubmissionDate(DateUtils.getToday().getTime());
			}
			
			this.proposal.setTitle(this.textTitle.getValue());
			this.proposal.setSubarea(this.textSubarea.getValue());
			
			if(this.proposal.getIdProposal() == 0){
				this.proposal.setSupervisor(this.comboSupervisor.getProfessor());
				this.proposal.setCosupervisor(this.comboCosupervisor.getProfessor());
				this.proposal.setDepartment(this.comboDepartment.getDepartment());
			}
			
			if((this.proposal.getCosupervisor() == null) || (this.proposal.getCosupervisor().getIdUser() == 0)){
				this.proposal.setCosupervisor(this.comboCosupervisor.getProfessor());
			}
			
			bo.save(this.proposal);
			
			Notification.show("Salvar Proposta", "Proposta salva com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private int getAppraiserSelectedIndex(){
    	Object itemId = this.gridAppraisers.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }
	
	private void addAppraiser(){
		ProposalAppraiser p = new ProposalAppraiser();
    	p.setProposal(proposal);
    	
    	UI.getCurrent().addWindow(new EditProposalAppraiserWindow(p, this));
	}
	
	private void editAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			Notification.show("Selecionar Avaliador", "Selecione o avaliador para visualizar.", Notification.Type.WARNING_MESSAGE);
		}else{
			UI.getCurrent().addWindow(new EditProposalAppraiserWindow(this.proposal.getAppraisers().get(index), this));	
		}
	}
	
	private void deleteAppraiser(){
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			Notification.show("Selecionar Avaliador", "Selecione o avaliador para remover.", Notification.Type.WARNING_MESSAGE);
		}else if(this.proposal.getAppraisers().get(index).getFeedback() != ProposalFeedback.NONE){
			Notification.show("Selecionar Avaliador", "O avaliador selecionado não pode ser removido pois o mesmo já informou seu parecer.", Notification.Type.WARNING_MESSAGE);
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do avaliador?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	proposal.getAppraisers().remove(index);
                    	loadGridAppraisers();
                    }
                }
            });
		}
	}
	
	public void setAppraiser(ProposalAppraiser appraiser){
		boolean add = true;
		
		for(int i = 0; i < this.proposal.getAppraisers().size(); i++){
			if((this.proposal.getAppraisers().get(i).getIdProposalAppraiser() == appraiser.getIdProposalAppraiser()) && (this.proposal.getAppraisers().get(i).getAppraiser().getIdUser() == appraiser.getAppraiser().getIdUser())){
				this.proposal.getAppraisers().set(i, appraiser);
				add = false;
				break;
			}
		}
		
		if(add){
			this.proposal.getAppraisers().add(appraiser);
		}
		
		this.loadGridAppraisers();
	}
	
	private void prepareDownloadProposal(){
		new ExtensionUtils().removeAllExtensions(this.buttonDownloadProposal);
		
		if(this.proposal.getFile() != null){
			this.buttonDownloadProposal.setVisible(true);
			
			new ExtensionUtils().extendToDownload("Proposta_TCC_" + this.proposal.getIdProposal() + Document.DocumentType.PDF.getExtension(), this.proposal.getFile(), this.buttonDownloadProposal);
		}else{
			this.buttonDownloadProposal.setVisible(false);
		}
	}
	
	@SuppressWarnings("serial")
	class DocumentUploader implements Receiver, SucceededListener {
		private File tempFile;
		
		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {
			try {
				imageFileUploaded.setVisible(false);
				
				if(DocumentType.fromMimeType(mimeType) != DocumentType.PDF){
					throw new Exception("O arquivo precisa estar no formato PDF.");
				}
				
				proposal.setFileType(DocumentType.fromMimeType(mimeType));
	            tempFile = File.createTempFile(filename, "tmp");
	            tempFile.deleteOnExit();
	            return new FileOutputStream(tempFile);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }

	        return null;
		}
		
		@Override
		public void uploadSucceeded(SucceededEvent event) {
			try {
	            FileInputStream input = new FileInputStream(tempFile);
	            
	            if(input.available() > (10 * 1024 * 1024)){
					throw new Exception("O arquivo precisa ter um tamanho máximo de 5 MB.");
	            }
	            
	            byte[] buffer = new byte[input.available()];
	            
	            input.read(buffer);
	            
	            proposal.setFile(buffer);
	            
	            imageFileUploaded.setVisible(true);
	            
	            Notification.show("Carregamento do Arquivo", "O arquivo foi enviado com sucesso.\n\nClique em SALVAR para concluir a submissão.", Notification.Type.HUMANIZED_MESSAGE);
	        } catch (Exception e) {
	        	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	            
	            Notification.show("Carregamento do Arquivo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
	        }
		}
	}

}
