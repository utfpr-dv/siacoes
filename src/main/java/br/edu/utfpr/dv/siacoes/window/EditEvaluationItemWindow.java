package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.EvaluationItemBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditEvaluationItemWindow extends EditWindow {

	private final EvaluationItem item;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textDescription;
	private final NativeSelect comboType;
	private final TextField textPonderosity;
	private final NativeSelect comboStage;
	private final CheckBox checkActive;
	
	public EditEvaluationItemWindow(EvaluationItem item, ListView parentView){
		super("Editar Quesito", parentView);
		
		if(item == null){
			this.item = new EvaluationItem();
			this.item.setDepartment(Session.getSelectedDepartment().getDepartment());
		}else{
			this.item = item;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("400px");
		this.textDescription.setMaxLength(255);
		this.textDescription.setRequired(true);
		
		this.comboType = new NativeSelect("Avaliação");
		this.comboType.setWidth("150px");
		this.comboType.setNullSelectionAllowed(false);
		this.comboType.addItem(EvaluationItemType.WRITING);
		this.comboType.addItem(EvaluationItemType.ORAL);
		this.comboType.addItem(EvaluationItemType.ARGUMENTATION);
		this.comboType.setRequired(true);
		
		this.textPonderosity = new TextField("Peso");
		this.textPonderosity.setWidth("100px");
		this.textPonderosity.setRequired(true);
		
		this.comboStage = new NativeSelect("TCC");
		this.comboStage.setWidth("100px");
		this.comboStage.setNullSelectionAllowed(false);
		this.comboStage.addItem(1);
		this.comboStage.addItem(2);
		this.comboStage.setRequired(true);
		
		this.checkActive = new CheckBox("Ativo");
		
		this.addField(this.comboCampus);
		this.addField(this.comboDepartment);
		this.addField(this.textDescription);
		this.addField(new HorizontalLayout(this.comboType, this.textPonderosity, this.comboStage));
		this.addField(this.checkActive);
		
		this.loadEvaluationItem();
		this.textDescription.focus();
	}
	
	private void loadEvaluationItem(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.item.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.item.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textDescription.setValue(this.item.getDescription());
		this.textPonderosity.setValue(String.valueOf(this.item.getPonderosity()));
		this.comboStage.setValue(this.item.getStage());
		this.comboType.setValue(this.item.getType());
		this.checkActive.setValue(this.item.isActive());
		
		EvaluationItemBO bo = new EvaluationItemBO();
		
		if(bo.hasScores(this.item.getIdEvaluationItem())){
			this.textDescription.setEnabled(false);
			this.textPonderosity.setEnabled(false);
			this.comboStage.setEnabled(false);
			this.comboType.setEnabled(false);
		}
	}
	
	@Override
	public void save() {
		try {
			EvaluationItemBO bo = new EvaluationItemBO();
			
			if(!bo.hasScores(this.item.getIdEvaluationItem())){
				this.item.setDescription(this.textDescription.getValue());
				this.item.setPonderosity(Double.parseDouble(this.textPonderosity.getValue()));
				this.item.setStage((int)this.comboStage.getValue());
				this.item.setType((EvaluationItemType)this.comboType.getValue());
			}
			
			this.item.setActive(this.checkActive.getValue());
			
			bo.save(Session.getIdUserLog(), this.item);
			
			this.showSuccessNotification("Salvar Quesito", "Quesito salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Quesito", e.getMessage());
		}
	}

}
