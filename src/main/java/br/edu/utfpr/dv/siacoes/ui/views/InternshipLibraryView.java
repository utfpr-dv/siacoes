package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipLibraryDataSource;

@PageTitle("Relatórios de Estágio")
@Route(value = "internshiplibrary", layout = MainLayout.class)
public class InternshipLibraryView extends ListView<InternshipLibraryDataSource> {
	
	private final Button buttonDownloadFile;
	
	public InternshipLibraryView(){
		super(SystemModule.SIGES);
		
		this.getGrid().addColumn(new LocalDateRenderer<>(InternshipLibraryDataSource::getSubmission, "dd/MM/yyyy"), "Submission").setHeader("Submissão").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(InternshipLibraryDataSource::getTitle, "Title").setHeader("Título");
		this.getGrid().addColumn(InternshipLibraryDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(InternshipLibraryDataSource::getCompany, "Company").setHeader("Empresa");
		
		this.buttonDownloadFile = new Button("Download", new Icon(VaadinIcon.DOWNLOAD), event -> {
            download();
        });
		this.addActionButton(this.buttonDownloadFile);
		
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		this.setFiltersVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		try {
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
	    	List<InternshipFinalDocument> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), false);
	    	
	    	this.getGrid().setItems(InternshipLibraryDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
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
            	Logger.log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Download do Relatório de Estágio", e.getMessage());
			}
		}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		// TODO Auto-generated method stub
		
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
