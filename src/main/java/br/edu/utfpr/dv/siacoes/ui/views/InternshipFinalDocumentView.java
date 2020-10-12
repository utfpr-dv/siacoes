package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipFinalDocumentDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipFinalDocumentWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Versão Final do Relatório de Estágio")
@Route(value = "internshipfinaldocument", layout = MainLayout.class)
public class InternshipFinalDocumentView extends ListView<InternshipFinalDocumentDataSource> {
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	
	public InternshipFinalDocumentView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(InternshipFinalDocumentDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(InternshipFinalDocumentDataSource::getCompany, "Company").setHeader("Empresa");
		this.getGrid().addColumn(new LocalDateRenderer<>(InternshipFinalDocumentDataSource::getSubmission, "dd/MM/yyyy"), "Submission").setHeader("Submissão").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(InternshipFinalDocumentDataSource::getPrivate).setHeader("Sigilo").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(InternshipFinalDocumentDataSource::getFeedback, "Feedback").setHeader("Feedback").setFlexGrow(0).setWidth("125px");
		
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
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setEditCaption("Validar");
		this.setEditIcon(new Icon(VaadinIcon.CHECK));
	}

	@Override
	protected void loadGrid() {
		try{
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
			List<InternshipFinalDocument> list;
			
			list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), true);
			
			this.getGrid().setItems(InternshipFinalDocumentDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Relatórios de Estágio", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
			InternshipFinalDocument doc = bo.findById((int)id);
			
			EditInternshipFinalDocumentWindow window = new EditInternshipFinalDocumentWindow(doc, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Validar Relatório de Estágio", e.getMessage());
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
