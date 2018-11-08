package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditSemesterWindow;

public class SemesterView extends ListView {
	
	public static final String NAME = "semester";
	
	private final CampusComboBox comboCampus;

	public SemesterView(){
		super(SystemModule.GENERAL);
		
		this.setCaption("Semestres");
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.comboCampus = new CampusComboBox();
		
		this.addFilterField(this.comboCampus);
		
    	this.setDeleteVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Início", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Fim", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(2).setWidth(125);
		this.getGrid().getColumns().get(3).setWidth(125);
		
		try {
			SemesterBO bo = new SemesterBO();
	    	List<Semester> list = bo.listByCampus(this.comboCampus.getCampus().getIdCampus());
	    	
	    	for(Semester d : list){
				Object itemId = this.getGrid().addRow(d.getSemester(), d.getYear(), d.getStartDate(), d.getEndDate());
				this.addRowId(itemId, String.valueOf(d.getCampus().getIdCampus()) + "-" + String.valueOf(d.getSemester()) + "-" + String.valueOf(d.getYear()));
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Datas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		Semester semester = new Semester();
		
		semester.setCampus(this.comboCampus.getCampus());
		
		UI.getCurrent().addWindow(new EditSemesterWindow(semester, this));
	}

	@Override
	public void editClick(Object id) {
		String values[] = id.toString().split("-");
		
		try {
			SemesterBO bo = new SemesterBO();
			Semester semester = bo.findBySemester(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
			
			UI.getCurrent().addWindow(new EditSemesterWindow(semester, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Semestre", e.getMessage());
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
