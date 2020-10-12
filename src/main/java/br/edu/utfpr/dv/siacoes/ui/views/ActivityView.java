package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Activity;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.ActivityDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditActivityWindow;

@PageTitle("Atividades")
@Route(value = "activity", layout = MainLayout.class)
public class ActivityView extends ListView<ActivityDataSource> {
	
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	public ActivityView(){
		super(SystemModule.SIGAC);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(ActivityDataSource::getGroup, "Group").setHeader("Grupo").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ActivityDataSource::getDescription, "Description").setHeader("Descrição");
		this.getGrid().addColumn(ActivityDataSource::getScore).setHeader("Pontuação").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(ActivityDataSource::getUnit).setHeader("Unidade").setFlexGrow(0).setWidth("200px");
		
		this.buttonMoveUp = new Button("Para Cima", new Icon(VaadinIcon.ARROW_UP), event -> {
            moveUp();
        });
    	
    	this.buttonMoveDown = new Button("Para Baixo", new Icon(VaadinIcon.ARROW_DOWN), event -> {
            moveDown();
        });
    	
    	this.addActionButton(this.buttonMoveUp);
    	this.addActionButton(this.buttonMoveDown);
		
		this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try{
			ActivityBO bo = new ActivityBO();
			List<Activity> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			this.getGrid().setItems(ActivityDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Atividades", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditActivityWindow window = new EditActivityWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			ActivityBO bo = new ActivityBO();
			Activity activity = bo.findById(id);
			
			EditActivityWindow window = new EditActivityWindow(activity, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Atividade", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
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
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
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
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Atividade", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Atividade", "Selecione o registro.");
    	}
	}

}
