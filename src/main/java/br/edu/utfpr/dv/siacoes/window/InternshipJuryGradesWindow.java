package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryBO;
import br.edu.utfpr.dv.siacoes.bo.SigesConfigBO;
import br.edu.utfpr.dv.siacoes.model.Internship;
import br.edu.utfpr.dv.siacoes.model.InternshipJury;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryFormReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.SigesConfig;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class InternshipJuryGradesWindow extends BasicWindow {
	
	private final TextField textStudent;
	private final TextField textSupervisor;
	private final TextField textCompany;
	private final VerticalLayout layout;
	private final Button buttonSign;
	
	private final InternshipJury jury;
	
	public InternshipJuryGradesWindow(InternshipJury jury) throws Exception {
		super("Avaliação da Banca");
		
		if(jury == null) {
			this.jury = new InternshipJury();
		} else {
			this.jury = jury;
		}
		
		this.buttonSign = new Button("Assinar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	sign();
            }
        });
		this.buttonSign.setWidth("150px");
		this.buttonSign.setIcon(FontAwesome.PENCIL);
		this.buttonSign.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		this.buttonSign.setDisableOnClick(true);
		
		this.textStudent = new TextField("Acadêmico(a)");
		this.textStudent.setEnabled(false);
		this.textStudent.setWidth("800px");
		
		this.textSupervisor = new TextField("Acadêmico(a)");
		this.textSupervisor.setEnabled(false);
		this.textSupervisor.setWidth("800px");
		
		this.textCompany = new TextField("Acadêmico(a)");
		this.textCompany.setEnabled(false);
		this.textCompany.setWidth("800px");
		
		this.layout = new VerticalLayout(this.textStudent, this.textSupervisor, this.textCompany);
		this.layout.setSpacing(true);
		this.layout.setMargin(true);
		
		this.setWidth("820px");
		this.setHeight("600px");
		
		this.setContent(this.layout);
		
		this.loadGrades();
	}
	
	private void loadGrades() throws Exception {
		if((this.jury.getIdInternshipJury() != 0) && (new InternshipJuryBO().hasScores(this.jury.getIdInternshipJury()))) {
			InternshipJuryFormReport report = new InternshipJuryBO().getJuryFormReport(this.jury.getIdInternshipJury());
			Internship internship = new InternshipBO().findById(this.jury.getInternship().getIdInternship());
			
			this.textStudent.setValue(internship.getStudent().getName());
			this.textSupervisor.setValue(internship.getSupervisor().getName());
			this.textCompany.setValue(internship.getCompany().getName());
			
			TabSheet tab = new TabSheet();
			tab.setWidth("800px");
			tab.setHeight("360px");
			
			HorizontalLayout h1 = new HorizontalLayout();
			h1.setWidth("100%");
			h1.addComponent(this.buildLabel("Itens Avaliados", "100%", true, false, true));
			h1.addComponent(this.buildLabel("Peso", "75px", true, true, true));
			h1.addComponent(this.buildLabel("Aval. 1", "75px", true, true, true));
			h1.addComponent(this.buildLabel("Aval. 2", "75px", true, true, true));
			h1.setExpandRatio(h1.getComponent(0), 1f);
			
			HorizontalLayout h2 = new HorizontalLayout();
			h2.setWidth("100%");
			h2.addComponent(this.buildLabel("Banca examinadora – avaliação do relatório e da apresentação da defesa (esta última se houver), com notas atribuídas seguindo os critérios descritos na ficha de avalição individual.", "100%", true, false, false));
			h2.addComponent(this.buildLabel(String.format("%.2f", report.getAppraiser1Score()), "75px", true, true, false));
			h2.addComponent(this.buildLabel(String.format("%.2f", report.getAppraiser2Score()), "75px", true, true, false));
			h2.setExpandRatio(h2.getComponent(0), 1f);
			h2.getComponent(1).setHeight("100%");
			h2.getComponent(2).setHeight("100%");
			
			HorizontalLayout h3 = new HorizontalLayout();
			h3.setWidth("100%");
			h3.addComponent(this.buildLabel("Nota banca examinadora (média aritmética)", "100%", true, false, true));
			h3.addComponent(this.buildLabel(String.format("%.1f", report.getAppraisersPonderosity()), "75px", true, true, false));
			h3.addComponent(this.buildLabel(String.format("%.2f", (report.getAppraiser1Score() + report.getAppraiser2Score()) / 2), "150px", true, true, false));
			h3.setExpandRatio(h3.getComponent(0), 1f);
			
			HorizontalLayout h4 = new HorizontalLayout();
			h4.setWidth("100%");
			h4.addComponent(this.buildLabel("Supervisão - Nota atribuída a partir do relatório de avaliação do supervisor.", "100%", true, false, false));
			h4.addComponent(this.buildLabel(String.format("%.1f", report.getCompanySupervisorPonderosity()), "75px", true, true, false));
			h4.addComponent(this.buildLabel(String.format("%.2f", report.getCompanySupervisorScore()), "150px", true, true, false));
			h4.setExpandRatio(h4.getComponent(0), 1f);
			
			HorizontalLayout h5 = new HorizontalLayout();
			h5.setWidth("100%");
			h5.addComponent(this.buildLabel("Orientação - Nota atribuída a partir do relatório de acompanhamento e relatório final.", "100%", true, false, false));
			h5.addComponent(this.buildLabel(String.format("%.1f", report.getSupervisorPonderosity()), "75px", true, true, false));
			h5.addComponent(this.buildLabel(String.format("%.2f", report.getSupervisorScore()), "150px", true, true, false));
			h5.setExpandRatio(h5.getComponent(0), 1f);
			
			HorizontalLayout h6 = new HorizontalLayout();
			h6.setWidth("100%");
			h6.addComponent(this.buildLabel("NOTA FINAL (MÉDIA PONDERADA)", "100%", true, false, true));
			h6.addComponent(this.buildLabel(String.format("%.2f", report.getFinalScore()), "150px", true, true, false));
			h6.setExpandRatio(h6.getComponent(0), 1f);
			
			VerticalLayout layoutGrades = new VerticalLayout(h1, h2, h3, h4, h5, h6);
			layoutGrades.setWidth("100%");
			
			TextArea textComments = new TextArea("Comentários");
			textComments.setWidth("100%");
			textComments.setHeight("75px");
			textComments.setEnabled(false);
			textComments.setValue(report.getComments());
			
			VerticalLayout tab1 = new VerticalLayout(layoutGrades, textComments);
			tab1.setSpacing(true);
			
			tab.addTab(tab1, "Geral");
			
			for(JuryFormAppraiserReport appraiser : report.getAppraisers()) {
				if(!appraiser.getName().equals(internship.getSupervisor().getName()) || this.jury.isSupervisorFillJuryForm()) {
					TextField textAppraiser = new TextField("Avaliador:");
					textAppraiser.setWidth("100%");
					textAppraiser.setEnabled(false);
					textAppraiser.setValue(appraiser.getName());
					
					Grid gridScores = new Grid();
					gridScores.setWidth("100%");
					gridScores.setHeight("150px");
					gridScores.addColumn("Quesito", String.class);
					gridScores.addColumn("Peso", Double.class);
					gridScores.addColumn("Nota", Double.class);
					
					for(JuryFormAppraiserDetailReport scores : appraiser.getDetail()) {
						gridScores.addRow(scores.getEvaluationItem(), scores.getPonderosity(), scores.getScore());
					}
					
					TextArea textAppraiserComments = new TextArea("Comentários");
					textAppraiserComments.setWidth("100%");
					textAppraiserComments.setHeight("75px");
					textAppraiserComments.setEnabled(false);
					textAppraiserComments.setValue(appraiser.getComments());
					
					VerticalLayout tabAppraiser = new VerticalLayout(textAppraiser, gridScores, textAppraiserComments);
					tabAppraiser.setSpacing(true);
					
					tab.addTab(tabAppraiser, appraiser.getDescription());
				}
			}
			
			this.layout.addComponent(tab);
			
			if(Session.getUser().getIdUser() == new InternshipJuryAppraiserBO().findChair(this.jury.getIdInternshipJury()).getAppraiser().getIdUser()) {
				HorizontalLayout buttons = new HorizontalLayout(this.buttonSign);
				buttons.setSpacing(true);
				this.layout.addComponent(buttons);
				this.setHeight("635px");
				
				if(Document.hasSignature(DocumentType.INTERNSHIPJURY, this.jury.getIdInternshipJury(), Session.getUser().getIdUser())) {
					this.buttonSign.setEnabled(false);
				}
			}
		} else {
			throw new Exception("Ainda não foram lançadas as notas da banca.");
		}
	}
	
	private Label buildLabel(String text, String width, boolean border, boolean center, boolean bold) {
		Label label = new Label(text);
		label.setWidth(width);
		if(border)
			label.addStyleName("Border");
		if(center)
			label.addStyleName("CenterText");
		if(bold)
			label.addStyleName("BoldText");
		return label;
	}
	
	private void sign() {
		if(this.jury.getIdInternshipJury() == 0) {
			this.showWarningNotification("Assinar Ficha", "É necessário salvar a banca antes de assinar.");
		} else {
			try {
				if(!new InternshipJuryBO().hasAllScores(this.jury.getIdInternshipJury())) {
					this.showWarningNotification("Assinar Ficha", "Todas as notas devem ser lançadas para que a ficha de avaliação possa ser assinada.");
				} else {
					InternshipJuryFormReport report = new InternshipJuryBO().getJuryFormReport(this.jury.getIdInternshipJury());
					
					UI.getCurrent().addWindow(new SignatureWindow(DocumentType.INTERNSHIPJURY, this.jury.getIdInternshipJury(), SignDatasetBuilder.build(report), SignDatasetBuilder.getSignaturesList(report), null, null));
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Ficha", e.getMessage());
			}
		}
	}

}
