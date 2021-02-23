package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditActivityGroupWindow extends EditWindow {

	private final ActivityGroup group;
	
	private final TextField textDescription;
	private final IntegerField textMinimumScore;
	private final IntegerField textMaximumScore;
	
	public EditActivityGroupWindow(ActivityGroup group, ListView parentView){
		super("Editar Grupo", parentView);
		
		if(group == null){
			this.group = new ActivityGroup();
		}else{
			this.group = group;
		}
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("600px");
		this.textDescription.setMaxLength(255);
		this.textDescription.setRequired(true);
		
		this.textMinimumScore = new IntegerField("Pontuação Mínima");
		this.textMinimumScore.setWidth("150px");
		this.textMinimumScore.setRequiredIndicatorVisible(true);
		
		this.textMaximumScore = new IntegerField("Pontuação Máxima");
		this.textMaximumScore.setWidth("150px");
		this.textMaximumScore.setRequiredIndicatorVisible(true);
		
		this.addField(this.textDescription);
		this.addField(new HorizontalLayout(this.textMinimumScore, this.textMaximumScore));
		
		this.loadGroup();
		this.textDescription.focus();
	}
	
	private void loadGroup(){
		this.textDescription.setValue(this.group.getDescription());
		this.textMinimumScore.setValue(this.group.getMinimumScore());
		this.textMaximumScore.setValue(this.group.getMaximumScore());
	}
	
	@Override
	public void save() {
		try {
			ActivityGroupBO bo = new ActivityGroupBO();
			
			this.group.setDescription(this.textDescription.getValue());
			this.group.setMinimumScore(this.textMinimumScore.getValue());
			this.group.setMaximumScore(this.textMaximumScore.getValue());
			
			bo.save(Session.getIdUserLog(), this.group);
			
			this.showSuccessNotification("Salvar Grupo", "Grupo salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Grupo", e.getMessage());
		}
	}

}
