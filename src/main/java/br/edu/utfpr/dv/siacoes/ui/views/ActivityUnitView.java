package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.ActivityUnitDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditActivityUnitWindow;

@PageTitle("Unidades de Atividades")
@Route(value = "activityunit", layout = MainLayout.class)
public class ActivityUnitView extends ListView<ActivityUnitDataSource> {
	
	public ActivityUnitView(){
		super(SystemModule.SIGAC);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.getGrid().addColumn(ActivityUnitDataSource::getDescription).setHeader("Descrição");
		this.getGrid().addColumn(ActivityUnitDataSource::getFillAmount).setHeader("Inform. Qtde.").setFlexGrow(0).setWidth("150px");
		
		this.setFiltersVisible(false);
    	this.setDeleteVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		try{
			ActivityUnitBO bo = new ActivityUnitBO();
			List<ActivityUnit> list = bo.listAll();
			
			this.getGrid().setItems(ActivityUnitDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Unidades", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		EditActivityUnitWindow window = new EditActivityUnitWindow(null, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			ActivityUnitBO bo = new ActivityUnitBO();
			ActivityUnit unit = bo.findById(id);
			
			EditActivityUnitWindow window = new EditActivityUnitWindow(unit, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Unidade", e.getMessage());
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

}
