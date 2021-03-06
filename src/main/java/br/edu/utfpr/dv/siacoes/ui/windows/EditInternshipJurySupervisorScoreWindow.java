package br.edu.utfpr.dv.siacoes.ui.windows;

import java.sql.SQLException;
import java.util.logging.Level;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.Jury.JuryResult;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class EditInternshipJurySupervisorScoreWindow extends EditWindow {
	
	private final InternshipJury jury;
	
	private final TextField textStudent;
	private final TextField textCompany;
	private final NumberField textSupervisorScore;
	private final Select<JuryResult> comboResult;
	
	public EditInternshipJurySupervisorScoreWindow(InternshipJury jury) {
		super("Lançar Nota do Orientador", null);
		
		this.jury = jury;
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("600px");
		this.textStudent.setEnabled(false);
		
		this.textCompany = new TextField("Empresa");
		this.textCompany.setWidth("600px");
		this.textCompany.setEnabled(false);
		
		this.textSupervisorScore = new NumberField("Nota do Orientador");
		this.textSupervisorScore.setWidth("250px");
		
		this.comboResult = new Select<JuryResult>();
		this.comboResult.setLabel("Resultado Final");
		this.comboResult.setWidth("200px");
		this.comboResult.setItems(JuryResult.NONE, JuryResult.APPROVED, JuryResult.APPROVEDWITHRESERVATIONS, JuryResult.DISAPPROVED);
		this.comboResult.setValue(JuryResult.NONE);
		
		this.addField(this.textStudent);
		this.addField(this.textCompany);
		this.addField(this.textSupervisorScore);
		this.addField(this.comboResult);
		
		this.loadScore();
	}
	
	private void loadScore() {
		this.textStudent.setValue(this.jury.getInternship().getStudent().getName());
		this.textCompany.setValue(this.jury.getInternship().getCompany().getName());
		this.textSupervisorScore.setValue(this.jury.getSupervisorScore());
		this.comboResult.setValue(this.jury.getResult());
		
		try {
			if(Document.hasSignature(DocumentType.INTERNSHIPJURY, this.jury.getIdInternshipJury())) {
				this.disableButtons();
			}
		} catch (SQLException e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
	}

	@Override
	public void save() {
		try {
			new InternshipJuryBO().saveSupervisorScore(Session.getIdUserLog(), this.jury.getIdInternshipJury(), this.textSupervisorScore.getValue(), this.comboResult.getValue());
			
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Nota", e.getMessage());
		}
	}

}
