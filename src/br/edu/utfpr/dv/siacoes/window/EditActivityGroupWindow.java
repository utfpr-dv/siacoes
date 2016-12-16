package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditActivityGroupWindow extends EditWindow {

	private final ActivityGroup group;
	
	private final TextField textDescription;
	private final TextField textMinimumScore;
	private final TextField textMaximumScore;
	
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
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.textMaximumScore = new TextField("Pontuação Máxima");
		this.textMaximumScore.setWidth("100px");
		
		this.addField(this.textDescription);
		this.addField(new HorizontalLayout(this.textMinimumScore, this.textMaximumScore));
		
		this.loadGroup();
		this.textDescription.focus();
	}
	
	private void loadGroup(){
		this.textDescription.setValue(this.group.getDescription());
		this.textMinimumScore.setValue(String.valueOf(this.group.getMinimumScore()));
		this.textMaximumScore.setValue(String.valueOf(this.group.getMaximumScore()));
	}
	
	@Override
	public void save() {
		try {
			ActivityGroupBO bo = new ActivityGroupBO();
			
			this.group.setDescription(this.textDescription.getValue());
			this.group.setMinimumScore(Integer.parseInt(this.textMinimumScore.getValue()));
			this.group.setMaximumScore(Integer.parseInt(this.textMaximumScore.getValue()));
			
			bo.save(this.group);
			
			Notification.show("Salvar Grupo", "Grupo salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Grupo", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
