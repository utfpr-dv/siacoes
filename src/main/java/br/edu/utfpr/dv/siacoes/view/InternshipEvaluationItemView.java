package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
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
			
			this.showErrorNotification("Listar Quesitos", e.getMessage());
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
			
			this.showErrorNotification("Editar Quesito", e.getMessage());
		}
	}
	
	@Override
	public void deleteClick(Object id) {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Quesito", e.getMessage());
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
    			
    			this.showErrorNotification("Mover Quesito", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Quesito", "Selecione o registro.");
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
    			
    			this.showErrorNotification("Mover Quesito", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Quesito", "Selecione o registro.");
    	}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
