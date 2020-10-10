package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.EvaluationItemBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.EvaluationItemDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditEvaluationItemWindow;

@PageTitle("Quesitos de Avaliação")
@Route(value = "evaluationitem", layout = MainLayout.class)
public class EvaluationItemView extends ListView<EvaluationItemDataSource> {
	
	private final StageComboBox comboStage;
	private final Checkbox checkActive;
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	public EvaluationItemView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(EvaluationItemDataSource::getStage).setHeader("TCC").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(EvaluationItemDataSource::getType).setHeader("Avaliação").setFlexGrow(0).setWidth("200px");
		this.getGrid().addColumn(EvaluationItemDataSource::getDescription).setHeader("Quesito");
		this.getGrid().addColumn(EvaluationItemDataSource::getPonderosity).setHeader("Peso").setFlexGrow(0).setWidth("100px");
		
		this.comboStage = new StageComboBox(true);
		this.comboStage.selectBoth();
		
		this.checkActive = new Checkbox("Listar apenas quesitos ativos");
		this.checkActive.setValue(true);
		
		HorizontalLayout h = new HorizontalLayout(this.comboStage, this.checkActive);
		h.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, this.checkActive);
		
		this.addFilterField(h);
		
		this.buttonMoveUp = new Button("Para Cima", new Icon(VaadinIcon.ARROW_UP), event -> {
            moveUp();
        });
		this.buttonMoveUp.setEnabled(false);
    	
    	this.buttonMoveDown = new Button("Para Baixo", new Icon(VaadinIcon.ARROW_DOWN), event -> {
            moveDown();
        });
    	this.buttonMoveDown.setEnabled(false);
    	
    	this.addActionButton(this.buttonMoveUp);
    	this.addActionButton(this.buttonMoveDown);
	}

	@Override
	protected void loadGrid() {
		try {
			int stage = 0;
			
			if((this.comboStage != null) && (this.comboStage.getValue() != null) && !this.comboStage.getValue().toString().toLowerCase().equals("ambos")){
				stage = this.comboStage.getStage();
			}
			
			EvaluationItemBO bo = new EvaluationItemBO();
	    	List<EvaluationItem> list = bo.listByStage(stage, Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.checkActive.getValue());
	    	
	    	this.getGrid().setItems(EvaluationItemDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Quesitos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EvaluationItem item = new EvaluationItem();
		
		item.setDepartment(Session.getSelectedDepartment().getDepartment());
		
		if(!this.comboStage.getValue().toString().toLowerCase().equals("ambos")){
			item.setStage(this.comboStage.getStage());
		}
		
		EditEvaluationItemWindow window = new EditEvaluationItemWindow(item, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			EvaluationItemBO bo = new EvaluationItemBO();
			EvaluationItem item = bo.findById((int)id);
			
			EditEvaluationItemWindow window = new EditEvaluationItemWindow(item, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Quesito", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		try {
			EvaluationItemBO bo = new EvaluationItemBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Quesito", e.getMessage());
		}
	}
	
	private void moveUp(){
		Object value = getIdSelected();
    	
    	if(value != null){
    		try{
    			EvaluationItemBO bo = new EvaluationItemBO();
    			
    			bo.moveUp((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
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
    			EvaluationItemBO bo = new EvaluationItemBO();
    			
    			bo.moveDown((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Quesito", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Quesito", "Selecione o registro.");
    	}
	}

	@Override
	public void filterClick() throws Exception {
		this.buttonMoveUp.setEnabled(!this.comboStage.getValue().toString().toLowerCase().equals("ambos"));
		this.buttonMoveDown.setEnabled(!this.comboStage.getValue().toString().toLowerCase().equals("ambos"));
	}

}
