package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.AttendanceBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Attendance;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EditAttendanceWindow extends EditWindow {
	
	private final Attendance attendance;
	
	private final TextField textStudent;
	private final TextField textSupervisor;
	private final TextField textProposal;
	private final DatePicker textDate;
	private final TimePicker textStartTime;
	private final TimePicker textEndTime;
	private final TextArea textComments;
	private final TextArea textNextMeeting;
	private final StageComboBox comboStage;
	
	public EditAttendanceWindow(Attendance attendance, ListView parentView){
		super("Registrar Acompanhamento", parentView);
		
		if(attendance == null){
			this.attendance = new Attendance();
		}else{
			this.attendance = attendance;
		}
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setEnabled(false);
		this.textStudent.setWidth("400px");
		this.textStudent.setRequired(true);
		
		this.textSupervisor = new TextField("Orientador");
		this.textSupervisor.setEnabled(false);
		this.textSupervisor.setWidth("400px");
		this.textSupervisor.setRequired(true);
		
		this.textProposal = new TextField("Proposta");
		this.textProposal.setEnabled(false);
		this.textProposal.setWidth("810px");
		this.textProposal.setRequired(true);
		
		this.comboStage = new StageComboBox();
		
		this.textDate = new DatePicker("Data");
		this.textDate.setRequired(true);
		
		this.textStartTime = new TimePicker("Horário Inicial");
		this.textStartTime.setRequired(true);
		
		this.textEndTime = new TimePicker("Horário Final");
		this.textEndTime.setRequired(true);
		
		this.textComments = new TextArea("Observações/Orientações");
		this.textComments.setWidth("810px");
		this.textComments.setRequired(true);
		
		this.textNextMeeting = new TextArea("Atividades previstas para a próxima reunião");
		this.textNextMeeting.setWidth("810px");
		
		HorizontalLayout h1 = new HorizontalLayout(this.textStudent, this.textSupervisor);
		h1.setSpacing(true);
		
		HorizontalLayout layoutDate = new HorizontalLayout(this.comboStage, this.textDate, this.textStartTime, this.textEndTime);
		layoutDate.setSpacing(true);
		
		this.addField(h1);
		this.addField(this.textProposal);
		this.addField(layoutDate);
		this.addField(this.textComments);
		this.addField(this.textNextMeeting);
		
		this.loadAttendance();
		this.comboStage.focus();
	}
	
	private void loadAttendance(){
		this.textStudent.setValue(this.attendance.getStudent().getName());
		this.textSupervisor.setValue(this.attendance.getSupervisor().getName());
		this.textProposal.setValue(this.attendance.getProposal().getTitle());
		this.comboStage.setStage(this.attendance.getStage());
		this.textDate.setValue(DateUtils.convertToLocalDate(this.attendance.getDate()));
		this.textStartTime.setValue(DateUtils.convertToLocalDateTime(this.attendance.getStartTime()).toLocalTime());
		this.textEndTime.setValue(DateUtils.convertToLocalDateTime(this.attendance.getEndTime()).toLocalTime());
		this.textComments.setValue(this.attendance.getComments());
		this.textNextMeeting.setValue(this.attendance.getNextMeeting());
	}

	@Override
	public void save() {
		try{
			AttendanceBO bo = new AttendanceBO();
			
			this.attendance.setStage((int)this.comboStage.getStage());
			this.attendance.setDate(DateUtils.convertToDate(this.textDate.getValue()));
			this.attendance.setStartTime(DateUtils.convertToDate(this.textStartTime.getValue()));
			this.attendance.setEndTime(DateUtils.convertToDate(this.textEndTime.getValue()));
			this.attendance.setComments(this.textComments.getValue());
			this.attendance.setNextMeeting(this.textNextMeeting.getValue());
			
			bo.save(Session.getIdUserLog(), this.attendance);
			
			this.showSuccessNotification("Salvar Acompanhamento", "Acompanhamento salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Acompanhamento", e.getMessage());
		}
	}

}
