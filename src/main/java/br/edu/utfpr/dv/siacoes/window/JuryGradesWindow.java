package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
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
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserDetailReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserScoreReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class JuryGradesWindow extends BasicWindow {
	
	private final Button buttonSign;
	
	private final Jury jury;

	public JuryGradesWindow(Jury jury) throws Exception {
		super("Avaliação da Banca");
		
		if(jury == null) {
			this.jury = new Jury();
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
		
		this.setWidth("800px");
		this.setHeight("530px");
		
		this.loadGrades();
	}
	
	private void loadGrades() throws Exception {
		JuryBO bo = new JuryBO();
		
		if((this.jury.getIdJury() != 0) && (bo.hasScores(this.jury.getIdJury()))) {
			JuryFormReport report = bo.getJuryFormReport(this.jury.getIdJury());
			
			TabSheet tab = new TabSheet();
			tab.setSizeFull();
			tab.addStyleName(ValoTheme.TABSHEET_FRAMED);
			tab.addStyleName(ValoTheme.TABSHEET_EQUAL_WIDTH_TABS);
			tab.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
			
			VerticalLayout layoutMain = new VerticalLayout(tab);
			layoutMain.setSpacing(true);
			layoutMain.setMargin(true);
			
			Grid gridGeneral = new Grid();
			gridGeneral.setWidth("100%");
			gridGeneral.setHeight("245px");
			gridGeneral.addColumn("", String.class);
			gridGeneral.addColumn("Avaliador", String.class);
			gridGeneral.addColumn("Escrita", Double.class);
			gridGeneral.addColumn("Apresentação", Double.class);
			gridGeneral.addColumn("Arguição", Double.class);
			gridGeneral.addColumn("Total", Double.class);
			
			for(JuryFormAppraiserScoreReport appraiser : report.getScores()) {
				gridGeneral.addRow(appraiser.getDescription(), appraiser.getName(), appraiser.getScoreWriting(), appraiser.getScoreOral(), appraiser.getScoreArgumentation(), (appraiser.getScoreWriting() + appraiser.getScoreOral() + appraiser.getScoreArgumentation()));
			}
			
			TextField textScore = new TextField();
			textScore.setCaption(null);
			textScore.setEnabled(false);
			textScore.setWidth("100px");
			textScore.setValue(String.format("%.2f", report.getScore()));
			
			Label labelScore = new Label("Média Final:");
			
			HorizontalLayout layoutScore = new HorizontalLayout(labelScore, textScore);
			layoutScore.setComponentAlignment(labelScore, Alignment.MIDDLE_RIGHT);
			layoutScore.setSpacing(true);
			
			TextArea textComments = new TextArea("Comentários");
			textComments.setWidth("100%");
			textComments.setHeight("100px");
			textComments.setValue(report.getComments());
			textComments.addStyleName("textscroll");
			
			VerticalLayout tab1 = new VerticalLayout(gridGeneral, layoutScore, textComments);
			tab1.setComponentAlignment(layoutScore, Alignment.MIDDLE_RIGHT);
			tab1.setSpacing(true);
			
			tab.addTab(tab1, "Geral");
			
			for(JuryFormAppraiserReport appraiser : report.getAppraisers()) {
				TextField textAppraiser = new TextField("Avaliador:");
				textAppraiser.setWidth("100%");
				textAppraiser.setEnabled(false);
				textAppraiser.setValue(appraiser.getName());
				
				Grid gridScores = new Grid();
				gridScores.setWidth("100%");
				gridScores.setHeight("225px");
				gridScores.addColumn("Quesito", String.class);
				gridScores.addColumn("Peso", Double.class);
				gridScores.addColumn("Nota", Double.class);
				
				for(JuryFormAppraiserDetailReport scores : appraiser.getDetail()) {
					gridScores.addRow(scores.getEvaluationItem(), scores.getPonderosity(), scores.getScore());
				}
				
				TextArea textAppraiserComments = new TextArea("Comentários");
				textAppraiserComments.setWidth("100%");
				textAppraiserComments.setHeight("100px");
				textAppraiserComments.setValue(appraiser.getComments());
				textAppraiserComments.addStyleName("textscroll");
				
				VerticalLayout tabAppraiser = new VerticalLayout(textAppraiser, gridScores, textAppraiserComments);
				tabAppraiser.setSpacing(true);
				
				tab.addTab(tabAppraiser, appraiser.getDescription());
			}
			
			if(Session.getUser().getIdUser() == new JuryAppraiserBO().findChair(this.jury.getIdJury()).getAppraiser().getIdUser()) {
				HorizontalLayout buttons = new HorizontalLayout(this.buttonSign);
				buttons.setSpacing(true);
				layoutMain.addComponent(buttons);
				this.setHeight("560px");
				
				if(Document.hasSignature(DocumentType.JURY, this.jury.getIdJury(), Session.getUser().getIdUser())) {
					this.buttonSign.setEnabled(false);
				}
			}
			
			this.setContent(layoutMain);
		} else {
			this.close();
		}
	}
	
	private void sign() {
		if(this.jury.getIdJury() == 0) {
			this.showWarningNotification("Assinar Ficha", "É necessário salvar a banca antes de assinar.");
		} else {
			try {
				if(!new JuryBO().hasAllScores(this.jury.getIdJury())) {
					this.showWarningNotification("Assinar Ficha", "Todas as notas devem ser lançadas para que a ficha de avaliação possa ser assinada.");
				} else {
					JuryFormReport report = new JuryBO().getJuryFormReport(this.jury.getIdJury());
					
					UI.getCurrent().addWindow(new SignatureWindow(DocumentType.JURY, this.jury.getIdJury(), SignDatasetBuilder.buildJury(report), SignDatasetBuilder.getSignaturesList(report), null, null));
				}
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Ficha", e.getMessage());
			}
		}
	}
	
}
