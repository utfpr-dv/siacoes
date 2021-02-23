package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.EvaluationItemBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditEvaluationItemWindow extends EditWindow {

	private final EvaluationItem item;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textDescription;
	private final Select<EvaluationItemType> comboType;
	private final NumberField textPonderosity;
	private final StageComboBox comboStage;
	private final Checkbox checkActive;
	
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
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("400px");
		this.textDescription.setMaxLength(255);
		this.textDescription.setRequired(true);
		
		this.comboType = new Select<EvaluationItemType>();
		this.comboType.setLabel("Avaliação");
		this.comboType.setWidth("150px");
		this.comboType.setItems(EvaluationItemType.WRITING, EvaluationItemType.ORAL, EvaluationItemType.ARGUMENTATION);
		
		this.textPonderosity = new NumberField("Peso");
		this.textPonderosity.setWidth("100px");
		
		this.comboStage = new StageComboBox();
		this.comboStage.setShowBoth(false);
		
		this.checkActive = new Checkbox("Ativo");
		
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
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textDescription.setValue(this.item.getDescription());
		this.textPonderosity.setValue(this.item.getPonderosity());
		this.comboStage.setStage(this.item.getStage());
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
				this.item.setPonderosity(this.textPonderosity.getValue());
				this.item.setStage(this.comboStage.getStage());
				this.item.setType(this.comboType.getValue());
			}
			
			this.item.setActive(this.checkActive.getValue());
			
			bo.save(Session.getIdUserLog(), this.item);
			
			this.showSuccessNotification("Salvar Quesito", "Quesito salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Quesito", e.getMessage());
		}
	}

}
