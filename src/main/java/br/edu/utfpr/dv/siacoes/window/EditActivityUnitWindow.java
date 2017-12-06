package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditActivityUnitWindow extends EditWindow {
	
	private final ActivityUnit unit;
	
	private final TextField textDescription;
	private final CheckBox checkFillAmount;
	
	public EditActivityUnitWindow(ActivityUnit unit, ListView parentView){
		super("Editar Unidade", parentView);
		
		if(unit == null){
			this.unit = new ActivityUnit();
		}else{
			this.unit = unit;
		}
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("400px");
		this.textDescription.setMaxLength(50);
		
		this.checkFillAmount = new CheckBox("Preencher quantidade");
		
		this.addField(this.textDescription);
		this.addField(this.checkFillAmount);
		
		this.loadUnit();
		this.textDescription.focus();
	}
	
	private void loadUnit(){
		this.textDescription.setValue(this.unit.getDescription());
		this.checkFillAmount.setValue(this.unit.isFillAmount());
	}

	@Override
	public void save() {
		try {
			ActivityUnitBO bo = new ActivityUnitBO();
			
			this.unit.setDescription(this.textDescription.getValue());
			this.unit.setFillAmount(this.checkFillAmount.getValue());
			
			bo.save(this.unit);
			
			Notification.show("Salvar Unidade", "Unidade salva com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Unidade", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
