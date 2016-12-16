package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionReport;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.util.ReportUtils;
import br.edu.utfpr.dv.siacoes.window.EditActivitySubmissionWindow;

public class ActivitySubmissionView extends ListView {

	public static final String NAME = "activitysubmission";
	
	private final OptionGroup optionFilterType;
	private final StudentComboBox comboStudent;
	private final Button buttonFinalReport;
	
	public ActivitySubmissionView(){
		super(SystemModule.SIGAC);
		
		this.optionFilterType = new OptionGroup();
		this.optionFilterType.addItem("Listar submissões sem parecer");
		this.optionFilterType.addItem("Listar submissões por acadêmico");
		this.optionFilterType.select(this.optionFilterType.getItemIds().iterator().next());
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.buttonFinalReport = new Button("Relatório Final");
		
		if(Session.isUserManager(this.getModule())){
			this.setAddVisible(false);
			this.setDeleteVisible(false);
			this.setEditCaption("Validar");
			
			this.addFilterField(new HorizontalLayout(this.optionFilterType, this.comboStudent));
		}else{
			this.setFiltersVisible(false);
		}
		
		this.addActionButton(this.buttonFinalReport);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Grupo", Integer.class);
		this.getGrid().addColumn("Atividade", String.class);
		this.getGrid().addColumn("Parecer", String.class);
		this.getGrid().addColumn("Pontuação", Double.class);
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(2).setWidth(100);
		this.getGrid().getColumns().get(4).setWidth(150);
		this.getGrid().getColumns().get(5).setWidth(125);
		
		new ExtensionUtils().removeAllExtensions(this.buttonFinalReport);
		this.buttonFinalReport.setEnabled(true);
		
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			ByteArrayOutputStream report = new ByteArrayOutputStream();
			
			if(Session.isUserManager(this.getModule())){
				if(this.optionFilterType.isSelected(this.optionFilterType.getItemIds().iterator().next())){
					list = bo.listWithNoFeedback(Session.getUser().getDepartment().getIdDepartment());
					this.buttonFinalReport.setEnabled(false);
				}else{
					list = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), Session.getUser().getDepartment().getIdDepartment());
					report = bo.getReport(this.comboStudent.getStudent().getIdUser(), Session.getUser().getDepartment().getIdDepartment());
				}
			}else{
				list = bo.listByStudent(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment());
				report = bo.getReport(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment());
			}
			
			if(this.buttonFinalReport.isEnabled()){
				new ExtensionUtils().extendToDownload("RelatorioFinal.pdf", report.toByteArray(), this.buttonFinalReport);
			}
			
			for(ActivitySubmission submission : list){
				Object itemId = this.getGrid().addRow(submission.getSemester(), submission.getYear(), submission.getActivity().getGroup().getSequence(), submission.getActivity().getDescription(), submission.getFeedback().toString(), submission.getScore());
				this.addRowId(itemId, submission.getIdActivitySubmission());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Submissões", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		UI.getCurrent().addWindow(new EditActivitySubmissionWindow(null, this));
	}

	@Override
	public void editClick(Object id) {
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			ActivitySubmission submission = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditActivitySubmissionWindow(submission, this));
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Submissão", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void deleteClick(Object id) {
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			
			bo.delete((int)id);
			
			this.refreshGrid();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Excluir Submissão", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void filterClick() throws Exception {
		if(!this.optionFilterType.isSelected(this.optionFilterType.getItemIds().iterator().next())){
			if((this.comboStudent.getStudent() == null) || (this.comboStudent.getStudent().getIdUser() == 0)){
				throw new Exception("Selecione um acadêmico");	
			}
		}
	}

}
