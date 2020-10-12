package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.ProposalDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditProposalWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Propostas de TCC 1")
@Route(value = "proposal", layout = MainLayout.class)
public class ProposalView extends ListView<ProposalDataSource> {
	
	private SigetConfig config;
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonDownload;
	private final Button buttonInvalidate;
	private final Button buttonCloseFeedback;
	
	public ProposalView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(ProposalDataSource::getSemester).setHeader("Semestre").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ProposalDataSource::getYear).setHeader("Ano").setFlexGrow(0).setWidth("100px");
		this.getGrid().addColumn(ProposalDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(ProposalDataSource::getSupervisor, "Supervisor").setHeader("Orientador");
		this.getGrid().addColumn(ProposalDataSource::getTitle, "Title").setHeader("Título");
		this.getGrid().addColumn(new LocalDateRenderer<>(ProposalDataSource::getSubmission, "dd/MM/yyyy"), "Submission").setHeader("Registro").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(ProposalDataSource::getHasFile, "HasFile").setHeader("Prop. Env.").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(ProposalDataSource::getSupervisorFeedback, "SupervisorFeedback").setHeader("Feedback Orient.").setFlexGrow(0).setWidth("125px");
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.config = new SigetConfig();
		try {
			this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!this.config.isSupervisorAgreement()) {
			this.getGrid().getColumns().get(7).setVisible(false);
		}
		
		this.buttonDownload = new Button("Down. da Proposta", new Icon(VaadinIcon.CLOUD_DOWNLOAD), event -> {
            downloadFile();
        });
		if(this.config.isRegisterProposal()) {
			this.addActionButton(this.buttonDownload);
		}
		
		this.buttonInvalidate = new Button("Invalidar Proposta", new Icon(VaadinIcon.CLOSE), event -> {
            invalidateProposal();
        });
		this.buttonInvalidate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		if(this.config.isRegisterProposal()) {
			this.addActionButton(this.buttonInvalidate);
		}
		this.buttonCloseFeedback = new Button("Encerrar Avaliações", new Icon(VaadinIcon.CALENDAR_ENVELOPE), event -> {
            closeFeedback();
        });
		if(this.config.isRegisterProposal()) {
			this.addActionButton(this.buttonCloseFeedback);
		}
		
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
		
		if(!this.config.isRegisterProposal()) {
			this.setEditCaption("Visualizar");
			this.setEditIcon(new Icon(VaadinIcon.SEARCH));
		}
	}
	
	protected void loadGrid(){
		try {
			ProposalBO bo = new ProposalBO();
	    	List<Proposal> list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	this.getGrid().setItems(ProposalDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Propostas", e.getMessage());
		}
    }
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Download da Proposta", "Selecione um registro para baixar a proposta.");
		}else{
			try{
				ProposalBO bo = new ProposalBO();
            	Proposal p = bo.findById((int)value);
				
            	if(p.getFile() != null) {
            		this.showReport(p.getFile());
            	} else {
            		this.showWarningNotification("Download da Proposta", "O acadêmico ainda não efetuou a submissão da proposta.");
            	}
			}catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Download da Proposta", e.getMessage());
			}
		}
	}
	
	private void invalidateProposal() {
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Invalidar Proposta", "Selecione um registro para invalidar a proposta.");
		}else{
			ConfirmDialog.createQuestion()
				.withIcon(new Icon(VaadinIcon.CLOSE))
		    	.withCaption("Invalidar Proposta")
		    	.withMessage("Confirma a invalidação da proposta?\n\nIsso fará com que o acadêmico tenha que enviar uma nova proposta.")
		    	.withOkButton(() -> {
		    		try {
                		ProposalBO bo = new ProposalBO();
						bo.invalidated(Session.getIdUserLog(), (int)value);
						
						refreshGrid();
						
						this.showSuccessNotification("Invalidar Proposta", "Proposta invalidada com sucesso.");
					} catch (Exception e) {
						Logger.log(Level.SEVERE, e.getMessage(), e);
						
						this.showErrorNotification("Invalidar Proposta", e.getMessage());
					}
		    	}, ButtonOption.caption("Invalidar"), ButtonOption.icon(VaadinIcon.TRASH))
		    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	.open();
		}
	}
	
	private void closeFeedback() {
		ConfirmDialog.createQuestion()
			.withIcon(new Icon(VaadinIcon.CLOSE))
	    	.withCaption("Encerrar Avaliações")
	    	.withMessage("Confirma o encerramento das avaliações de Proposta de TCC 1 para o semestre " +
	    			String.valueOf(this.comboSemester.getSemester()) + "/" + String.valueOf(this.textYear.getYear()) + "?")
	    	.withOkButton(() -> {
	    		try {
            		ProposalAppraiserBO bo = new ProposalAppraiserBO();
            		
					bo.closeFeedback(Session.getSelectedDepartment().getDepartment().getIdDepartment(), comboSemester.getSemester(), textYear.getYear());
					
					this.showSuccessNotification("Encerrar Avaliações", "Avaliações encerradas com sucesso.");
				} catch (Exception e) {
					Logger.log(Level.SEVERE, e.getMessage(), e);
					
					this.showErrorNotification("Encerrar Avaliações", e.getMessage());
				}
	    	}, ButtonOption.caption("Invalidar"), ButtonOption.icon(VaadinIcon.TRASH))
	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
	    	.open();
	}
	
	@Override
	public void addClick() {
		
	}

	@Override
	public void editClick(int id) {
		try {
			ProposalBO bo = new ProposalBO();
			Proposal p = bo.findById((int)id);
			
			EditProposalWindow window = new EditProposalWindow(p, this, this.config.isRegisterProposal());
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Proposta", e.getMessage());
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
