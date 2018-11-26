package br.edu.utfpr.dv.siacoes.window;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import br.edu.utfpr.dv.siacoes.components.CalendarEvent;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class CalendarEventWindow extends Window {
	
	private final TextField textTitle;
	private final TextField textStudent;
	private final TextField textLocal;
	private final TextField textCompany;
	private final TextField textDate;
	private final TextArea textAppraisers;

	public CalendarEventWindow(CalendarEvent event){
		this.textTitle = new TextField("Título");
		this.textTitle.setWidth("600px");
		this.textTitle.setEnabled(false);
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("600px");
		this.textStudent.setEnabled(false);
		
		this.textCompany = new TextField("Empresa");
		this.textCompany.setWidth("600px");
		this.textCompany.setEnabled(false);
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("400px");
		this.textLocal.setEnabled(false);
		
		this.textDate = new TextField("Data/Hora");
		this.textDate.setWidth("190px");
		this.textDate.setEnabled(false);
		
		this.textAppraisers = new TextArea("Membros da Banca");
		this.textAppraisers.setWidth("600px");
		this.textAppraisers.setHeight("100px");
		this.textAppraisers.setEnabled(false);
		
		HorizontalLayout h1 = new HorizontalLayout(this.textDate, this.textLocal);
		h1.setSpacing(true);
		
		VerticalLayout layout = new VerticalLayout(this.textTitle, this.textStudent, this.textCompany, h1, this.textAppraisers);
		layout.setSpacing(true);
		layout.setMargin(true);
		
		if((event.getJury() != null) && (event.getJury().getIdJury() != 0)) {
			this.setCaption("Banca de TCC " + String.valueOf(event.getJury().getStage()));
			
			this.textCompany.setVisible(false);
			
			this.textStudent.setValue(event.getJury().getStudent().getName());
			this.textTitle.setValue(event.getJury().getStage() == 2 ? event.getJury().getThesis().getTitle() : event.getJury().getProject().getTitle());
			this.textDate.setValue(DateUtils.format(event.getJury().getDate(), "dd/MM/yyyy HH:mm"));
			this.textLocal.setValue(event.getJury().getLocal());
			
			for(JuryAppraiser appraiser : event.getJury().getAppraisers()){
				this.textAppraisers.setValue(this.textAppraisers.getValue() + appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")) + "\n");
			}
		} else if((event.getJuryRequest() != null) && (event.getJuryRequest().getIdJuryRequest() != 0)) {
			this.setCaption("Banca de TCC " + String.valueOf(event.getJuryRequest().getStage()));
			
			this.textCompany.setVisible(false);
			
			this.textStudent.setValue(event.getJuryRequest().getStudent());
			this.textTitle.setValue(event.getJuryRequest().getTitle());
			this.textDate.setValue(DateUtils.format(event.getJuryRequest().getDate(), "dd/MM/yyyy HH:mm"));
			this.textLocal.setValue(event.getJuryRequest().getLocal());
			
			for(JuryAppraiserRequest appraiser : event.getJuryRequest().getAppraisers()){
				this.textAppraisers.setValue(this.textAppraisers.getValue() + appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")) + "\n");
			}
		} else {
			this.setCaption("Banca de Estágio");
			
			this.textStudent.setValue(event.getInternshipJury().getStudent().getName());
			this.textTitle.setValue(event.getInternshipJury().getInternship().getReportTitle());
			this.textCompany.setValue(event.getInternshipJury().getInternship().getCompany().getName());
			this.textDate.setValue(DateUtils.format(event.getInternshipJury().getDate(), "dd/MM/yyyy HH:mm"));
			this.textLocal.setValue(event.getInternshipJury().getLocal());
			
			for(InternshipJuryAppraiser appraiser : event.getInternshipJury().getAppraisers()){
				this.textAppraisers.setValue(this.textAppraisers.getValue() + appraiser.getAppraiser().getName() + (appraiser.isSubstitute() ? " (suplente)" : (appraiser.isChair() ? " (presidente)" : "")) + "\n");
			}
		}
		
		this.setContent(layout);
		this.setModal(true);
        this.center();
        this.setResizable(false);
	}
	
}
