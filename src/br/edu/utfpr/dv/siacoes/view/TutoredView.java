package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.TutoredBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Tutored;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditTutoredWindow;

public class TutoredView extends ListView {
	
	public static final String NAME = "tutored";
	
	public TutoredView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.PROFESSOR);
		
		this.setFiltersVisible(false);
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Visualizar");
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Aluno", String.class);
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Sem.", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		
		this.getGrid().getColumns().get(1).setWidth(75);
		this.getGrid().getColumns().get(3).setWidth(75);
		this.getGrid().getColumns().get(4).setWidth(100);
		
		try {
			TutoredBO bo = new TutoredBO();
	    	List<Tutored> list = bo.listBySupervisor(Session.getUser().getIdUser());
	    	
	    	for(Tutored t : list){
				Object itemId = this.getGrid().addRow(t.getStudent().getName(), t.getStage(), t.getTitle(), t.getSemester(), t.getYear());
				this.addRowId(itemId, t.getProposal().getIdProposal());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Orientados", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		UI.getCurrent().addWindow(new EditTutoredWindow((int)id, this));
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
