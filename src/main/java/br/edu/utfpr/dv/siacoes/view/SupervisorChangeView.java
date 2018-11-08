package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.window.EditSupervisorChangeWindow;

public class SupervisorChangeView extends ListView {

	public static final String NAME = "supervisorchange";
	
	private final CheckBox checkOnlyPending;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	
	public SupervisorChangeView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Alterações de Orientador");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setEditCaption("Aprovar");
		this.setEditIcon(FontAwesome.CHECK);
		
		this.checkOnlyPending = new CheckBox("Apenas pendentes");
		this.checkOnlyPending.setValue(true);
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.checkOnlyPending));
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Data", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Orientador Anterior", String.class);
		this.getGrid().addColumn("Novo Orientador", String.class);
		this.getGrid().addColumn("Situação", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(75);
		this.getGrid().getColumns().get(2).setWidth(125);
		this.getGrid().getColumns().get(5).setWidth(150);
		
		try {
			SupervisorChangeBO bo = new SupervisorChangeBO();
			List<SupervisorChange> list = bo.list(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), this.checkOnlyPending.getValue());
			
			for(SupervisorChange change : list){
				Object itemId = this.getGrid().addRow(change.getStage(), change.getProposal().getStudent().getName(), change.getDate(), change.getOldSupervisor().getName(), change.getNewSupervisor().getName(), change.getApproved().toString());
				this.addRowId(itemId, change.getIdSupervisorChange());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Alterações", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try{
			SupervisorChangeBO bo = new SupervisorChangeBO();
			
			SupervisorChange change = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditSupervisorChangeWindow(change, this));
		}catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Alteração de Orientador", e.getMessage());
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
