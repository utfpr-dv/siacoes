package br.edu.utfpr.dv.siacoes.window;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserScoreBO;
import br.edu.utfpr.dv.siacoes.dao.ConnectionDAO;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserScore;

public class EditJuryAppraiserScoreWindow extends EditWindow {
	
	private final JuryAppraiser appraiser;
	
	private final TextField textAppraiser;
	private final VerticalLayout layoutEvaluationItems;
	private final TextArea textComments;
	private final TabSheet tab;
	
	public EditJuryAppraiserScoreWindow(JuryAppraiser appraiser){
		super("Lançar Notas", null);
		
		if(appraiser == null){
			this.appraiser = new JuryAppraiser();
		}else{
			this.appraiser = appraiser;
		}
		
		this.textAppraiser = new TextField("Membro");
		this.textAppraiser.setWidth("800px");
		this.textAppraiser.setEnabled(false);
		
		this.layoutEvaluationItems = new VerticalLayout();
		
		Label labelDescription = new Label("Quesito");
		labelDescription.setWidth("650px");
		
		Label labelPonderosity = new Label("Peso");
		labelPonderosity.setWidth("50px");
		
		Label labelScore = new Label("Nota");
		labelScore.setWidth("100px");
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("300px");
		
		this.tab = new TabSheet();
		
		VerticalLayout tab1 = new VerticalLayout(new HorizontalLayout(labelDescription, labelPonderosity, labelScore), this.layoutEvaluationItems);
		this.tab.addTab(tab1, "Notas");
		this.tab.addTab(this.textComments);
		
		this.addField(this.textAppraiser);
		this.addField(this.tab);
		
		this.loadScores();
	}
	
	private void loadScores(){
		try {
			JuryAppraiserScoreBO bo = new JuryAppraiserScoreBO();
			
			this.appraiser.setScores(bo.listScores(this.appraiser.getIdJuryAppraiser()));
			
			this.textAppraiser.setValue(this.appraiser.getAppraiser().getName());
			this.textComments.setValue(this.appraiser.getComments());
			this.layoutEvaluationItems.removeAllComponents();
			
			for(JuryAppraiserScore score : this.appraiser.getScores()){
				Label labelDescription = new Label(score.getEvaluationItem().getDescription());
				labelDescription.setWidth("650px");
				
				Label labelPonderosity = new Label(String.valueOf(score.getEvaluationItem().getPonderosity()));
				labelPonderosity.setWidth("50px");
				
				TextField textScore = new TextField();
				textScore.setValue(String.valueOf(score.getScore()));
				textScore.setWidth("100px");
				
				this.layoutEvaluationItems.addComponent(new HorizontalLayout(labelDescription, labelPonderosity, textScore));
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Carregar Notas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void save() {
		try{
			JuryAppraiserScoreBO bo = new JuryAppraiserScoreBO();
			
			this.appraiser.setComments(this.textComments.getValue());
		
			ConnectionDAO.getInstance().getConnection().setAutoCommit(false);
			
			for(int i = 0; i < this.appraiser.getScores().size(); i++){
				double score = 0;
				
				try{
					score = Double.parseDouble(((TextField)((HorizontalLayout)this.layoutEvaluationItems.getComponent(i)).getComponent(2)).getValue());
				}catch(Exception e){
					throw new Exception("A nota para o quesito " + String.valueOf(i + 1) + " está em um formato incorreto.");
				}
				
				JuryAppraiserScore jas = this.appraiser.getScores().get(i);
				jas.setScore(score);
				
				bo.save(jas);
			}
			
			ConnectionDAO.getInstance().getConnection().commit();
			
			this.close();
		} catch (Exception e) {
			try {
				ConnectionDAO.getInstance().getConnection().rollback();
			} catch (SQLException e1) {
				Logger.getGlobal().log(Level.SEVERE, e1.getMessage(), e1);
			}
			
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Notas", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}finally{
			try {
				ConnectionDAO.getInstance().getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

}
