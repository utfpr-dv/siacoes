package br.edu.utfpr.dv.siacoes.components;

import java.util.Date;

import com.vaadin.ui.components.calendar.event.BasicEvent;

import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;

public class CalendarEvent extends BasicEvent {

	private Jury jury;
	private JuryRequest juryRequest;
	private InternshipJury internshipJury;
	
	public CalendarEvent(String caption, String description, Date startDate, Date endDate) {
		super(caption, description, startDate, endDate);
		this.setJury(new Jury());
		this.setJuryRequest(new JuryRequest());
		this.setInternshipJury(new InternshipJury());
	}
	public Jury getJury() {
		return jury;
	}
	public void setJury(Jury jury) {
		this.jury = jury;
	}
	public JuryRequest getJuryRequest() {
		return juryRequest;
	}
	public void setJuryRequest(JuryRequest juryRequest) {
		this.juryRequest = juryRequest;
	}
	public InternshipJury getInternshipJury() {
		return internshipJury;
	}
	public void setInternshipJury(InternshipJury internshipJury) {
		this.internshipJury = internshipJury;
	}
	
}
