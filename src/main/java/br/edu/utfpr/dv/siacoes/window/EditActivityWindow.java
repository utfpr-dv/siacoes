package br.edu.utfpr.dv.siacoes.window;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ActivityBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityGroupBO;
import br.edu.utfpr.dv.siacoes.bo.ActivityUnitBO;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.model.Activity;
import br.edu.utfpr.dv.siacoes.model.ActivityGroup;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditActivityWindow extends EditWindow {

	private final Activity activity;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final NativeSelect comboGroup;
	private final TextField textDescription;
	private final TextField textScore;
	private final NativeSelect comboUnit;
	private final TextField textMaximumInSemester;
	private final CheckBox checkActive;
	
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
		this.comboCampus.setRequired(true);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		this.comboDepartment.setRequired(true);
		
		this.comboGroup = new NativeSelect("Grupo");
		this.comboGroup.setWidth("800px");
		this.comboGroup.setNullSelectionAllowed(false);
		this.comboGroup.setRequired(true);
		this.loadComboGroup();
		
		this.textDescription = new TextField("Descrição");
		this.textDescription.setWidth("800px");
		this.textDescription.setMaxLength(255);
		this.textDescription.setRequired(true);
		
		this.textScore = new TextField("Pontuação");
		this.textScore.setWidth("100px");
		this.textScore.setRequired(true);
		
		this.comboUnit = new NativeSelect("Unidade");
		this.comboUnit.setWidth("400px");
		this.comboUnit.setNullSelectionAllowed(false);
		this.comboUnit.setRequired(true);
		this.loadComboUnit();
		
		this.textMaximumInSemester = new TextField("Máximo de Pontos/Semestre");
		this.textMaximumInSemester.setWidth("200px");
		
		this.checkActive = new CheckBox("Ativo");
		
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
			
			this.comboGroup.removeAllItems();
			this.comboGroup.addItems(list);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Atividade", e.getMessage());
		}
	}
	
	private void loadComboUnit(){
		try{
			ActivityUnitBO bo = new ActivityUnitBO();
			List<ActivityUnit> list = bo.listAll();
			
			this.comboUnit.removeAllItems();
			this.comboUnit.addItems(list);
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
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
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.comboGroup.select(this.activity.getGroup());
		this.textDescription.setValue(this.activity.getDescription());
		this.textScore.setValue(String.valueOf(this.activity.getScore()));
		this.comboUnit.select(this.activity.getUnit());
		this.textMaximumInSemester.setValue(String.valueOf(this.activity.getMaximumInSemester()));
		this.checkActive.setValue(this.activity.isActive());
	}

	@Override
	public void save() {
		try{
			ActivityBO bo = new ActivityBO();
			
			this.activity.setGroup((ActivityGroup)this.comboGroup.getValue());
			this.activity.setDescription(this.textDescription.getValue());
			this.activity.setScore(Double.parseDouble(this.textScore.getValue()));
			this.activity.setUnit((ActivityUnit)this.comboUnit.getValue());
			this.activity.setMaximumInSemester(Double.parseDouble(this.textMaximumInSemester.getValue()));
			this.activity.setActive(this.checkActive.getValue());
			
			bo.save(Session.getIdUserLog(), this.activity);
			
			this.showSuccessNotification("Salvar Atividade", "Atividade salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Atividade", e.getMessage());
		}
	}
	
}
