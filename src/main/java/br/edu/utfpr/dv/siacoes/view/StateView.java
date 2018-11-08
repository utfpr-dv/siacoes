package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.StateBO;
import br.edu.utfpr.dv.siacoes.model.State;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditStateWindow;

public class StateView extends ListView {

	public static final String NAME = "state";
	
	public StateView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Estados");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setFiltersVisible(false);
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("País", String.class);
		this.getGrid().addColumn("Sigla", String.class);
		
		this.getGrid().getColumns().get(2).setWidth(100);
		
		try{
			StateBO bo = new StateBO();
			List<State> list = bo.listAll();
			
			for(State c : list){
				Object itemId = this.getGrid().addRow(c.getName(), c.getCountry().getName(), c.getInitials());
				this.addRowId(itemId, c.getIdState());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Estados", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditStateWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			StateBO bo = new StateBO();
			State state = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditStateWindow(state, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Estado", e.getMessage());
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
