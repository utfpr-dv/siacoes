package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipStatus;
import br.edu.utfpr.dv.siacoes.model.Internship.InternshipType;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.SigetConfig.SupervisorFilter;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.CompanyComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;

@PageTitle("Relatório de Documentos de Estágio Faltantes")
@Route(value = "internshipmissingdocumentsreport", layout = MainLayout.class)
public class InternshipMissingDocumentsReportView extends ReportView {

	private final YearField textYear;
	private final StudentComboBox comboStudent;
	private final SupervisorComboBox comboProfessor;
	private final CompanyComboBox comboCompany;
	private final Select<String> comboStatus;
	private final Select<String> comboType;
	private final Checkbox checkFinalReportMissing;
	
	private static final String ALL = "(TODOS)";
	
	public InternshipMissingDocumentsReportView(){
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.textYear = new YearField();
		this.textYear.setYear(0);
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.comboProfessor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.comboCompany = new CompanyComboBox();
		
		this.comboStatus = new Select<String>();
		this.comboStatus.setLabel("Situação");
		this.comboStatus.setWidth("195px");
		this.comboStatus.setItems(InternshipStatus.CURRENT.toString(), InternshipStatus.FINISHED.toString(), ALL);
		this.comboStatus.setValue(InternshipStatus.CURRENT.toString());
		
		this.comboType = new Select<String>();
		this.comboType.setLabel("Tipo");
		this.comboType.setWidth("195px");
		this.comboType.setItems(InternshipType.NONREQUIRED.toString(), InternshipType.REQUIRED.toString(), ALL);
		this.comboType.setValue(ALL);
		
		this.checkFinalReportMissing = new Checkbox("Estágio sem Relatório Final");
		
		HorizontalLayout h2 = new HorizontalLayout(this.textYear, this.checkFinalReportMissing);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		VerticalLayout v1 = new VerticalLayout(this.comboStudent, this.comboCompany, h2);
		v1.setSpacing(false);
		v1.setMargin(false);
		v1.setPadding(false);
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboType, this.comboStatus);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		
		VerticalLayout v2 = new VerticalLayout(this.comboProfessor, h1);
		v2.setSpacing(false);
		v2.setMargin(false);
		v2.setPadding(false);
		
		this.addFilterField(new HorizontalLayout(v1, v2));
	}
	
	@Override
	public byte[] generateReport() throws Exception {
		int type = -1, status = -1;
		
		if(!this.comboType.getValue().equals(ALL)){
			type = InternshipType.valueOf(this.comboType.getValue()).getValue();
		}
		
		if(!this.comboStatus.getValue().equals(ALL)){
			status = InternshipStatus.valueOf(this.comboStatus.getValue()).getValue();
		}
		
		InternshipBO bo = new InternshipBO();
		return bo.getMissingDocumentsReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textYear.getYear(), (this.comboStudent.getStudent() == null ? 0 : this.comboStudent.getStudent().getIdUser()), (this.comboProfessor.getProfessor() == null ? 0 : this.comboProfessor.getProfessor().getIdUser()), (this.comboCompany.getCompany() == null ? 0 : this.comboCompany.getCompany().getIdCompany()), type, status, this.checkFinalReportMissing.getValue());
	}

}
