package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditActivityUnitWindow extends EditWindow {
	
	private final ActivityUnit unit;
	
	private final TextField textDescription;
	private final CheckBox checkFillAmount;
	private final TextField textAmountDescription;
	
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
		this.textDescription.setRequired(true);
		
		this.checkFillAmount = new CheckBox("Preencher Quantidade");
		
		this.textAmountDescription = new TextField("Descrição da Quantidade");
		this.textAmountDescription.setWidth("400px");
		this.textAmountDescription.setMaxLength(50);
		
		this.addField(this.textDescription);
		this.addField(this.checkFillAmount);
		this.addField(this.textAmountDescription);
		
		this.loadUnit();
		this.textDescription.focus();
	}
	
	private void loadUnit(){
		this.textDescription.setValue(this.unit.getDescription());
		this.checkFillAmount.setValue(this.unit.isFillAmount());
		this.textAmountDescription.setValue(this.unit.getAmountDescription());
	}

	@Override
	public void save() {
		try {
			ActivityUnitBO bo = new ActivityUnitBO();
			
			this.unit.setDescription(this.textDescription.getValue());
			this.unit.setFillAmount(this.checkFillAmount.getValue());
			this.unit.setAmountDescription(this.textAmountDescription.getValue());
			
			bo.save(Session.getIdUserLog(), this.unit);
			
			this.showSuccessNotification("Salvar Unidade", "Unidade salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Unidade", e.getMessage());
		}
	}

}
