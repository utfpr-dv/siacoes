package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryRequestDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryRequestWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditJuryWindow;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Requisições de Bancas de TCC")
@Route(value = "juryrequest", layout = MainLayout.class)
public class JuryRequestView extends ListView<JuryRequestDataSource> {
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final StageComboBox comboStage;
	private final Button buttonConfirmRequest;
	private final Button buttonRequestForm;
	
	public JuryRequestView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(new LocalDateTimeRenderer<>(JuryRequestDataSource::getDate, "dd/MM/yyyy HH:mm"), "Date").setHeader("Data e Hora").setFlexGrow(0).setWidth("150px");
		this.getGrid().addColumn(JuryRequestDataSource::getLocal).setHeader("Local");
		this.getGrid().addColumn(JuryRequestDataSource::getStage, "Stage").setHeader("TCC").setFlexGrow(0).setWidth("50px");
		this.getGrid().addColumn(JuryRequestDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(JuryRequestDataSource::getChair, "Chair").setHeader("Presidente");
		this.getGrid().addColumn(JuryRequestDataSource::getConfirmed, "Confirmed").setHeader("Confirmada").setFlexGrow(0).setWidth("100px");
		
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
		
		this.comboStage = new StageComboBox(true);
		this.comboStage.selectBoth();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.comboStage));
		
		this.setAddVisible(false);
		
		this.setEditCaption("Visualizar");
		this.setEditIcon(new Icon(VaadinIcon.SEARCH));
		
		this.buttonConfirmRequest = new Button("Confirmar Agend.", new Icon(VaadinIcon.CHECK), event -> {
            confirmRequest();
        });
		this.buttonConfirmRequest.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.addActionButton(this.buttonConfirmRequest);
		
		this.buttonRequestForm = new Button("Imp. Formulário", new Icon(VaadinIcon.PRINT), event -> {
            printRequest();
        });
		this.addActionButton(this.buttonRequestForm);
	}

	@Override
	protected void loadGrid() {
		try {
			List<JuryRequest> list = new JuryRequestBO().listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
			
			for(JuryRequest jury : list) {
				jury.setAppraisers(new JuryAppraiserRequestBO().listAppraisers(jury.getIdJuryRequest()));
			}
			
			this.getGrid().setItems(JuryRequestDataSource.load(list, this.comboStage.getStage()));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Agendamento de Bancas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(int id) {
		try {
			JuryRequest jury = new JuryRequestBO().findById((int)id);
			
			EditJuryRequestWindow window = new EditJuryRequestWindow(jury, false, this);
			window.open();
		} catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Visualizar Agendamento de Banca", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		try {
			new JuryRequestBO().delete(Session.getIdUserLog(), (int) id);
			
			this.refreshGrid();
		} catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Agendamento de Banca", e.getMessage());
		}
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private void confirmRequest() {
		Object id = getIdSelected();
    	
    	if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para confirmar o agendamento da banca.");
    	} else {
			try {
				JuryRequest jury = new JuryRequestBO().findById((int)id);
				
				if(jury.getStage() == 1) {
					Project project = new ProjectBO().findByProposal(jury.getProposal().getIdProposal());
					
					if((project == null) || (project.getIdProject() == 0)) {
						this.showWarningNotification("Confirmar Agendamento de Banca", "O agendamento só pode ser confirmado após o acadêmico submeter o projeto.");
					} else {
						Jury j = new JuryBO().findByProject(project.getIdProject());
						
						EditJuryWindow window = new EditJuryWindow(j, this);
						window.open();
					}
				} else {
					Thesis thesis = new ThesisBO().findByProposal(jury.getProposal().getIdProposal());
					
					if((thesis == null) || (thesis.getIdThesis() == 0)) {
						this.showWarningNotification("Confirmar Agendamento de Banca", "O agendamento só pode ser confirmado após o acadêmico submeter o monografia.");
					} else {
						Jury j = new JuryBO().findByThesis(thesis.getIdThesis());
						
						EditJuryWindow window = new EditJuryWindow(j, this);
						window.open();
					}
				}
			} catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Confirmar Agendamento de Banca", e.getMessage());
			}
    	}
	}
	
	private void printRequest() {
		Object id = getIdSelected();
    	
    	if(id == null) {
    		this.showWarningNotification("Selecionar Registro", "Selecione o registro para imprimir o formulário de agendamento da banca.");
    	} else {
    		try {
    			this.showReport(new JuryRequestBO().getJuryRequestForm((int)id));
    		} catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Imprimir Formulário", e.getMessage());
			}
    	}
	}

}
