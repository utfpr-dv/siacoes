package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.DepartmentDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditDepartmentWindow;

@PageTitle("Departamentos")
@Route(value = "department", layout = MainLayout.class)
public class DepartmentView extends ListView<DepartmentDataSource> {
	
	private final CampusComboBox comboCampus;
	
	public DepartmentView(){
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.getGrid().addColumn(DepartmentDataSource::getName).setHeader("Nome");
		this.getGrid().addColumn(DepartmentDataSource::getActive).setHeader("Ativo").setFlexGrow(0).setWidth("100px");
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setFilterOnlyActives(false);
		
		this.addFilterField(this.comboCampus);
		
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try{
			DepartmentBO bo = new DepartmentBO();
			List<Department> list = bo.listByCampus((this.comboCampus.getCampus() == null ? 0 : this.comboCampus.getCampus().getIdCampus()), false);
			
			this.getGrid().setItems(DepartmentDataSource.load(list));
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Departamentos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		Department department = new Department();
		
		department.setCampus(this.comboCampus.getCampus());
		
		EditDepartmentWindow window = new EditDepartmentWindow(department, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try{
			DepartmentBO bo = new DepartmentBO();
			Department department = bo.findById((int)id);
			
			EditDepartmentWindow window = new EditDepartmentWindow(department, this);
			window.open();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Departamento", e.getMessage());
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
