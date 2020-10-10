package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.DeadlineDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditDeadlineWindow;

@PageTitle("Datas Limite de TCC")
@Route(value = "deadline", layout = MainLayout.class)
public class DeadlineView extends ListView<DeadlineDataSource> {
	
	public DeadlineView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		SigetConfig sigetConfig = new SigetConfig();
		try {
			sigetConfig = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e1) {
			Logger.log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		this.getGrid().addColumn(DeadlineDataSource::getSemester).setHeader("Semestre");
		this.getGrid().addColumn(DeadlineDataSource::getYear).setHeader("Ano");
		this.getGrid().addColumn(DeadlineDataSource::getProposal).setHeader(sigetConfig.isRegisterProposal() ? "Proposta" : "Reg. Orient.");
		this.getGrid().addColumn(DeadlineDataSource::getProject).setHeader("Projeto");
		this.getGrid().addColumn(DeadlineDataSource::getThesis).setHeader("Monografia");
		this.getGrid().addColumn(DeadlineDataSource::getFinalProject).setHeader("Proj. Final");
		this.getGrid().addColumn(DeadlineDataSource::getFinalThesis).setHeader("Monog. Final");
		
    	this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}
	
	protected void loadGrid(){
		try {
			DeadlineBO bo = new DeadlineBO();
	    	List<Deadline> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
	    	
	    	this.getGrid().setItems(DeadlineDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Datas", e.getMessage());
		}
    }
	
	@Override
	public void addClick() {
		EditDeadlineWindow window = new EditDeadlineWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			DeadlineBO bo = new DeadlineBO();
			Deadline d = bo.findById((int)id);
			
			EditDeadlineWindow window = new EditDeadlineWindow(d, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Datas", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() {
		// TODO Auto-generated method stub
		
	}

}
