package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditSigetWindow extends EditWindow {
	
	private final Department department;
	
	private final TextField textMinimumScore;
	private final CheckBox checkRegisterProposal;
	
	public EditSigetWindow(Department department, ListView parentView){
		super("Editar Configurações", parentView);
		
		if(department == null){
			this.department = new Department();
		}else{
			this.department = department;
		}
		
		this.textMinimumScore = new TextField("Pontuação Mínima");
		this.textMinimumScore.setWidth("100px");
		
		this.checkRegisterProposal = new CheckBox("O aluno deve registrar a proposta para TCC1");
		
		this.addField(this.textMinimumScore);
		this.addField(this.checkRegisterProposal);
		
		this.loadConfigurations();
	}
	
	private void loadConfigurations(){
		this.textMinimumScore.setValue(String.valueOf(this.department.getSigetMinimumScore()));
		this.checkRegisterProposal.setValue(this.department.isSigetRegisterProposal());
	}

	@Override
	public void save() {
		try{
			DepartmentBO bo = new DepartmentBO();
			
			this.department.setSigetMinimumScore(Double.parseDouble(this.textMinimumScore.getValue()));
			this.department.setSigetRegisterProposal(this.checkRegisterProposal.getValue());
			
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
