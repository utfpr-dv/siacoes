package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditDepartmentWindow;

public class DepartmentView extends ListView {

	public static final String NAME = "department";
	
	private final CampusComboBox comboCampus;
	
	public DepartmentView(){
		super(SystemModule.GENERAL);
		
		this.setCaption("Departamentos");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setFilterOnlyActives(false);
		
		this.addFilterField(this.comboCampus);
		
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("Ativo", String.class);
		
		this.getGrid().getColumns().get(1).setWidth(100);
		
		try{
			DepartmentBO bo = new DepartmentBO();
			List<Department> list = bo.listByCampus((this.comboCampus.getCampus() == null ? 0 : this.comboCampus.getCampus().getIdCampus()), false);
			
			for(Department d : list){
				Object itemId = this.getGrid().addRow(d.getName(), (d.isActive() ? "Sim" : "Não"));
				this.addRowId(itemId, d.getIdDepartment());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Departamentos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		Department department = new Department();
		
		department.setCampus(this.comboCampus.getCampus());
		
		UI.getCurrent().addWindow(new EditDepartmentWindow(department, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			DepartmentBO bo = new DepartmentBO();
			Department department = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditDepartmentWindow(department, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Departamento", e.getMessage());
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
	
}
