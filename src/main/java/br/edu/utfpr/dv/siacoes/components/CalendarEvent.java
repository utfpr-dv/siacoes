package br.edu.utfpr.dv.siacoes.components;

import java.util.Date;

import com.vaadin.ui.components.calendar.event.BasicEvent;

import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.Jury;

public class CalendarEvent extends BasicEvent {

	private Jury jury;
	private InternshipJury internshipJury;
	
	public CalendarEvent(String caption, String description, Date startDate, Date endDate) {
		super(caption, description, startDate, endDate);
		this.setJury(new Jury());
		this.setInternshipJury(new InternshipJury());
	}
	public Jury getJury() {
		return jury;
	}
	public void setJury(Jury jury) {
		this.jury = jury;
	}
	public InternshipJury getInternshipJury() {
		return internshipJury;
	}
	public void setInternshipJury(InternshipJury internshipJury) {
		this.internshipJury = internshipJury;
	}
	
}
