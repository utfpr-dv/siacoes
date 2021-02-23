package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.ActivityGroupDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditActivityGroupWindow;

@PageTitle("Grupos de Atividades")
@Route(value = "activitygroup", layout = MainLayout.class)
public class ActivityGroupView extends ListView<ActivityGroupDataSource> {
	
	private final Button buttonMoveUp;
	private final Button buttonMoveDown;
	
	public ActivityGroupView(){
		super(SystemModule.SIGAC);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.buttonMoveUp = new Button("Para Cima", event -> {
			moveUp();
        });
    	
    	this.buttonMoveDown = new Button("Para Baixo", event -> {
    		moveDown();
        });
    	
    	this.addActionButton(this.buttonMoveUp);
    	this.addActionButton(this.buttonMoveDown);
    	
    	this.getGrid().addColumn(ActivityGroupDataSource::getDescription).setHeader("Descrição");
		this.getGrid().addColumn(ActivityGroupDataSource::getMinimum).setHeader("Mínimo").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ActivityGroupDataSource::getMaximum).setHeader("Máximo").setFlexGrow(0).setWidth("100px");
		
    	this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try {
			ActivityGroupBO bo = new ActivityGroupBO();
			List<ActivityGroup> list = bo.listAll();
			
			this.getGrid().setItems(ActivityGroupDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Grupos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditActivityGroupWindow window = new EditActivityGroupWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			ActivityGroupBO bo = new ActivityGroupBO();
			ActivityGroup group = bo.findById(id);
			
			EditActivityGroupWindow window = new EditActivityGroupWindow(group, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Grupo", e.getMessage());
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
    			ActivityGroupBO bo = new ActivityGroupBO();
    			
    			bo.moveUp((int)value);
    			this.refreshGrid();
    		}catch(Exception e){
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
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
    			Logger.log(Level.SEVERE, e.getMessage(), e);
    			
    			this.showErrorNotification("Mover Grupo", e.getMessage());
    		}
    	}else{
    		this.showWarningNotification("Mover Grupo", "Selecione o registro.");
    	}
	}

}
