package br.edu.utfpr.dv.siacoes.ui.views;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.FinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.FinalDocument;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.FinalDocumentDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Versão Final do TCC")
@Route(value = "finaldocument", layout = MainLayout.class)
public class FinalDocumentView extends ListView<FinalDocumentDataSource> {
	
	private final Checkbox checkListAll;
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonGrades;
	private final Button buttonLibraryReport;
	
	private final Anchor anchorLibraryReport;
	
	public FinalDocumentView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.PROFESSOR);
		
		this.getGrid().addColumn(FinalDocumentDataSource::getStage, "Stage").setHeader("TCC").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(FinalDocumentDataSource::getSemester).setHeader("Semestre").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(FinalDocumentDataSource::getYear).setHeader("Ano").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(FinalDocumentDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(FinalDocumentDataSource::getTitle).setHeader("Título");
		this.getGrid().addColumn(new LocalDateRenderer<>(FinalDocumentDataSource::getSubmission, "dd/MM/yyyy"), "Submission").setHeader("Submissão").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(FinalDocumentDataSource::getPrivate).setHeader("Sigilo").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(FinalDocumentDataSource::getSupervisorFeedback, "Feedback").setHeader("Feedback").setFlexGrow(0).setWidth("125px");
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.setValue(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.checkListAll = new Checkbox("Listar Todos");
		
		this.buttonLibraryReport = new Button("Relat. Biblioteca");
		this.buttonLibraryReport.setIcon(new Icon(VaadinIcon.FILE_ZIP));
		
		this.anchorLibraryReport = new Anchor();
    	this.anchorLibraryReport.getElement().setAttribute("download", true);
    	this.anchorLibraryReport.add(this.buttonLibraryReport);
		
		this.buttonGrades = new Button("Relat. de Notas", new Icon(VaadinIcon.FILE_TEXT_O), event -> {
            juryGradesReport();
        });
		
		if(!Session.isUserManager(this.getModule())){
			this.checkListAll.setVisible(false);
			
			this.buttonGrades.setVisible(false);
			this.buttonLibraryReport.setVisible(false);
		}
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.checkListAll));
		
		this.addActionButton(this.buttonGrades);
		this.addActionButton(this.anchorLibraryReport);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setEditCaption("Validar");
		this.setEditIcon(new Icon(VaadinIcon.CHECK));
	}

	@Override
	protected void loadGrid() {
		try{
			FinalDocumentBO bo = new FinalDocumentBO();
			List<FinalDocument> list;
			
			if(this.checkListAll.getValue()) {
				list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), true);
				this.anchorLibraryReport.setHref(new StreamResource("RelatorioBiblioteca.zip", this::makeDownloadAll));
				this.buttonLibraryReport.setEnabled(true);
			} else {
				list = bo.listBySupervisor(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
				this.buttonLibraryReport.setEnabled(false);
			}
			
			this.getGrid().setItems(FinalDocumentDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Projetos/Monografias", e.getMessage());
		}
	}
	
	private InputStream makeDownloadAll() {
		try {
	    	byte[] data = new FinalDocumentBO().getLibraryReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textYear.getYear(), this.comboSemester.getSemester());
	    	
	    	return new ByteArrayInputStream(data);
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Relatório para a Biblioteca", e.getMessage());
			
			return null;
		}
	}
	
	private void juryGradesReport() {
		try {
			this.showReport(new JuryBO().getJuryGradesReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), 0, false));
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
        	
			this.showErrorNotification("Relatório de Notas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			FinalDocumentBO bo = new FinalDocumentBO();
			FinalDocument doc = bo.findById((int)id);
			
			EditFinalDocumentWindow window = new EditFinalDocumentWindow(doc, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Validar Projeto/Monografia", e.getMessage());
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
