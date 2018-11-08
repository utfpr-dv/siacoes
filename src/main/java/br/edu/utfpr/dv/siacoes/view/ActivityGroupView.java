package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditActivityGroupWindow;

public class ActivityGroupView extends ListView {
	
	public static final String NAME = "activitygroup";
	
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	public ActivityGroupView(){
		super(SystemModule.SIGAC);
		
		this.setCaption("Grupos de Atividades");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.buttonMoveUp = new Button("Para Cima", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	moveUp();
            }
        });
    	
    	this.buttonMoveDown = new Button("Para Baixo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	moveDown();
            }
        });
    	
    	this.addActionButton(this.buttonMoveUp);
    	this.addActionButton(this.buttonMoveDown);
		
    	this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Descrição", String.class);
		this.getGrid().addColumn("Mínimo", Integer.class);
		this.getGrid().addColumn("Máximo", Integer.class);
		
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(2).setWidth(100);
		
		try {
			ActivityGroupBO bo = new ActivityGroupBO();
			List<ActivityGroup> list = bo.listAll();
			
			for(ActivityGroup group : list){
				Object itemId = this.getGrid().addRow(group.getDescription(), group.getMinimumScore(), group.getMaximumScore());
				this.addRowId(itemId, group.getIdActivityGroup());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Grupos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditActivityGroupWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try {
			ActivityGroupBO bo = new ActivityGroupBO();
			ActivityGroup group = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditActivityGroupWindow(group, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Grupo", e.getMessage());
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
	
	private void moveUp(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			ActivityGroupBO bo = new ActivityGroupBO();
    			
    			bo.moveUp((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Grupo", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Grupo", "Selecione o registro.");
    	}
	}
	
	private void moveDown(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			ActivityGroupBO bo = new ActivityGroupBO();
    			
    			bo.moveDown((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Grupo", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Grupo", "Selecione o registro.");
    	}
	}

}
