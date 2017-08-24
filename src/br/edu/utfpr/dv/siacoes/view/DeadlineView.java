package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditDeadlineWindow;

public class DeadlineView extends ListView {

	public static final String NAME = "deadline";
	
	public DeadlineView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
    	this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}
	
	protected void loadGrid(){
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Proposta", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Projeto", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Monografia", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(2).setWidth(125);
		this.getGrid().getColumns().get(3).setWidth(125);
		this.getGrid().getColumns().get(4).setWidth(125);
		
		try {
			DeadlineBO bo = new DeadlineBO();
	    	List<Deadline> list = bo.listByDepartment(Session.getUser().getDepartment().getIdDepartment());
	    	
	    	for(Deadline d : list){
				Object itemId = this.getGrid().addRow(d.getSemester(), d.getYear(), d.getProposalDeadline(), d.getProjectDeadline(), d.getThesisDeadline());
				this.addRowId(itemId, d.getIdDeadline());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Datas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
    }
	
	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditDeadlineWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try {
			DeadlineBO bo = new DeadlineBO();
			Deadline d = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditDeadlineWindow(d, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Datas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
