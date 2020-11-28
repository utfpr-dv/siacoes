package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;

public class EditInternshipJuryAppraiserSingleScoreWindow extends EditWindow {

	private final InternshipJuryAppraiser appraiser;
	
	private final TextField textStudent;
	private final TextField textCompany;
	private final TextField textAppraiser;
	private final NumberField textScore;
	private final TextArea textComments;
	
	public EditInternshipJuryAppraiserSingleScoreWindow(InternshipJuryAppraiser appraiser) {
		super("Lançar Nota", null);
		
		if(appraiser == null){
			this.appraiser = new InternshipJuryAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		this.textStudent = new TextField("Acadêmico(a)");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		
		this.textCompany = new TextField("Empresa");
		this.textCompany.setWidth("800px");
		this.textCompany.setEnabled(false);
		
		this.textAppraiser = new TextField("Membro");
		this.textAppraiser.setWidth("800px");
		this.textAppraiser.setEnabled(false);
		
		this.textScore = new NumberField("Nota");
		this.textScore.setMin(0);
		this.textScore.setMax(10);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("300px");
		this.textComments.setVisible(false);
		
		this.addField(this.textStudent);
		//this.addField(this.textCompany);
		this.addField(this.textAppraiser);
		this.addField(this.textScore);
		this.addField(this.textComments);
		
		this.loadScore();
	}
	
	private void loadScore() {
		//this.textCompany.setValue(this.appraiser.getInternshipJury().getInternship().getCompany().getName());
		this.textStudent.setValue(this.appraiser.getInternshipJury().getStudent().getName());
		this.textAppraiser.setValue(this.appraiser.getAppraiser().getName());
		this.textScore.setValue(this.appraiser.getScore());
		this.textComments.setValue(this.appraiser.getComments());
	}
	
	@Override
	public void save() {
		try {
			this.appraiser.setScore(this.textScore.getValue());
			this.appraiser.setComments(this.textComments.getValue());
			
			new InternshipJuryAppraiserBO().updateScore(Session.getIdUserLog(), this.appraiser);
			
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Nota", e.getMessage());
		}
	}
	
}
