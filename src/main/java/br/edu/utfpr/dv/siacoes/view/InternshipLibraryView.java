package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;

public class InternshipLibraryView extends ListView {
	
	public static final String NAME = "internshiplibrary";
	
	private final Button buttonDownloadFile;
	
	public InternshipLibraryView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Relatórios de Estágio");
		
		this.buttonDownloadFile = new Button("Download", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	download();
            }
        });
		this.buttonDownloadFile.setIcon(FontAwesome.DOWNLOAD);
		this.addActionButton(this.buttonDownloadFile);
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		this.setFiltersVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(120);
		
		try {
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
	    	List<InternshipFinalDocument> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), false);
	    	
	    	for(InternshipFinalDocument p : list){
				Object itemId = this.getGrid().addRow(p.getSubmissionDate(), p.getTitle(), p.getInternship().getStudent().getName(), p.getInternship().getCompany().getName());
				this.addRowId(itemId, p.getIdInternshipFinalDocument());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Monografias", e.getMessage());
		}
	}
	
	private void download() {
		Object value = getIdSelected();
		
		if(value == null) {
			this.showWarningNotification("Download do Relatório de Estágio", "Selecione um registro para baixar o relatório de estágio.");
		} else {
			try {
				InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
            	InternshipFinalDocument p = bo.findById((int)value);
            	
            	this.showReport(p.getFile());
        	} catch (Exception e) {
            	Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Download do Relatório de Estágio", e.getMessage());
			}
		}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
