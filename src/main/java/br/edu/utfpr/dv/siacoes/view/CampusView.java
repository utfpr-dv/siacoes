package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditCampusWindow;

public class CampusView extends ListView {
	
	public static final String NAME = "campus";
	
	public CampusView(){
		super(SystemModule.GENERAL);
		
		this.setCaption("Câmpus");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("Ativo", String.class);
		
		this.getGrid().getColumns().get(1).setWidth(100);
		
		try{
			CampusBO bo = new CampusBO();
			List<Campus> list = bo.listAll(false);
			
			for(Campus c : list){
				Object itemId = this.getGrid().addRow(c.getName(), (c.isActive() ? "Sim" : "Não"));
				this.addRowId(itemId, c.getIdCampus());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Câmpus", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditCampusWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditCampusWindow(campus, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Câmpus", e.getMessage());
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
