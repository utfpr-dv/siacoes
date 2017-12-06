package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.window.EditProposalWindow;

public class ProposalView extends ListView {

	public static final String NAME = "proposals";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonDownload;
	
	private Button.ClickListener listenerClickDownload;
	
	public ProposalView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Propostas de TCC 1");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.buttonDownload = new Button("Proposta");
		this.addActionButton(this.buttonDownload);
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
	}
	
	protected void loadGrid(){
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addSelectionListener(new SelectionListener() {
			@Override
			public void select(SelectionEvent event) {
				prepareDownload();
			}
		});
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(4).setWidth(125);
		
		this.prepareDownload();
		
		try {
			ProposalBO bo = new ProposalBO();
	    	List<Proposal> list = bo.listBySemester(Session.getUser().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Proposal p : list){
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getTitle(), p.getStudent().getName(), p.getSubmissionDate());
				this.addRowId(itemId, p.getIdProposal());
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			Notification.show("Listar Propostas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
    }
	
	private void prepareDownload(){
		Object value = getIdSelected();
		
		this.buttonDownload.removeClickListener(this.listenerClickDownload);
    	
    	if(value != null){
			try {
            	ProposalBO bo = new ProposalBO();
            	Proposal p = bo.findById((int)value);
            	
            	if(p.getFile() != null){
            		new ExtensionUtils().extendToDownload(p.getTitle() + p.getFileType().getExtension(), p.getFile(), this.buttonDownload);	
            	}else{
            		this.listenerClickDownload = new Button.ClickListener() {
    		            @Override
    		            public void buttonClick(ClickEvent event) {
    		            	Notification.show("Download da Proposta", "O acadêmico ainda não efetuou a submissão da proposta.", Notification.Type.WARNING_MESSAGE);
    		            }
    		        };
    		        
            		this.buttonDownload.addClickListener(this.listenerClickDownload);
            	}
        	} catch (Exception e) {
        		this.listenerClickDownload = new Button.ClickListener() {
		            @Override
		            public void buttonClick(ClickEvent event) {
		            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		            	
		            	Notification.show("Download da Proposta", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		            }
		        };
		        
        		this.buttonDownload.addClickListener(this.listenerClickDownload);
			}
    	}else{
    		new ExtensionUtils().removeAllExtensions(this.buttonDownload);
    		
    		this.listenerClickDownload = new Button.ClickListener() {
	            @Override
	            public void buttonClick(ClickEvent event) {
	            	Notification.show("Download da Proposta", "Selecione um registro para baixar a proposta.", Notification.Type.WARNING_MESSAGE);
	            }
	        };
    		
    		this.buttonDownload.addClickListener(this.listenerClickDownload);
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
