package br.edu.utfpr.dv.siacoes.ui.views;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Relatório de Estágios")
@Route(value = "internshipreport", layout = MainLayout.class)
public class InternshipReportView extends ReportView {
	
	public static final String NAME = "internshipreport";
	
	private static final String ALL = "(TODOS)";
	
	private final YearField textYear;
	private final StudentComboBox comboStudent;
	private final SupervisorComboBox comboProfessor;
	private final CompanyComboBox comboCompany;
	private final Select<String> comboStatus;
	private final Select<String> comboCompanyStatus;
	private final Select<String> comboType;
	private final DatePicker textStartDate1;
	private final DatePicker textStartDate2;
	private final DatePicker textEndDate1;
	private final DatePicker textEndDate2;
	
	public InternshipReportView() {
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.textYear = new YearField();
		this.textYear.setYear(0);
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.comboProfessor = new SupervisorComboBox("Orientador", Session.getSelectedDepartment().getDepartment().getIdDepartment(), SupervisorFilter.EVERYONE);
		
		this.comboCompany = new CompanyComboBox();
		
		this.comboStatus = new Select<String>();
		this.comboStatus.setLabel("Situação do Processo");
		this.comboStatus.setWidth("170px");
		this.comboStatus.setItems(InternshipStatus.CURRENT.toString(), InternshipStatus.FINISHED.toString(), ALL);
		this.comboStatus.setValue(InternshipStatus.CURRENT.toString());
		
		this.comboCompanyStatus = new Select<String>();
		this.comboCompanyStatus.setLabel("Situação na Empresa");
		this.comboCompanyStatus.setWidth("170px");
		this.comboCompanyStatus.setItems(InternshipStatus.CURRENT.toString(), InternshipStatus.FINISHED.toString(), ALL);
		this.comboCompanyStatus.setValue(InternshipStatus.CURRENT.toString());
		
		this.comboType = new Select<String>();
		this.comboType.setLabel("Tipo");
		this.comboType.setWidth("150px");
		this.comboType.setItems(InternshipType.NONREQUIRED.toString(), InternshipType.REQUIRED.toString(), ALL);
		this.comboType.setValue(ALL);
		
		this.textStartDate1 = new DatePicker("Início Entre");
		//this.textStartDate1.setDateFormat("dd/MM/yyyy");
		this.textStartDate2 = new DatePicker("E");
		//this.textStartDate2.setDateFormat("dd/MM/yyyy");
		
		this.textEndDate1 = new DatePicker("Término Entre");
		//this.textEndDate1.setDateFormat("dd/MM/yyyy");
		this.textEndDate2 = new DatePicker("E");
		//this.textEndDate2.setDateFormat("dd/MM/yyyy");
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboStudent, this.comboProfessor, this.comboCompany);
		h1.setSpacing(true);
		h1.setMargin(false);
		h1.setPadding(false);
		
		HorizontalLayout h2 = new HorizontalLayout(this.comboType, this.comboCompanyStatus, this.comboStatus);
		h2.setSpacing(true);
		h2.setMargin(false);
		h2.setPadding(false);
		
		HorizontalLayout h3 = new HorizontalLayout(this.textYear, this.textStartDate1, this.textStartDate2, this.textEndDate1, this.textEndDate2);
		h3.setSpacing(true);
		h3.setMargin(false);
		h3.setPadding(false);
		
		this.addFilterField(h1);
		this.addFilterField(h2);
		this.addFilterField(h3);
	}

	@Override
	public byte[] generateReport() throws Exception {
		int type = -1, status = -1, companyStatus = -1;
		
		if(!this.comboType.getValue().equals(ALL)){
			type = InternshipType.fromDescription(this.comboType.getValue()).getValue();
		}
		
		if(!this.comboStatus.getValue().equals(ALL)){
			status = InternshipStatus.fromDescription(this.comboStatus.getValue()).getValue();
		}
		
		if(!this.comboCompanyStatus.getValue().equals(ALL)){
			companyStatus = InternshipStatus.fromDescription(this.comboCompanyStatus.getValue()).getValue();
		}
		
		return new InternshipBO().getInternshipReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.textYear.getYear(), (this.comboStudent.getStudent() == null ? 0 : this.comboStudent.getStudent().getIdUser()), (this.comboProfessor.getProfessor() == null ? 0 : this.comboProfessor.getProfessor().getIdUser()), (this.comboCompany.getCompany() == null ? 0 : this.comboCompany.getCompany().getIdCompany()), type, status, DateUtils.convertToDate(this.textStartDate1.getValue()), DateUtils.convertToDate(this.textStartDate2.getValue()), DateUtils.convertToDate(this.textEndDate1.getValue()), DateUtils.convertToDate(this.textEndDate2.getValue()), companyStatus);
	}

}
