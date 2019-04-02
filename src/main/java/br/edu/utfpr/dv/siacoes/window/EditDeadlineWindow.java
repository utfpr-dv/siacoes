package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditDeadlineWindow extends EditWindow {

	private final Deadline deadline;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final SemesterComboBox semester;
	private final YearField year;
	private final DateField proposalDeadline;
	private final DateField projectDeadline;
	private final DateField thesisDeadline;
	private final DateField projectFinalDocumentDeadline;
	private final DateField thesisFinalDocumentDeadline;
	
	public EditDeadlineWindow(Deadline deadline, ListView parentView){
		super("Editar Datas", parentView);
		
		if(deadline == null){
			this.deadline = new Deadline();
			this.deadline.setDepartment(Session.getSelectedDepartment().getDepartment());
		}else{
			this.deadline = deadline;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.semester = new SemesterComboBox();
		this.semester.setRequired(true);
		
		this.year = new YearField();
		this.year.setRequired(true);
		
		SigetConfigBO bo = new SigetConfigBO();
		SigetConfig sigetConfig = new SigetConfig();
		try {
			sigetConfig = bo.findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e1) {
			Logger.getGlobal().log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		if(sigetConfig.isRegisterProposal()){
			this.proposalDeadline = new DateField("Data Limite da Proposta");
		}else{
			this.proposalDeadline = new DateField("Data Limite para Registro de Orientação");
		}
		this.proposalDeadline.setDateFormat("dd/MM/yyyy");
		this.proposalDeadline.setRequired(true);
		
		this.projectDeadline = new DateField("Data Limite do Projeto");
		this.projectDeadline.setDateFormat("dd/MM/yyyy");
		this.projectDeadline.setRequired(true);
		
		this.thesisDeadline = new DateField("Data Limite da Monografia");
		this.thesisDeadline.setDateFormat("dd/MM/yyyy");
		this.thesisDeadline.setRequired(true);
		
		this.projectFinalDocumentDeadline = new DateField("Data Limite da Versão Final do Projeto");
		this.projectFinalDocumentDeadline.setDateFormat("dd/MM/yyyy");
		this.projectFinalDocumentDeadline.setRequired(true);
		
		this.thesisFinalDocumentDeadline = new DateField("Data Limite da Versão Final da Monografia");
		this.thesisFinalDocumentDeadline.setDateFormat("dd/MM/yyyy");
		this.thesisFinalDocumentDeadline.setRequired(true);
		
		this.addField(this.comboCampus);
		this.addField(this.comboDepartment);
		this.addField(new HorizontalLayout(this.semester, this.year));
		this.addField(this.proposalDeadline);
		this.addField(new HorizontalLayout(this.projectDeadline, this.projectFinalDocumentDeadline));
		this.addField(new HorizontalLayout(this.thesisDeadline, this.thesisFinalDocumentDeadline));
		
		this.loadDeadline();
		this.semester.focus();
	}
	
	private void loadDeadline(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.deadline.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.deadline.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.semester.setSemester(this.deadline.getSemester());
		this.year.setYear(this.deadline.getYear());
		this.proposalDeadline.setValue(this.deadline.getProposalDeadline());
		this.projectDeadline.setValue(this.deadline.getProjectDeadline());
		this.thesisDeadline.setValue(this.deadline.getThesisDeadline());
		this.projectFinalDocumentDeadline.setValue(this.deadline.getProjectFinalDocumentDeadline());
		this.thesisFinalDocumentDeadline.setValue(this.deadline.getThesisFinalDocumentDeadline());
	}
	
	@Override
	public void save() {
		try{
			DeadlineBO bo = new DeadlineBO();
			
			this.deadline.setSemester(this.semester.getSemester());
			this.deadline.setYear(this.year.getYear());
			this.deadline.setProposalDeadline(this.proposalDeadline.getValue());
			this.deadline.setProjectDeadline(this.projectDeadline.getValue());
			this.deadline.setThesisDeadline(this.thesisDeadline.getValue());
			this.deadline.setProjectFinalDocumentDeadline(this.projectFinalDocumentDeadline.getValue());
			this.deadline.setThesisFinalDocumentDeadline(this.thesisFinalDocumentDeadline.getValue());
			
			bo.save(Session.getIdUserLog(), this.deadline);
			
			this.showSuccessNotification("Salvar Datas", "Datas salvas com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Datas", e.getMessage());
		}
	}

}
