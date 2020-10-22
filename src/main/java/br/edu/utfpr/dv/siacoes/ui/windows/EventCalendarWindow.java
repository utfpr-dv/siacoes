package br.edu.utfpr.dv.siacoes.ui.windows;

import org.vaadin.stefan.fullcalendar.Entry;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class EventCalendarWindow extends BasicWindow {

	public EventCalendarWindow(Entry entry) {
		super(entry.getTitle());
		
		String description[] = entry.getDescription().split("\n");
		String student = description[0].replace("Acadêmico(a):", "").trim();
		String local = description[1].replace("Local:", "").trim();
		String appraisers = description[2].replace("Membros da banca:", "").replace(";", "\n").trim();
		
		/*TextField textTitle = new TextField("Tipo");
		textTitle.setValue(entry.getTitle());
		textTitle.setWidth("800px");
		textTitle.setReadOnly(true);*/
		
		DateTimePicker textDate = new DateTimePicker("Data e Hora");
		textDate.setValue(entry.getStart());
		textDate.setReadOnly(true);
		
		TextField textLocal = new TextField("Local");
		textLocal.setValue(local);
		textLocal.setWidth("800px");
		textLocal.setReadOnly(true);
		
		TextField textStudent = new TextField("Acadêmico(a)");
		textStudent.setValue(student);
		textStudent.setWidth("800px");
		textStudent.setReadOnly(true);
		
		TextArea textAppraisers = new TextArea("Banca");
		textAppraisers.setValue(appraisers);
		textAppraisers.setWidth("800px");
		textAppraisers.setHeight("150px");
		textAppraisers.setReadOnly(true);
		
		VerticalLayout layout = new VerticalLayout(/*textTitle,*/ textDate, textLocal, textStudent, textAppraisers);
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.add(layout);
	}

}
