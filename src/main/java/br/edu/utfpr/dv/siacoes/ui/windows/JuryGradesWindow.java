package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.JuryBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Jury;
import br.edu.utfpr.dv.siacoes.model.JuryFormAppraiserReport;
import br.edu.utfpr.dv.siacoes.model.JuryFormReport;
import br.edu.utfpr.dv.siacoes.sign.SignDatasetBuilder;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryAppraiserGradeDataSource;
import br.edu.utfpr.dv.siacoes.ui.grid.JuryGradeDataSource;
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
		
		this.buttonSign = new Button("Assinar", new Icon(VaadinIcon.PENCIL), event -> {
            sign();
        });
		this.buttonSign.setWidth("150px");
		this.buttonSign.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.buttonSign.setDisableOnClick(true);
		
		this.setWidth("800px");
		this.setHeight("570px");
		
		this.loadGrades();
	}
	
	private void loadGrades() throws Exception {
		if((this.jury.getIdJury() != 0) && (new JuryBO().hasScores(this.jury.getIdJury()))) {
			JuryFormReport report = new JuryBO().getJuryFormReport(this.jury.getIdJury());
			
			Tabs tab = new Tabs();
			tab.setWidthFull();
			tab.setFlexGrowForEnclosedTabs(1);
			
			Map<Tab, Component> tabsToPages = new HashMap<>();
			
			Div pages = new Div();
			
			tab.addSelectedChangeListener(event -> {
			    tabsToPages.values().forEach(page -> page.setVisible(false));
			    Component selectedPage = tabsToPages.get(tab.getSelectedTab());
			    selectedPage.setVisible(true);
			});
			
			VerticalLayout layoutMain = new VerticalLayout(tab, pages);
			layoutMain.setSpacing(false);
			layoutMain.setMargin(false);
			layoutMain.setMargin(false);
			
			Grid<JuryGradeDataSource> gridGeneral = new Grid<JuryGradeDataSource>();
			gridGeneral.setWidth("100%");
			gridGeneral.setHeight("245px");
			gridGeneral.addColumn(JuryGradeDataSource::getDescription).setHeader("");
			gridGeneral.addColumn(JuryGradeDataSource::getAppraiser).setHeader("Avaliador");
			gridGeneral.addColumn(JuryGradeDataSource::getWriting).setHeader("Escrita");
			gridGeneral.addColumn(JuryGradeDataSource::getOral).setHeader("Apresentação");
			gridGeneral.addColumn(JuryGradeDataSource::getArgumentation).setHeader("Arguição");
			gridGeneral.addColumn(JuryGradeDataSource::getTotal).setHeader("Total");
			
			gridGeneral.setItems(JuryGradeDataSource.load(report.getScores()));
			
			TextField textScore = new TextField();
			textScore.setEnabled(false);
			textScore.setWidth("100px");
			textScore.setValue(String.format("%.2f", report.getScore()));
			
			Label labelScore = new Label("Média Final:");
			labelScore.getElement().getStyle().set("margin-left", "auto");
			
			HorizontalLayout layoutScore = new HorizontalLayout(labelScore, textScore);
			layoutScore.setSpacing(true);
			layoutScore.setMargin(false);
			layoutScore.setPadding(false);
			layoutScore.getElement().getStyle().set("margin-left", "auto");
			
			TextArea textComments = new TextArea("Comentários");
			textComments.setWidth("100%");
			textComments.setHeight("100px");
			textComments.setValue(report.getComments());
			
			VerticalLayout tab1 = new VerticalLayout(gridGeneral, layoutScore, textComments);
			tab1.setSpacing(false);
			tab1.setMargin(false);
			tab1.setPadding(false);
			tab1.setWidth("730px");
			
			Tab t1 = new Tab("Geral");
			tabsToPages.put(t1, tab1);
			tab.add(t1);
			pages.add(tab1);
			
			for(JuryFormAppraiserReport appraiser : report.getAppraisers()) {
				TextField textAppraiser = new TextField("Avaliador:");
				textAppraiser.setWidth("100%");
				textAppraiser.setEnabled(false);
				textAppraiser.setValue(appraiser.getName());
				
				Grid<JuryAppraiserGradeDataSource> gridScores = new Grid<JuryAppraiserGradeDataSource>();
				gridScores.setWidth("100%");
				gridScores.setHeight("215px");
				gridScores.addColumn(JuryAppraiserGradeDataSource::getEvaluationItem).setHeader("Quesito");
				gridScores.addColumn(JuryAppraiserGradeDataSource::getPonderosity).setHeader("Peso");
				gridScores.addColumn(JuryAppraiserGradeDataSource::getScore).setHeader("Nota");
				
				gridScores.setItems(JuryAppraiserGradeDataSource.load(appraiser.getDetail()));
				
				TextArea textAppraiserComments = new TextArea("Comentários");
				textAppraiserComments.setWidth("100%");
				textAppraiserComments.setHeight("100px");
				textAppraiserComments.setValue(appraiser.getComments());
				
				VerticalLayout tabAppraiser = new VerticalLayout(textAppraiser, gridScores, textAppraiserComments);
				tabAppraiser.setSpacing(false);
				tabAppraiser.setMargin(false);
				tabAppraiser.setPadding(false);
				tabAppraiser.setVisible(false);
				tabAppraiser.setWidth("730px");
				
				Tab t = new Tab(appraiser.getDescription());
				tabsToPages.put(t, tabAppraiser);
				tab.add(t);
				pages.add(tabAppraiser);
			}
			
			tab.setSelectedTab(t1);
			
			if(Session.getUser().getIdUser() == new JuryAppraiserBO().findChair(this.jury.getIdJury()).getAppraiser().getIdUser()) {
				HorizontalLayout buttons = new HorizontalLayout(this.buttonSign);
				buttons.setSpacing(true);
				buttons.setMargin(false);
				buttons.setPadding(false);
				layoutMain.add(buttons);
				this.setHeight("600px");
				
				if(Document.hasSignature(DocumentType.JURY, this.jury.getIdJury(), Session.getUser().getIdUser())) {
					this.buttonSign.setEnabled(false);
				}
			}
			
			this.add(layoutMain);
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
					
					SignatureWindow window = new SignatureWindow(DocumentType.JURY, this.jury.getIdJury(), SignDatasetBuilder.buildJury(report), SignDatasetBuilder.getSignaturesList(report), null, null);
					window.open();
				}
			} catch (Exception e) {
				Logger.log(Level.SEVERE, e.getMessage(), e);
				
				this.showErrorNotification("Assinar Ficha", e.getMessage());
			}
		}
	}
	
}
