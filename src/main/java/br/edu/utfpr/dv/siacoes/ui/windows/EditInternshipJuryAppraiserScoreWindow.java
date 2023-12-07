package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipJuryAppraiserScoreBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiserScore;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;

public class EditInternshipJuryAppraiserScoreWindow extends EditWindow {
	
	private final InternshipJuryAppraiser appraiser;
	
	private final TextField textAppraiser;
	private final VerticalLayout layoutEvaluationItems;
	private final TextArea textComments;
	private final Tabs tab;
	
	public EditInternshipJuryAppraiserScoreWindow(InternshipJuryAppraiser appraiser){
		super("Lançar Notas", null);
		
		if(appraiser == null){
			this.appraiser = new InternshipJuryAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		this.textAppraiser = new TextField("Membro");
		this.textAppraiser.setWidth("800px");
		this.textAppraiser.setEnabled(false);
		
		this.layoutEvaluationItems = new VerticalLayout();
		this.layoutEvaluationItems.setSpacing(false);
		this.layoutEvaluationItems.setMargin(false);
		this.layoutEvaluationItems.setPadding(false);
		this.layoutEvaluationItems.setWidth("800px");
		this.layoutEvaluationItems.setHeight("300px");
		this.layoutEvaluationItems.getStyle().set("overflow", "auto");
		
		Label labelDescription = new Label("Quesito");
		labelDescription.setWidth("600px");
		labelDescription.getStyle().set("font-weight", "bold");
		
		Label labelPonderosity = new Label("Peso");
		labelPonderosity.setWidth("50px");
		labelPonderosity.getStyle().set("font-weight", "bold");
		
		Label labelScore = new Label("Nota");
		labelScore.setWidth("100px");
		labelScore.getStyle().set("font-weight", "bold");
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("300px");
		this.textComments.setVisible(false);
		
		VerticalLayout tab1 = new VerticalLayout(new HorizontalLayout(labelDescription, labelPonderosity, labelScore), this.layoutEvaluationItems);
		tab1.setSpacing(false);
		tab1.setMargin(false);
		tab1.setPadding(false);
		
		Tab t1 = new Tab("Notas");
		Tab t2 = new Tab("Observações");
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(t1, tab1);
		tabsToPages.put(t2, this.textComments);
		Div pages = new Div(tab1, this.textComments);
		
		this.tab = new Tabs(t1, t2);
		this.tab.setWidthFull();
		this.tab.setFlexGrowForEnclosedTabs(1);
		
		this.tab.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(this.tab.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		this.tab.setSelectedTab(t1);
		
		VerticalLayout layout = new VerticalLayout(this.tab, pages);
		layout.setWidth("800px");
		layout.setHeight("370px");
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setPadding(false);
		
		this.addField(this.textAppraiser);
		this.addField(layout);
		
		this.loadScores();
	}
	
	private void loadScores(){
		try {
			InternshipJuryAppraiserScoreBO bo = new InternshipJuryAppraiserScoreBO();
			
			this.appraiser.setScores(bo.listScores(this.appraiser.getIdInternshipJuryAppraiser()));
			
			this.textAppraiser.setValue(this.appraiser.getAppraiser().getName());
			this.textComments.setValue(this.appraiser.getComments());
			this.layoutEvaluationItems.removeAll();
			
			for(InternshipJuryAppraiserScore score : this.appraiser.getScores()){
				Label labelDescription = new Label(score.getInternshipEvaluationItem().getDescription());
				labelDescription.setWidth("600px");
				
				Label labelPonderosity = new Label(String.valueOf(score.getInternshipEvaluationItem().getPonderosity()));
				labelPonderosity.setWidth("50px");
				
				NumberField textScore = new NumberField();
				textScore.setValue(score.getFormattedScore());
				textScore.setMin(0);
				textScore.setMax(score.getInternshipEvaluationItem().getPonderosity());
				textScore.setWidth("100px");
				textScore.setId("ei" + String.valueOf(score.getInternshipEvaluationItem().getIdInternshipEvaluationItem()));
				
				this.layoutEvaluationItems.add(new HorizontalLayout(labelDescription, labelPonderosity, textScore));
			}
			
			if((this.appraiser.getInternshipJury().getIdInternshipJury() != 0) && Document.hasSignature(DocumentType.INTERNSHIPJURY, this.appraiser.getInternshipJury().getIdInternshipJury())) {
				this.disableButtons();
			}
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.disableButtons();
			this.showErrorNotification("Carregar Notas", e.getMessage());
		}
	}
	
	private List<NumberField> getFields() {
		List<NumberField> ret = new ArrayList<NumberField>();
		
		Object lines[] = this.layoutEvaluationItems.getChildren().filter(child -> {
			if(child instanceof HorizontalLayout) {
				return true;
			} else {
				return false;
			}
		}).toArray();
		
		for(Object l : lines) {
			NumberField field = (NumberField) ((HorizontalLayout)l).getChildren().filter(child -> {
				if(child instanceof NumberField) {
					return true;
				} else {
					return false;
				}
			}).findFirst().get();
			
			if(field != null) {
				ret.add(field);
			}
		}
		
		return ret;
	}
	
	private void fillScores() {
		List<NumberField> fields = this.getFields();
		
		for(InternshipJuryAppraiserScore jas : this.appraiser.getScores()) {
			for(NumberField field : fields) {
				String id = "0";
				
				if(field.getId().isPresent()) {
					id = field.getId().get().replace("ei", "");	
				}
				
				if(jas.getInternshipEvaluationItem().getIdInternshipEvaluationItem() == Integer.parseInt(id)) {
					jas.setScore(field.getValue());
				}
			}
		}
	}
	
	@Override
	public void save() {
		try{
			this.fillScores();
			
			new InternshipJuryAppraiserScoreBO().save(Session.getIdUserLog(), this.appraiser.getScores());
			
			this.appraiser.setComments(this.textComments.getValue());
			
			new InternshipJuryAppraiserBO().save(Session.getIdUserLog(), this.appraiser);
			
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Notas", e.getMessage());
		}
	}

}
