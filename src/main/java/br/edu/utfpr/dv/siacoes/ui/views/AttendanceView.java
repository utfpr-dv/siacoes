package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.bo.SupervisorChangeBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.ui.grid.AttendanceDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditAttendanceWindow;
import br.edu.utfpr.dv.siacoes.ui.windows.SignatureWindow;

@PageTitle("Registro de Reuniões")
@Route(value = "attendance", layout = MainLayout.class)
public class AttendanceView extends ListView<AttendanceDataSource> {
	
	private final StudentComboBox comboStudent;
	private final Select<Proposal> comboProposal;
	private final Select<User> comboSupervisor;
	private final StageComboBox comboStage;
	private final Button buttonSign;
	private final Button buttonPrint;
	
	private SigetConfig config;
	
	public AttendanceView(){
		super(SystemModule.SIGET);
		
		this.getGrid().addColumn(new LocalDateRenderer<>(AttendanceDataSource::getDate, "dd/MM/yyyy")).setHeader("Data").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(new LocalDateTimeRenderer<>(AttendanceDataSource::getStart, "HH:mm")).setHeader("Horário Inicial").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(new LocalDateTimeRenderer<>(AttendanceDataSource::getEnd, "HH:MM")).setHeader("Horário Final").setFlexGrow(0).setWidth("125px");
		this.getGrid().addColumn(AttendanceDataSource::getComments).setHeader("Observaçoes");
		
		this.comboSupervisor = new Select<User>();
		this.comboSupervisor.setLabel("Orientador");
		this.comboSupervisor.setWidth("400px");
		
		this.comboProposal = new Select<Proposal>();
		this.comboProposal.setLabel("Proposta");
		this.comboProposal.setWidth("400px");
		this.comboProposal.addValueChangeListener(event -> {
			if(comboProposal.getValue() != null){
				selectStage();
			}
		});
		
		this.comboStage = new StageComboBox();
		this.comboStage.setShowBoth(true);
		
		if(Session.isUserProfessor()) {
			this.comboStudent = new StudentComboBox("Acadêmico", Session.getUser().getIdUser());
			this.comboStudent.addValueChangeListener(event -> {
				if(comboStudent.getStudent() != null){
					loadProposals();
				}
			});
			
			this.comboSupervisor.setItems(Session.getUser());
			this.comboSupervisor.setValue(Session.getUser());
			this.comboSupervisor.setVisible(false);
		} else {
			this.comboStudent = new StudentComboBox("Acadêmico");
			this.comboStudent.setItems(Session.getUser());
			this.comboStudent.setValue(Session.getUser());
			this.loadProposals();
			this.loadSupervisors();
			
			this.comboStudent.setVisible(false);
		}
		
		this.addFilterField(this.comboStudent);
		this.addFilterField(this.comboProposal);
		this.addFilterField(this.comboSupervisor);
		this.addFilterField(this.comboStage);
		
		this.comboProposal.addValueChangeListener(event -> {
			if(comboSupervisor.isVisible() && (comboProposal.getValue() != null)) {
				loadSupervisors();
			}
		});
		
		this.buttonSign = new Button("Assinar", new Icon(VaadinIcon.PENCIL), event -> {
        	clickSign();
        });
		this.buttonSign.setWidth("150px");
		this.buttonSign.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		
		this.buttonPrint = new Button("Imprimir", new Icon(VaadinIcon.PRINT), event -> {
            printReport();
        });
		this.buttonPrint.setWidth("150px");
		
		try {
			this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch(Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			this.config = new SigetConfig();
		}
		
		if(this.config.isUseDigitalSignature()) {
			this.addActionButton(this.buttonSign);
		}
		this.addActionButton(this.buttonPrint);
	}

	@Override
	protected void loadGrid() {
		try {			
			AttendanceBO bo = new AttendanceBO();
	    	List<Attendance> list = new ArrayList<Attendance>();
	    	
	    	if((this.comboStudent.getStudent() != null) && (this.comboProposal.getValue() != null) && (this.comboSupervisor.getValue() != null)) {
	    		list = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), ((User)this.comboSupervisor.getValue()).getIdUser(), ((Proposal)this.comboProposal.getValue()).getIdProposal(), (this.comboStage.isBothSelected() ? 0 : this.comboStage.getStage()));
	    	}
	    	
	    	this.getGrid().setItems(AttendanceDataSource.load(list));
	    	
	    	this.buttonSign.setEnabled(true);
	    	
	    	if((this.comboStudent.getStudent() == null) || (this.comboProposal.getValue() == null) || (this.comboSupervisor.getValue() == null)) {
	    		this.buttonSign.setEnabled(false);
	    	} else if(this.comboStage.isBothSelected() || new AttendanceBO().hasSignature(((Proposal)this.comboProposal.getValue()).getIdProposal(), this.comboStage.getStage(), ((User)this.comboSupervisor.getValue()).getIdUser())) {
	    		this.buttonSign.setEnabled(false);
	    	}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
		}
	}
	
	private void loadProposals(){
		try {
			ProposalBO bo = new ProposalBO();
			List<Proposal> list = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			this.comboProposal.setItems(list);
			if(list.size() > 0){
				this.comboProposal.setValue(list.get(0));	
			}
			
			selectStage();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
		}
	}
	
	private void loadSupervisors(){
		try {
			this.comboSupervisor.clear();
			
			if(this.comboProposal.getValue() != null){
				List<User> list = new ProposalBO().listSupervisors(((Proposal)this.comboProposal.getValue()).getIdProposal(), true);
				
				this.comboSupervisor.setItems(list);
				if(list.size() > 0){
					this.comboSupervisor.setValue(list.get(0));	
				}
				
				User supervisor = new SupervisorChangeBO().findCurrentSupervisor(((Proposal)this.comboProposal.getValue()).getIdProposal());
				for(User u : list) {
					if(u.getIdUser() == supervisor.getIdUser()) {
						this.comboSupervisor.setValue(u);
						break;
					}
				}
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
		}
	}
	
	private void selectStage() {
		try {
			if(this.comboProposal.getValue() != null) {
				int stage = new ProposalBO().getProposalStage(((Proposal)this.comboProposal.getValue()).getIdProposal());
				
				this.comboStage.setStage(stage);
			} else {
				this.comboStage.setStage(1);
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acompanhamentos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		if(this.comboStudent.getStudent() == null) {
			this.showErrorNotification("Incluir Reunião", "Selecione o acadêmico.");
		} else if(this.comboProposal.getValue() == null) {
			this.showErrorNotification("Incluir Reunião", "Selecione o projeto.");
		} else if(this.comboSupervisor.getValue() == null) {
			this.showErrorNotification("Incluir Reunião", "Selecione o orientador.");
		} else {
			Attendance attendance = new Attendance();
	    	
	    	attendance.setProposal((Proposal)this.comboProposal.getValue());
	    	attendance.setStudent(((Proposal)this.comboProposal.getValue()).getStudent());
	    	attendance.setSupervisor((User)this.comboSupervisor.getValue());
	    	attendance.setStage(this.comboStage.getStage());
	    	
	    	EditAttendanceWindow window = new EditAttendanceWindow(attendance, this);
	    	window.open();
		}
	}

	@Override
	public void editClick(int id) {
		try {
			AttendanceBO bo = new AttendanceBO();
			Attendance attendance = bo.findById((int)id);
			
			EditAttendanceWindow window = new EditAttendanceWindow(attendance, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Reunião", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		try {
			AttendanceBO bo = new AttendanceBO();
			
			bo.delete(Session.getIdUserLog(), (int)id);
			
			this.refreshGrid();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Excluir Reunião", e.getMessage());
		}
	}

	@Override
	public void filterClick() throws Exception {
		if(this.comboStudent.getStudent() == null) {
			throw new Exception("Selecione o acadêmico.");
		}
		
		if(this.comboProposal.getValue() == null) {
			throw new Exception("Selecione a proposta.");
		}
	}
	
	private void clickSign() {
		if(this.comboStage.isBothSelected()) {
			this.showWarningNotification("Assinar Documento", "Selecione TCC 1 ou 2 para assinar o documento.");
		} else if((this.comboStudent.getStudent() != null) && (this.comboProposal.getValue() != null)) {
			try {
				if(new AttendanceBO().hasSignature(((Proposal)this.comboProposal.getValue()).getIdProposal(), this.comboStage.getStage(), ((User)this.comboSupervisor.getValue()).getIdUser(), Session.getUser().getIdUser())) {
					this.showWarningNotification("Assinar Documento", "Este documento já foi assinado.");
				} else {
					ConfirmDialog.createQuestion()
		    			.withIcon(new Icon(VaadinIcon.QUESTION))
		    	    	.withCaption("Confirma a Assinatura?")
		    	    	.withMessage("Confirma a assinatura do documento?\n\nApós assinar o documento, não será possível efetuar alterações, inclusões e exclusões de reuniões para o TCC " + String.valueOf(this.comboStage.getStage()) + ".")
		    	    	.withOkButton(() -> {
		    	    		sign();
		    	    	}, ButtonOption.caption("Assinar"), ButtonOption.icon(VaadinIcon.CHECK))
		    	    	.withCancelButton(ButtonOption.focus(), ButtonOption.caption("Cancelar"), ButtonOption.icon(VaadinIcon.CLOSE))
		    	    	.open();
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Documento", e.getMessage());
			}
		} else {
        	if(Session.isUserStudent()) {
        		this.showWarningNotification("Assinar Documento", "É necessário selecionar o orientador e a proposta para assinar o documento.");
        	} else {
        		this.showWarningNotification("Assinar Documento", "É necessário selecionar o acadêmico e a proposta para assinar o documento.");
        	}
		}
	}
	
	private void sign() {
		try {
			br.edu.utfpr.dv.siacoes.report.dataset.v1.Attendance dataset = new AttendanceBO().buildDataset(this.comboStudent.getStudent().getIdUser(), ((Proposal)this.comboProposal.getValue()).getIdProposal(), ((User)this.comboSupervisor.getValue()).getIdUser(), this.comboStage.getStage());
			List<User> users = new ArrayList<User>();
			
			users.add(this.comboStudent.getStudent());
			users.add((User)this.comboSupervisor.getValue());
			
			SignatureWindow window = new SignatureWindow(DocumentType.ATTENDANCE, dataset.getIdGroup(), dataset, users, null, null);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Assinar Documento", e.getMessage());
		}
	}
	
	private void printReport() {
		if((this.comboStudent.getStudent() != null) && (this.comboProposal.getValue() != null)) {
			try {
				this.showReport(new AttendanceBO().getReport(this.comboStudent.getStudent().getIdUser(), ((Proposal)this.comboProposal.getValue()).getIdProposal(), ((User)this.comboSupervisor.getValue()).getIdUser(), this.comboStage.getStage()));
			} catch (Exception e) {
				this.showErrorNotification("Imprimir Acompanhamentos", e.getMessage());
			}
		} else {
        	if(Session.isUserStudent()) {
        		this.showWarningNotification("Imprimir Acompanhamentos", "É necessário selecionar o orientador e a proposta para imprimir os acompanhamentos.");
        	} else {
        		this.showWarningNotification("Imprimir Acompanhamentos", "É necessário selecionar o acadêmico e a proposta para imprimir os acompanhamentos.");
        	}
		}
	}

}
