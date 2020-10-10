package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.InternshipEvaluationItemBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;
import br.edu.utfpr.dv.siacoes.model.InternshipEvaluationItem;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditInternshipEvaluationItemWindow extends EditWindow {
	
	private final InternshipEvaluationItem item;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textDescription;
	private final Select<EvaluationItemType> comboType;
	private final TextField textPonderosity;
	private final Checkbox checkActive;
	
	public EditInternshipEvaluationItemWindow(InternshipEvaluationItem item, ListView parentView){
		super("Editar Quesito", parentView);
		
		if(item == null){
			this.item = new InternshipEvaluationItem();
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
		
		this.textPonderosity = new TextField("Peso");
		this.textPonderosity.setWidth("100px");
		this.textPonderosity.setRequired(true);
		
		this.checkActive = new Checkbox("Ativo");
		
		this.addField(this.comboCampus);
		this.addField(this.comboDepartment);
		this.addField(this.textDescription);
		this.addField(new HorizontalLayout(this.comboType, this.textPonderosity));
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
		this.textPonderosity.setValue(String.valueOf(this.item.getPonderosity()));
		this.comboType.setValue(this.item.getType());
		this.checkActive.setValue(this.item.isActive());
		
		InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
		
		if(bo.hasScores(this.item.getIdInternshipEvaluationItem())){
			this.textDescription.setEnabled(false);
			this.textPonderosity.setEnabled(false);
			this.comboType.setEnabled(false);
		}
	}
	
	@Override
	public void save() {
		try {
			InternshipEvaluationItemBO bo = new InternshipEvaluationItemBO();
			
			if(!bo.hasScores(this.item.getIdInternshipEvaluationItem())){
				this.item.setDescription(this.textDescription.getValue());
				this.item.setPonderosity(Double.parseDouble(this.textPonderosity.getValue()));
				this.item.setType((EvaluationItemType)this.comboType.getValue());
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
