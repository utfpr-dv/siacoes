package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigacWindow extends EditWindow {
	
	private final Department department;
	
	private final TextField textMinimumScore;
	
	public EditSigacWindow(Department department, ListView parentView){
		super("Configurações SIGAC", parentView);
		
		if(department == null){
			this.department = new Department();
		}else{
			this.department = department;
		}
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.addField(this.textMinimumScore);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.valueOf(this.department.getSigacMinimumScore()));
	}

	@Override
	public void save() {
		try{
			DepartmentBO bo = new DepartmentBO();
			
			this.department.setSigacMinimumScore(Double.parseDouble(this.textMinimumScore.getValue()));
			
			bo.save(this.department);
			
			Notification.show("Salvar Configurações", "Configurações salvas com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Configurações", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
