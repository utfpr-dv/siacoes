package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.BugReportBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.BugReportDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditBugReportWindow;

@PageTitle("Registro de Erros e Sugestões")
@Route(value = "bugreport", layout = MainLayout.class)
public class BugReportView extends ListView<BugReportDataSource> {
	
	public BugReportView(){
		super(SystemModule.GENERAL);
		
		this.getGrid().addColumn(BugReportDataSource::getDescription, "Description").setHeader("Descrição");
		this.getGrid().addColumn(BugReportDataSource::getType, "Type").setHeader("Tipo").setFlexGrow(0).setWidth("150px");
		this.getGrid().addColumn(BugReportDataSource::getStatus, "Status").setHeader("Status").setFlexGrow(0).setWidth("150px");
		
		this.setAddCaption("Reportar");
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try {
			BugReportBO bo = new BugReportBO();
			List<BugReport> list = bo.listAll();
			
			this.getGrid().setItems(BugReportDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Bugs", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditBugReportWindow window = new EditBugReportWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			BugReportBO bo = new BugReportBO();
			BugReport bug = bo.findById((int)id);
			
			EditBugReportWindow window = new EditBugReportWindow(bug, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Bug", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
