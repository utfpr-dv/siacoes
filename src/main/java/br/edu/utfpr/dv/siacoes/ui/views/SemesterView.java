package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.SemesterDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditSemesterWindow;

@PageTitle("Semestres")
@Route(value = "semester", layout = MainLayout.class)
public class SemesterView extends ListView<SemesterDataSource> {
	
	private final CampusComboBox comboCampus;

	public SemesterView(){
		super(SystemModule.GENERAL);
		
		this.setProfilePerimissions(UserProfile.ADMINISTRATOR);
		
		this.getGrid().addColumn(SemesterDataSource::getSemester).setHeader("Semestre").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(SemesterDataSource::getYear).setHeader("Ano").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(SemesterDataSource::getStart).setHeader("Início").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(SemesterDataSource::getEnd).setHeader("Fim").setFlexGrow(0).setWidth("125px");
		
		this.comboCampus = new CampusComboBox();
		
		this.addFilterField(this.comboCampus);
		
    	this.setDeleteVisible(false);
	}
	
	@Override
	protected void loadGrid() {
		try {
			SemesterBO bo = new SemesterBO();
	    	List<Semester> list = bo.listByCampus(this.comboCampus.getCampus().getIdCampus());
	    	
	    	this.getGrid().setItems(SemesterDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Datas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		Semester semester = new Semester();
		
		semester.setCampus(this.comboCampus.getCampus());
		
		EditSemesterWindow window = new EditSemesterWindow(semester, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		int idCampus = SemesterDataSource.getIdCampusFromId(id);
		int year = SemesterDataSource.getYearFromId(id);
		int s = SemesterDataSource.getSemesterFromId(id);
		
		try {
			SemesterBO bo = new SemesterBO();
			Semester semester = bo.findBySemester(idCampus, s, year);
			
			EditSemesterWindow window = new EditSemesterWindow(semester, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Semestre", e.getMessage());
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
