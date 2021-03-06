package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DeadlineBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Deadline;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.YearField;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditDeadlineWindow extends EditWindow {

	private final Deadline deadline;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final SemesterComboBox semester;
	private final YearField year;
	private final DatePicker proposalDeadline;
	private final DatePicker projectDeadline;
	private final DatePicker thesisDeadline;
	private final DatePicker projectFinalDocumentDeadline;
	private final DatePicker thesisFinalDocumentDeadline;
	
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
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.semester = new SemesterComboBox();
		
		this.year = new YearField();
		
		SigetConfigBO bo = new SigetConfigBO();
		SigetConfig sigetConfig = new SigetConfig();
		try {
			sigetConfig = bo.findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e1) {
			Logger.log(Level.SEVERE, e1.getMessage(), e1);
		}
		
		if(sigetConfig.isRegisterProposal()){
			this.proposalDeadline = new DatePicker("Data Limite da Proposta");
		}else{
			this.proposalDeadline = new DatePicker("Data Limite para Registro de Orientação");
		}
		this.proposalDeadline.setRequired(true);
		
		this.projectDeadline = new DatePicker("Data Limite do Projeto");
		this.projectDeadline.setRequired(true);
		
		this.thesisDeadline = new DatePicker("Data Limite da Monografia");
		this.thesisDeadline.setRequired(true);
		
		this.projectFinalDocumentDeadline = new DatePicker("Data Limite da Versão Final do Projeto");
		this.projectFinalDocumentDeadline.setRequired(true);
		
		this.thesisFinalDocumentDeadline = new DatePicker("Data Limite da Versão Final da Monografia");
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.semester.setSemester(this.deadline.getSemester());
		this.year.setYear(this.deadline.getYear());
		this.proposalDeadline.setValue(DateUtils.convertToLocalDate(this.deadline.getProposalDeadline()));
		this.projectDeadline.setValue(DateUtils.convertToLocalDate(this.deadline.getProjectDeadline()));
		this.thesisDeadline.setValue(DateUtils.convertToLocalDate(this.deadline.getThesisDeadline()));
		this.projectFinalDocumentDeadline.setValue(DateUtils.convertToLocalDate(this.deadline.getProjectFinalDocumentDeadline()));
		this.thesisFinalDocumentDeadline.setValue(DateUtils.convertToLocalDate(this.deadline.getThesisFinalDocumentDeadline()));
	}
	
	@Override
	public void save() {
		try{
			DeadlineBO bo = new DeadlineBO();
			
			this.deadline.setSemester(this.semester.getSemester());
			this.deadline.setYear(this.year.getYear());
			this.deadline.setProposalDeadline(DateUtils.convertToDate(this.proposalDeadline.getValue()));
			this.deadline.setProjectDeadline(DateUtils.convertToDate(this.projectDeadline.getValue()));
			this.deadline.setThesisDeadline(DateUtils.convertToDate(this.thesisDeadline.getValue()));
			this.deadline.setProjectFinalDocumentDeadline(DateUtils.convertToDate(this.projectFinalDocumentDeadline.getValue()));
			this.deadline.setThesisFinalDocumentDeadline(DateUtils.convertToDate(this.thesisFinalDocumentDeadline.getValue()));
			
			bo.save(Session.getIdUserLog(), this.deadline);
			
			this.showSuccessNotification("Salvar Datas", "Datas salvas com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Datas", e.getMessage());
		}
	}

}
