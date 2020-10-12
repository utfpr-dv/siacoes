package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.ProposalAppraiserDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProposalAppraiserWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Parecer da Proposta de TCC 1")
@Route(value = "proposalfeedback", layout = MainLayout.class)
public class ProposalFeedbackView extends ListView<ProposalAppraiserDataSource> {
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonPrintFeedback;
	private final Button buttonDownloadProposal;
	
	public ProposalFeedbackView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.SUPERVISOR);
		
		this.getGrid().addColumn(ProposalAppraiserDataSource::getSemester).setHeader("Semestre").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ProposalAppraiserDataSource::getYear).setHeader("Ano").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ProposalAppraiserDataSource::getTitle, "Title").setHeader("Título");
		this.getGrid().addColumn(ProposalAppraiserDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(new LocalDateRenderer<>(ProposalAppraiserDataSource::getSubmission, "dd/MM/yyyy"), "Submission").setHeader("Submissão").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(ProposalAppraiserDataSource::getFeedback, "Feedback").setHeader("Parecer").setFlexGrow(0).setWidth("150px");
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Emitir Parecer");
		this.setEditIcon(new Icon(VaadinIcon.CHECK));
		
		this.buttonPrintFeedback = new Button("Imprimir Parecer", new Icon(VaadinIcon.PRINT), event -> {
            printFeedback();
        });
		this.buttonPrintFeedback.setWidth("150px");
		
		this.buttonDownloadProposal = new Button("Baixar Proposta", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadProposal();
        });
		this.buttonDownloadProposal.setWidth("150px");
		
		this.addActionButton(this.buttonPrintFeedback);
		this.addActionButton(this.buttonDownloadProposal);
		
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
	}
	
	protected void loadGrid(){
		try {
	    	List<ProposalAppraiser> list = new ProposalAppraiserBO().listByAppraiser(Session.getUser().getIdUser(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	this.getGrid().setItems(ProposalAppraiserDataSource.load(list));
		} catch (Exception e) {
			e.printStackTrace();
			
			this.showErrorNotification("Listar Propostas", e.getMessage());
		}
    }
	
	private void downloadProposal() {
		Object value = getIdSelected();
		
		if(value != null) {
			try {
            	Proposal proposal = new ProposalBO().findById((int)value);
            	SigetConfig config = new SigetConfigBO().findByDepartment(proposal.getDepartment().getIdDepartment());
            	
            	if(config.isSupervisorAgreement() && (proposal.getSupervisorFeedback() != ProposalFeedback.APPROVED)) {
            		this.showWarningNotification("Download da Proposta", "O Professor Orientador ainda não preencheu o Termo de Concordância de Orientação.");
            	} else if(proposal.getFile() != null) {
            		this.showReport(proposal.getFile());
            	} else {
            		this.showWarningNotification("Download da Proposta", "O acadêmico ainda não efetuou a submissão da proposta.");
            	}
        	} catch (Exception e) {
            	Logger.log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Download da Proposta", e.getMessage());
			}
		} else {
			this.showWarningNotification("Download da Proposta", "Selecione um registro para efetuar do download da proposta.");
		}
	}
	
	private void printFeedback() {
		Object value = getIdSelected();
		
		if(value != null){
			try {
				ProposalAppraiserBO bo = new ProposalAppraiserBO();
				ProposalAppraiser appraiser = bo.findByAppraiser((int)value, Session.getUser().getIdUser());
				
				if(appraiser.getFeedback() == ProposalFeedback.NONE) {
					this.showWarningNotification("Imprimir Parecer", "É necessário preencher o parecer antes de imprimir.");
				} else {
					this.showReport(bo.getFeedbackReport(appraiser));	
				}
			} catch (Exception e) {
            	Logger.log(Level.SEVERE, e.getMessage(), e);
            	
            	this.showErrorNotification("Imprimir Parecer", e.getMessage());
			}
		} else {
			this.showWarningNotification("Imprimir Parecer", "Selecione um registro para imprimir o parecer.");
		}
	}
	
	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			ProposalAppraiserBO bo = new ProposalAppraiserBO();
			ProposalAppraiser appraiser = bo.findByAppraiser((int)id, Session.getUser().getIdUser());
			
			EditProposalAppraiserWindow window = new EditProposalAppraiserWindow(appraiser, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Cadastrar Parecer", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() {
		// TODO Auto-generated method stub
		
	}

}
