package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipFinalDocumentBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.InternshipFinalDocument;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditInternshipFinalDocumentWindow;

public class InternshipFinalDocumentView extends ListView {

	public static final String NAME = "internshipfinaldocument";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	
	public InternshipFinalDocumentView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Versão Final do Relatório de Estágio");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		this.setEditCaption("Validar");
		this.setEditIcon(FontAwesome.CHECK);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addColumn("Submissão", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Sigilo", String.class);
		this.getGrid().addColumn("Feedback", String.class);
		
		this.getGrid().getColumns().get(2).setWidth(125);
		this.getGrid().getColumns().get(3).setWidth(100);
		this.getGrid().getColumns().get(4).setWidth(125);
		
		try{
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
			List<InternshipFinalDocument> list;
			
			list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear(), true);
			
			for(InternshipFinalDocument doc : list){
				Object itemId = this.getGrid().addRow(doc.getInternship().getStudent().getName(), doc.getInternship().getCompany().getName(), doc.getSubmissionDate(), (doc.isPrivate() ? "Sim" : "Não"), doc.getSupervisorFeedback().toString());
				
				this.addRowId(itemId, doc.getIdInternshipFinalDocument());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Relatórios de Estágio", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			InternshipFinalDocumentBO bo = new InternshipFinalDocumentBO();
			InternshipFinalDocument doc = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditInternshipFinalDocumentWindow(doc, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Validar Relatório de Estágio", e.getMessage());
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
