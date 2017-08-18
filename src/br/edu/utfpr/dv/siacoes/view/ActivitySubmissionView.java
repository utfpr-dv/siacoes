package br.edu.utfpr.dv.siacoes.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivitySubmissionBO;
import br.edu.utfpr.dv.siacoes.components.StudentComboBox;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmission;
import br.edu.utfpr.dv.siacoes.model.ActivitySubmissionFooterReport;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.util.ExtensionUtils;
import br.edu.utfpr.dv.siacoes.window.EditActivitySubmissionWindow;

public class ActivitySubmissionView extends ListView {

	public static final String NAME = "activitysubmission";
	
	private final OptionGroup optionFilterType;
	private final StudentComboBox comboStudent;
	private final Button buttonFinalReport;
	
	private final Panel panelScore;
	
	public ActivitySubmissionView(){
		super(SystemModule.SIGAC);
		
		this.optionFilterType = new OptionGroup();
		this.optionFilterType.addItem("Listar submissões sem parecer");
		this.optionFilterType.addItem("Listar submissões por acadêmico");
		this.optionFilterType.select(this.optionFilterType.getItemIds().iterator().next());
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.buttonFinalReport = new Button("Relatório Final");
		
		this.panelScore = new Panel("Pontos Validados");
		
		if(Session.isUserManager(this.getModule()) || Session.isUserDepartmentManager()){
			this.setAddVisible(false);
			this.setDeleteVisible(false);
			
			if(Session.isUserManager(this.getModule())){
				this.setEditCaption("Validar");
			}else{
				this.setEditCaption("Visualizar");
			}
			
			this.addFilterField(new HorizontalLayout(this.optionFilterType, this.comboStudent));
		}else{
			this.setFiltersVisible(false);
		}
		
		this.addActionButton(this.buttonFinalReport);
		this.addActionPanel(this.panelScore);
	}
	
	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Aluno", String.class);
		this.getGrid().addColumn("Sem.", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Grupo", Integer.class);
		this.getGrid().addColumn("Descrição da Atividade", String.class);
		this.getGrid().addColumn("Parecer", String.class);
		this.getGrid().addColumn("Pontuação", Double.class);
		
		this.getGrid().getColumns().get(1).setWidth(75);
		this.getGrid().getColumns().get(2).setWidth(80);
		this.getGrid().getColumns().get(3).setWidth(80);
		this.getGrid().getColumns().get(5).setWidth(150);
		this.getGrid().getColumns().get(6).setWidth(125);
		
		new ExtensionUtils().removeAllExtensions(this.buttonFinalReport);
		this.buttonFinalReport.setEnabled(true);
		this.panelScore.setVisible(true);
		
		try{
			ActivitySubmissionBO bo = new ActivitySubmissionBO();
			List<ActivitySubmission> list = new ArrayList<ActivitySubmission>();
			ByteArrayOutputStream report = new ByteArrayOutputStream();
			List<ActivitySubmissionFooterReport> scores = new ArrayList<ActivitySubmissionFooterReport>();
			
			if(Session.isUserManager(this.getModule()) || Session.isUserDepartmentManager()){
				if(this.optionFilterType.isSelected(this.optionFilterType.getItemIds().iterator().next())){
					list = bo.listWithNoFeedback(Session.getUser().getDepartment().getIdDepartment());
					this.buttonFinalReport.setEnabled(false);
					this.panelScore.setVisible(false);
				}else{
					list = bo.listByStudent(this.comboStudent.getStudent().getIdUser(), Session.getUser().getDepartment().getIdDepartment());
					report = bo.getReport(list, this.comboStudent.getStudent(), Session.getUser().getDepartment().getIdDepartment());
					scores = bo.getFooterReport(list);
				}
			}else{
				list = bo.listByStudent(Session.getUser().getIdUser(), Session.getUser().getDepartment().getIdDepartment());
				report = bo.getReport(list, Session.getUser(), Session.getUser().getDepartment().getIdDepartment());
				scores = bo.getFooterReport(list);
			}
			
			this.buildPanelScores(scores);
			
			if(this.buttonFinalReport.isEnabled()){
				new ExtensionUtils().extendToDownload("RelatorioFinal.pdf", report.toByteArray(), this.buttonFinalReport);
			}
			
			for(ActivitySubmission submission : list){
				Object itemId = this.getGrid().addRow(submission.getStudent().getName(), submission.getSemester(), submission.getYear(), submission.getActivity().getGroup().getSequence(), submission.getDescription(), submission.getFeedback().toString(), submission.getScore());
				this.addRowId(itemId, submission.getIdActivitySubmission());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Submissões", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void buildPanelScores(List<ActivitySubmissionFooterReport> scores){
		VerticalLayout v = new VerticalLayout();
		v.setSpacing(true);
		v.setMargin(true);
		
		for(ActivitySubmissionFooterReport group : scores){
			Label labelScore = new Label(String.valueOf(group.getTotal()) + " ponto" + (group.getTotal() >= 2 ? "s" : ""));
			
			VerticalLayout v2 = new VerticalLayout(labelScore);
			v2.setSpacing(true);
			v2.setMargin(true);
			
			Panel panel = new Panel("Grupo " + String.valueOf(group.getSequence()));
			panel.setContent(v2);
			
			v.addComponent(panel);
		}
		
		this.panelScore.setContent(v);
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
