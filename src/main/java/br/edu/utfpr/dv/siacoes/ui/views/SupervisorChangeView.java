package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.SupervisorChangeDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditSupervisorChangeWindow;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;

@PageTitle("Alterações de Orientador")
@Route(value = "supervisorchange", layout = MainLayout.class)
public class SupervisorChangeView extends ListView<SupervisorChangeDataSource> {
	
	private final Checkbox checkOnlyPending;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	
	public SupervisorChangeView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(SupervisorChangeDataSource::getStage).setHeader("TCC").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(SupervisorChangeDataSource::getStudent).setHeader("Acadêmico");
		this.getGrid().addColumn(SupervisorChangeDataSource::getDate).setHeader("Data").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(SupervisorChangeDataSource::getOldSupervisor).setHeader("Orientador Anterior");
		this.getGrid().addColumn(SupervisorChangeDataSource::getNewSupervisor).setHeader("Novo Orientador");
		this.getGrid().addColumn(SupervisorChangeDataSource::getStatus).setHeader("Situação").setFlexGrow(0).setWidth("150px");
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setEditCaption("Aprovar");
		this.setEditIcon(new Icon(VaadinIcon.CHECK));
		
		this.checkOnlyPending = new Checkbox("Apenas pendentes");
		this.checkOnlyPending.setValue(true);
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.checkOnlyPending));
	}
	
	@Override
	protected void loadGrid() {
		try {
			SupervisorChangeBO bo = new SupervisorChangeBO();
			List<SupervisorChange> list = bo.list(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.checkOnlyPending.getValue());
			
			this.getGrid().setItems(SupervisorChangeDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Alterações", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try{
			SupervisorChangeBO bo = new SupervisorChangeBO();
			
			SupervisorChange change = bo.findById((int)id);
			
			EditSupervisorChangeWindow window = new EditSupervisorChangeWindow(change, this);
			window.open();
		}catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Alteração de Orientador", e.getMessage());
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
