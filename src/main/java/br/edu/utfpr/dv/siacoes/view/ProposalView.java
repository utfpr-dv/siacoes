package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditProposalWindow;

public class ProposalView extends ListView {

	public static final String NAME = "proposals";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonDownload;
	private final Button buttonInvalidate;
	
	private Button.ClickListener listenerClickDownload;
	
	public ProposalView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Propostas de TCC 1");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.buttonDownload = new Button("Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.addActionButton(this.buttonDownload);
		
		SigetConfig config = new SigetConfig();
		try {
			SigetConfigBO bo = new SigetConfigBO();
			
			config = bo.findByDepartment(Session.getUser().getDepartment().getIdDepartment());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.buttonInvalidate = new Button("Invalidar Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	invalidateProposal();
            }
        });
		if(config.isRegisterProposal()) {
			this.addActionButton(this.buttonInvalidate);
		}
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
	}
	
	protected void loadGrid(){
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Orientador", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(5).setWidth(125);
		
		try {
			ProposalBO bo = new ProposalBO();
	    	List<Proposal> list = bo.listBySemester(Session.getUser().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Proposal p : list){
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getStudent().getName(), p.getSupervisor().getName(), p.getTitle(), p.getSubmissionDate());
				this.addRowId(itemId, p.getIdProposal());
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			Notification.show("Listar Propostas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
    }
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Download da Proposta", "Selecione um registro para baixar a proposta.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				ProposalBO bo = new ProposalBO();
            	Proposal p = bo.findById((int)value);
				
            	if(p.getFile() != null) {
            		this.showReport(p.getFile());
            	} else {
            		Notification.show("Download da Proposta", "O acadêmico ainda não efetuou a submissão da proposta.", Notification.Type.WARNING_MESSAGE);
            	}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Download da Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}
	
	private void invalidateProposal() {
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Invalidar Proposta", "Selecione um registro para invalidar a proposta.", Notification.Type.WARNING_MESSAGE);
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a invalidação da proposta?\n\nIsso fará com que o acadêmico tenha que enviar uma nova proposta.", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	try {
                    		ProposalBO bo = new ProposalBO();
							bo.invalidated((int)value);
							
							refreshGrid();
							
							Notification.show("Invalidar Proposta", "Proposta invalidada com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
						} catch (Exception e) {
							Notification.show("Invalidar Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
						}
                    }
                }
            });
		}
	}
	
	@Override
	public void addClick() {
		
	}

	@Override
	public void editClick(Object id) {
		try {
			ProposalBO bo = new ProposalBO();
			Proposal p = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditProposalWindow(p, this, true));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() {
		// TODO Auto-generated method stub
		
	}

}
