package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.model.Activity;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditActivityWindow;

public class ActivityView extends ListView {
	
	public static final String NAME = "activity";
	
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	public ActivityView(){
		super(SystemModule.SIGAC);
		
		this.setCaption("Atividades");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.buttonMoveUp = new Button("Para Cima", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	moveUp();
            }
        });
		this.buttonMoveUp.setIcon(FontAwesome.ARROW_UP);
    	
    	this.buttonMoveDown = new Button("Para Baixo", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	moveDown();
            }
        });
    	this.buttonMoveDown.setIcon(FontAwesome.ARROW_DOWN);
    	
    	this.addActionButton(this.buttonMoveUp);
    	this.addActionButton(this.buttonMoveDown);
		
		this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Grupo", Integer.class);
		this.getGrid().addColumn("Descrição", String.class);
		this.getGrid().addColumn("Pontuação", Double.class);
		this.getGrid().addColumn("Unidade", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(2).setWidth(125);
		this.getGrid().getColumns().get(3).setWidth(200);
		
		try{
			ActivityBO bo = new ActivityBO();
			List<Activity> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			for(Activity activity : list){
				Object itemId = this.getGrid().addRow(activity.getGroup().getSequence(), activity.getDescription(), activity.getScore(), activity.getUnit().getDescription());
				this.addRowId(itemId, activity.getIdActivity());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Atividades", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditActivityWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			ActivityBO bo = new ActivityBO();
			Activity activity = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditActivityWindow(activity, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Atividade", e.getMessage());
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
    			ActivityBO bo = new ActivityBO();
    			
    			bo.moveUp((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Atividade", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Atividade", "Selecione o registro.");
    	}
	}
	
	private void moveDown(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			ActivityBO bo = new ActivityBO();
    			
    			bo.moveDown((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Atividade", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Atividade", "Selecione o registro.");
    	}
	}

}
