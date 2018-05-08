package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipEvaluationItemBO;
import br.edu.utfpr.dv.siacoes.model.InternshipEvaluationItem;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditInternshipEvaluationItemWindow;

public class InternshipEvaluationItemView extends ListView {
	
	public static final String NAME = "internshipevaluationitem";
	
	private final CheckBox checkActive;
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	public InternshipEvaluationItemView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Quesitos de Avaliação");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.checkActive = new CheckBox("Listar apenas quesitos ativos");
		this.checkActive.setValue(true);
		
		HorizontalLayout h = new HorizontalLayout(this.checkActive);
		h.setComponentAlignment(this.checkActive, Alignment.MIDDLE_LEFT);
		
		this.addFilterField(h);
		
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
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Avaliação", String.class);
		this.getGrid().addColumn("Quesito", String.class);
		this.getGrid().addColumn("Peso", Double.class);
		
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
	    	List<InternshipEvaluationItem> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.checkActive.getValue());
	    	
	    	for(InternshipEvaluationItem item : list){
				Object itemId = this.getGrid().addRow(item.getType().toString(), item.getDescription(), item.getPonderosity());
				this.addRowId(itemId, item.getIdInternshipEvaluationItem());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Quesitos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void addClick() {
		InternshipEvaluationItem item = new InternshipEvaluationItem();
		
		item.setDepartment(Session.getSelectedDepartment().getDepartment());
		
		UI.getCurrent().addWindow(new EditInternshipEvaluationItemWindow(item, this));
	}
	
	@Override
	public void editClick(Object id) {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
			InternshipEvaluationItem item = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditInternshipEvaluationItemWindow(item, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Quesito", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void deleteClick(Object id) {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
			
			bo.delete((int)id);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Excluir Quesito", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void moveUp(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
    			
    			bo.moveUp((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			Notification.show("Mover Quesito", e.getMessage(), Notification.Type.ERROR_MESSAGE);
    		}
    	}else{
    		Notification.show("Mover Quesito", "Selecione o registro.", Notification.Type.WARNING_MESSAGE);
    	}
	}
	
	private void moveDown(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
    			
    			bo.moveDown((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
    			
    			Notification.show("Mover Quesito", e.getMessage(), Notification.Type.ERROR_MESSAGE);
    		}
    	}else{
    		Notification.show("Mover Quesito", "Selecione o registro.", Notification.Type.WARNING_MESSAGE);
    	}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
