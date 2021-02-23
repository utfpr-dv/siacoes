package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipEvaluationItemBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipEvaluationItem;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.EvaluationItemDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipEvaluationItemWindow;

@PageTitle("Quesitos de Avaliação")
@Route(value = "internshipevaluationitem", layout = MainLayout.class)
public class InternshipEvaluationItemView extends ListView<EvaluationItemDataSource> {
	
	private final Checkbox checkActive;
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	public InternshipEvaluationItemView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(EvaluationItemDataSource::getType).setHeader("Avaliação").setFlexGrow(0).setWidth("200px");
		this.getGrid().addColumn(EvaluationItemDataSource::getDescription).setHeader("Quesito");
		this.getGrid().addColumn(EvaluationItemDataSource::getPonderosity).setHeader("Peso").setFlexGrow(0).setWidth("100px");
		
		this.checkActive = new Checkbox("Listar apenas quesitos ativos");
		this.checkActive.setValue(true);
		
		HorizontalLayout h = new HorizontalLayout(this.checkActive);
		
		this.addFilterField(h);
		
		this.buttonMoveUp = new Button("Para Cima", new Icon(VaadinIcon.ARROW_UP), event -> {
            moveUp();
        });
    	
    	this.buttonMoveDown = new Button("Para Baixo", new Icon(VaadinIcon.ARROW_DOWN), event -> {
            moveDown();
        });
    	
    	this.addActionButton(this.buttonMoveUp);
    	this.addActionButton(this.buttonMoveDown);
	}
	
	@Override
	protected void loadGrid() {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
	    	List<InternshipEvaluationItem> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.checkActive.getValue());
	    	
	    	this.getGrid().setItems(EvaluationItemDataSource.loadFromInternship(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Quesitos", e.getMessage());
		}
	}
	
	@Override
	public void addClick() {
		InternshipEvaluationItem item = new InternshipEvaluationItem();
		
		item.setDepartment(Session.getSelectedDepartment().getDepartment());
		
		EditInternshipEvaluationItemWindow window = new EditInternshipEvaluationItemWindow(item, this);
		window.open();
	}
	
	@Override
	public void editClick(int id) {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
			InternshipEvaluationItem item = bo.findById((int)id);
			
			EditInternshipEvaluationItemWindow window = new EditInternshipEvaluationItemWindow(item, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Quesito", e.getMessage());
		}
	}
	
	@Override
	public void deleteClick(int id) {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
			
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
    			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
    			
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
    			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
    			
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
		// TODO Auto-generated method stub
		
	}

}
