package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipPosterRequestBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterRequest;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditInternshipJuryWindow;
import br.edu.utfpr.dv.siacoes.window.EditInternshipPosterRequestWindow;

public class InternshipPosterRequestView extends ListView {
	
	public static final String NAME = "internshipposterrequest";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonConfirmRequest;
	private final Button buttonRequestForm;
	
	public InternshipPosterRequestView() {
		super(SystemModule.SIGES);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setCaption("Requisições de Bancas de Estágio");
		
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
		
		this.setEditCaption("Visualizar");
		this.setEditIcon(FontAwesome.SEARCH);
		
		this.buttonConfirmRequest = new Button("Confirmar Agend.", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	confirmRequest();
            }
        });
		this.buttonConfirmRequest.setIcon(FontAwesome.CHECK);
		this.buttonConfirmRequest.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.addActionButton(this.buttonConfirmRequest);
		
		this.buttonRequestForm = new Button("Imp. Formulário", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	printRequest();
            }
        });
		this.buttonRequestForm.setIcon(FontAwesome.PRINT);
		this.addActionButton(this.buttonRequestForm);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Empresa", String.class);
		this.getGrid().addColumn("Orientador", String.class);
		this.getGrid().addColumn("Confirmada", String.class);
		this.getGrid().getColumns().get(3).setWidth(75);
		
		try {
			List<InternshipPosterRequest> list = new InternshipPosterRequestBO().listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
			
			for(InternshipPosterRequest request : list) {
				request.setInternship(new InternshipBO().findById(request.getInternship().getIdInternship()));
				
				Object itemId = this.getGrid().addRow(request.getInternship().getStudent().getName(), request.getInternship().getCompany().getName(), request.getInternship().getSupervisor().getName(), (request.isConfirmed() ? "Sim" : "Não"));
				this.addRowId(itemId, request.getIdInternshipPosterRequest());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Agendamento de Bancas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			InternshipPosterRequest request = new InternshipPosterRequestBO().findById((int)id);
			
			UI.getCurrent().addWindow(new EditInternshipPosterRequestWindow(request, false, this));
		} catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Visualizar Agendamento de Banca", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			new InternshipPosterRequestBO().delete(Session.getIdUserLog(), (int)id);
			
			this.refreshGrid();
		} catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
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
    			
    			UI.getCurrent().addWindow(new EditInternshipJuryWindow(jury, this));
    		} catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
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
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Imprimir Formulário", e.getMessage());
			}
    	}
	}

}
