package br.edu.utfpr.dv.siacoes.view;

import com.vaadin.ui.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class ProposalFeedbackReportView extends ReportView {
	
	public static final String NAME = "proposalfeedbackreport";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	
	public ProposalFeedbackReportView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		this.setCaption("Relatório de Avaliação de Proposta de TCC 1");
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
				
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
	}

	@Override
	public byte[] generateReport() throws Exception {
		return new ProposalBO().getProposalFeedbackReport(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	}

}
