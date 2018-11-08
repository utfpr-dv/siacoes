package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditActivityUnitWindow;

public class ActivityUnitView extends ListView {

	public static final String NAME = "activityunit";
	
	public ActivityUnitView(){
		super(SystemModule.SIGAC);
		
		this.setCaption("Unidades de Atividades");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Descrição", String.class);
		this.getGrid().addColumn("Inform. Qtde.", String.class);
		
		this.getGrid().getColumns().get(1).setWidth(150);
		
		try{
			ActivityUnitBO bo = new ActivityUnitBO();
			List<ActivityUnit> list = bo.listAll();
			
			for(ActivityUnit unit : list){
				Object itemId = this.getGrid().addRow(unit.getDescription(), (unit.isFillAmount() ? "Sim" : "Não"));
				this.addRowId(itemId, unit.getIdActivityUnit());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Unidades", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditActivityUnitWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			ActivityUnitBO bo = new ActivityUnitBO();
			ActivityUnit unit = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditActivityUnitWindow(unit, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Unidade", e.getMessage());
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
