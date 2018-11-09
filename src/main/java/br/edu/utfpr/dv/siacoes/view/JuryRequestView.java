package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.ProjectBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.ThesisBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditJuryRequestWindow;
import br.edu.utfpr.dv.siacoes.window.EditJuryWindow;

public class JuryRequestView extends ListView {
	
	public static final String NAME = "juryrequest";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final StageComboBox comboStage;
	private final Button buttonConfirmRequest;
	
	public JuryRequestView() {
		super(SystemModule.SIGET);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setCaption("Requisições de Bancas de TCC");
		
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
		
		this.comboStage = new StageComboBox(true);
		this.comboStage.selectBoth();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear, this.comboStage));
		
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
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Data e Hora", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy HH:mm")));
		this.getGrid().addColumn("Local", String.class);
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Presidente", String.class);
		this.getGrid().getColumns().get(0).setWidth(150);
		this.getGrid().getColumns().get(2).setWidth(50);
		
		try {
			List<JuryRequest> list = new JuryRequestBO().listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
			
			for(JuryRequest jury : list) {
				if(this.comboStage.isBothSelected() || (this.comboStage.getStage() == jury.getStage())) {
					String chair = "";
					
					jury.setAppraisers(new JuryAppraiserRequestBO().listAppraisers(jury.getIdJuryRequest()));
					
					for(JuryAppraiserRequest appraiser : jury.getAppraisers()) {
						if(appraiser.isChair()) {
							chair = appraiser.getAppraiser().getName();
						}
					}
					
					Object itemId = this.getGrid().addRow(jury.getDate(), jury.getLocal(), jury.getStage(), jury.getStudent(), chair);
					this.addRowId(itemId, jury.getIdJuryRequest());
				}
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
			JuryRequest jury = new JuryRequestBO().findById((int)id);
			
			UI.getCurrent().addWindow(new EditJuryRequestWindow(jury, false, this));
		} catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Visualizar Agendamento de Banca", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		try {
			new JuryRequestBO().delete((int) id);
			
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
				JuryRequest jury = new JuryRequestBO().findById((int)id);
				
				if(jury.getStage() == 1) {
					Project project = new ProjectBO().findByProposal(jury.getProposal().getIdProposal());
					
					if((project == null) || (project.getIdProject() == 0)) {
						this.showWarningNotification("Confirmar Agendamento de Banca", "O agendamento só pode ser confirmado após o acadêmico submeter o projeto.");
					} else {
						Jury j = new JuryBO().findByProject(project.getIdProject());
						
						UI.getCurrent().addWindow(new EditJuryWindow(j, this));
					}
				} else {
					Thesis thesis = new ThesisBO().findByProposal(jury.getProposal().getIdProposal());
					
					if((thesis == null) || (thesis.getIdThesis() == 0)) {
						this.showWarningNotification("Confirmar Agendamento de Banca", "O agendamento só pode ser confirmado após o acadêmico submeter o monografia.");
					} else {
						Jury j = new JuryBO().findByThesis(thesis.getIdThesis());
						
						UI.getCurrent().addWindow(new EditJuryWindow(j, this));
					}
				}
			} catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Confirmar Agendamento de Banca", e.getMessage());
			}
    	}
	}

}
