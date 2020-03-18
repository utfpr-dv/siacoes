package br.edu.utfpr.dv.siacoes.window;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class EditInternshipJurySupervisorScoreWindow extends EditWindow {
	
	private final InternshipJury jury;
	
	private final TextField textStudent;
	private final TextField textCompany;
	private final TextField textSupervisorScore;
	
	public EditInternshipJurySupervisorScoreWindow(InternshipJury jury) {
		super("Lançar Nota do Orientador", null);
		
		this.jury = jury;
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("600px");
		this.textStudent.setEnabled(false);
		
		this.textCompany = new TextField("Empresa");
		this.textCompany.setWidth("600px");
		this.textCompany.setEnabled(false);
		
		this.textSupervisorScore = new TextField("Nota do Orientador");
		this.textSupervisorScore.setWidth("100px");
		
		this.addField(this.textStudent);
		this.addField(this.textCompany);
		this.addField(this.textSupervisorScore);
		
		this.loadScore();
	}
	
	private void loadScore() {
		this.textStudent.setValue(this.jury.getInternship().getStudent().getName());
		this.textCompany.setValue(this.jury.getInternship().getCompany().getName());
		this.textSupervisorScore.setValue(String.format("%.2f", this.jury.getSupervisorScore()));
		
		try {
			if(Document.hasSignature(DocumentType.INTERNSHIPJURY, this.jury.getIdInternshipJury())) {
				this.disableButtons();
			}
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
		}
	}

	@Override
	public void save() {
		try {
			new InternshipJuryBO().saveSupervisorScore(Session.getIdUserLog(), this.jury.getIdInternshipJury(), Double.parseDouble(this.textSupervisorScore.getValue().replace(",", ".")));
			
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Nota", e.getMessage());
		}
	}

}
