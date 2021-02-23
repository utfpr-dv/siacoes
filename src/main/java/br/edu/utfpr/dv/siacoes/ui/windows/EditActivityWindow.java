package br.edu.utfpr.dv.siacoes.ui.windows;

import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.Activity;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.ui.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.ui.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.ui.views.ListView;

public class EditActivityWindow extends EditWindow {

	private final Activity activity;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final Select<ActivityGroup> comboGroup;
	private final TextField textDescription;
	private final TextField textScore;
	private final Select<ActivityUnit> comboUnit;
	private final TextField textMaximumInSemester;
	private final Checkbox checkActive;
	
	public EditActivityWindow(Activity activity, ListView parentView){
		super("Editar Atividade", parentView);
		
		if(activity == null){
			this.activity = new Activity();
			this.activity.setDepartment(Session.getSelectedDepartment().getDepartment());
		}else{
			this.activity = activity;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.comboGroup = new Select<ActivityGroup>();
		this.comboGroup.setLabel("Grupo");
		this.comboGroup.setWidth("810px");
		this.loadComboGroup();
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("810px");
		this.textDescription.setMaxLength(255);
		this.textDescription.setRequired(true);
		
		this.textScore = new TextField("Pontuação");
		this.textScore.setWidth("100px");
		this.textScore.setRequired(true);
		
		this.comboUnit = new Select<ActivityUnit>();
		this.comboUnit.setLabel("Unidade");
		this.comboUnit.setWidth("400px");
		this.loadComboUnit();
		
		this.textMaximumInSemester = new TextField("Máximo de Pontos/Semestre");
		this.textMaximumInSemester.setWidth("200px");
		
		this.checkActive = new Checkbox("Ativo");
		
		this.addField(new HorizontalLayout(this.comboCampus, this.comboDepartment));
		this.addField(this.comboGroup);
		this.addField(this.textDescription);
		this.addField(new HorizontalLayout(this.textScore, this.comboUnit, this.textMaximumInSemester));
		this.addField(this.checkActive);
		
		this.loadActivity();
		this.comboGroup.focus();
	}
	
	private void loadComboGroup(){
		try{
			ActivityGroupBO bo = new ActivityGroupBO();
			List<ActivityGroup> list = bo.listAll();
			
			this.comboGroup.setItems(list);
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Atividade", e.getMessage());
		}
	}
	
	private void loadComboUnit(){
		try{
			ActivityUnitBO bo = new ActivityUnitBO();
			List<ActivityUnit> list = bo.listAll();
			
			this.comboUnit.setItems(list);
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Atividade", e.getMessage());
		}
	}
	
	private void loadActivity(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.activity.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.activity.getDepartment());
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.comboGroup.setValue(this.activity.getGroup());
		this.textDescription.setValue(this.activity.getDescription());
		this.textScore.setValue(String.valueOf(this.activity.getScore()));
		this.comboUnit.setValue(this.activity.getUnit());
		this.textMaximumInSemester.setValue(String.valueOf(this.activity.getMaximumInSemester()));
		this.checkActive.setValue(this.activity.isActive());
	}

	@Override
	public void save() {
		try{
			ActivityBO bo = new ActivityBO();
			
			this.activity.setGroup(this.comboGroup.getValue());
			this.activity.setDescription(this.textDescription.getValue());
			this.activity.setScore(Double.parseDouble(this.textScore.getValue()));
			this.activity.setUnit(this.comboUnit.getValue());
			this.activity.setMaximumInSemester(Double.parseDouble(this.textMaximumInSemester.getValue()));
			this.activity.setActive(this.checkActive.getValue());
			
			bo.save(Session.getIdUserLog(), this.activity);
			
			this.showSuccessNotification("Salvar Atividade", "Atividade salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Atividade", e.getMessage());
		}
	}
	
}
