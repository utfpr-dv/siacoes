package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.BugReportBO;
import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.window.EditBugReportWindow;

public class BugReportView extends ListView {
	
	public static final String NAME = "bugreport";
	
	public BugReportView(){
		super(SystemModule.GENERAL);
		
		this.setCaption("Registro de Erros e Sugestões");
		
		this.setAddCaption("Reportar");
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Descrição", String.class);
		this.getGrid().addColumn("Tipo", String.class);
		this.getGrid().addColumn("Status", String.class);
		
		this.getGrid().getColumns().get(1).setWidth(150);
		this.getGrid().getColumns().get(2).setWidth(150);
		
		try {
			BugReportBO bo = new BugReportBO();
			List<BugReport> list = bo.listAll();
			
			for(BugReport bug : list){
				Object itemId = this.getGrid().addRow(bug.getTitle(), bug.getType().toString(), bug.getStatus().toString());
				this.addRowId(itemId, bug.getIdBugReport());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Bugs", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditBugReportWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			BugReportBO bo = new BugReportBO();
			BugReport bug = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditBugReportWindow(bug, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Bug", e.getMessage());
		}
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
