package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.grid.InternshipPosterRequestDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.EditInternshipPosterRequestWindow;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@PageTitle("Requisições de Bancas de Estágio")
@Route(value = "internshipposterrequest", layout = MainLayout.class)
public class InternshipPosterRequestView extends ListView<InternshipPosterRequestDataSource> {
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonConfirmRequest;
	private final Button buttonRequestForm;
	
	public InternshipPosterRequestView() {
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(InternshipPosterRequestDataSource::getStudent, "Student").setHeader("Acadêmico");
		this.getGrid().addColumn(InternshipPosterRequestDataSource::getCompany, "Company").setHeader("Empresa");
		this.getGrid().addColumn(InternshipPosterRequestDataSource::getSupervisor, "Supervisor").setHeader("Orientador");
		this.getGrid().addColumn(InternshipPosterRequestDataSource::getConfirmed, "Confirmed").setHeader("Confirmada").setFlexGrow(0).setWidth("100px");
		
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
			List<InternshipPosterRequest> list = new InternshipPosterRequestBO().listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
			
			for(InternshipPosterRequest request : list) {
				request.setInternship(new InternshipBO().findById(request.getInternship().getIdInternship()));
			}
			
			this.getGrid().setItems(InternshipPosterRequestDataSource.load(list));
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
			InternshipPosterRequest request = new InternshipPosterRequestBO().findById((int)id);
			
			EditInternshipPosterRequestWindow window = new EditInternshipPosterRequestWindow(request, false, this);
			window.open();
		} catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Visualizar Agendamento de Banca", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		try {
			new InternshipPosterRequestBO().delete(Session.getIdUserLog(), (int)id);
			
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
    			InternshipJury jury = new InternshipJuryBO().findByInternship(new InternshipPosterRequestBO().findById((int)id).getInternship().getIdInternship());
    			
    			EditInternshipJuryWindow window = new EditInternshipJuryWindow(jury, this);
    			window.open();
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
    			this.showReport(new InternshipPosterRequestBO().getPosterRequestForm((int)id));
    		} catch(Exception e){
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Imprimir Formulário", e.getMessage());
			}
    	}
	}

}
