package br.edu.utfpr.dv.siacoes.ui.windows;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class EventLogWindow extends BasicWindow {
	
	private final TextField textUser;
	private final TextField textEvent;
	private final TextField textClassName;
	private final DateTimePicker textDate;
	private final TextField textId;
	private final TextArea textData;
	
	public EventLogWindow(UpdateEvent event) {
		super("Visualizar Registro");
		
		this.textUser = new TextField("Usuário");
		this.textUser.setWidth("600px");
		this.textUser.setEnabled(false);
		
		this.textEvent = new TextField("Operação");
		this.textEvent.setWidth("100px");
		this.textEvent.setEnabled(false);
		
		this.textClassName = new TextField("Tabela");
		this.textClassName.setWidth("200px");
		this.textClassName.setEnabled(false);
		
		this.textDate = new DateTimePicker("Data/Hora");
		//this.textDate.setDateFormat("dd/MM/yyyy HH:mm:ss");
		//this.textDate.setResolution(Resolution.SECOND);
		this.textDate.setEnabled(false);
		this.textDate.setWidth("180px");
		
		this.textId = new TextField("ID");
		this.textId.setWidth("70px");
		this.textId.setEnabled(false);
		
		this.textData = new TextArea("Dados");
		this.textData.setWidth("600px");
		this.textData.setHeight("300px");
		
		HorizontalLayout hl = new HorizontalLayout(this.textEvent, this.textClassName, this.textDate, this.textId);
		hl.setSpacing(true);
		hl.setMargin(false);
		hl.setPadding(false);
		
		VerticalLayout vl = new VerticalLayout(this.textUser, hl, this.textData);
		vl.setSpacing(false);
		vl.setMargin(false);
		vl.setPadding(false);
		
		this.textUser.setValue(event.getUser().getName());
		this.textEvent.setValue(event.getEvent().toString());
		this.textClassName.setValue(event.getClassName());
		this.textDate.setValue(DateUtils.convertToLocalDateTime(event.getDate()));
		this.textId.setValue(String.valueOf(event.getIdObject()));
		try {
			this.textData.setValue(UpdateEvent.unzipDataToJson(event.getData()));
		} catch (Exception e) {
			this.textData.setValue(e.getMessage());
		}
		
		this.textData.setReadOnly(true);
		
		this.add(vl);
	}

}
