package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditStudentWindow extends EditWindow {
	
	private final User user;
	private final UserDepartment department;
	
	private final TextField textName;
	private final TextField textStudentCode;
	
	public EditStudentWindow(User user, UserDepartment department, ListView parentView) {
		super("Editar Acadêmico", parentView);
		
		if(user == null) {
			this.user = new User();
		} else {
			this.user = user;
		}
		if(department == null) {
			this.department = new UserDepartment();
			this.department.setDepartment(Session.getSelectedDepartment().getDepartment());
			this.department.setProfile(UserProfile.STUDENT);
		} else {
			this.department = department;
		}
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("410px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
		this.textStudentCode = new TextField("R.A.");
		this.textStudentCode.setWidth("200px");
		this.textStudentCode.setMaxLength(45);
		this.textStudentCode.setRequired(true);
		
		this.addField(this.textName);
		this.addField(this.textStudentCode);
		
		this.loadStudent();
		this.textName.focus();
	}
	
	private void loadStudent(){
		this.textName.setValue(this.user.getName());
		this.textStudentCode.setValue(this.user.getStudentCode());
	}

	@Override
	public void save() {
		try{
			UserBO bo = new UserBO();
			
			this.user.setName(this.textName.getValue());
			this.user.setStudentCode(this.textStudentCode.getValue());
			
			if(this.user.getLogin().isEmpty()){
				this.user.setLogin(bo.formatLoginFromStudentCode(this.user.getStudentCode()));
			}
			
			bo.save(Session.getIdUserLog(), this.user);
			
			this.department.setUser(this.user);
			
			new UserDepartmentBO().save(Session.getIdUserLog(), this.department);
			
			this.showSuccessNotification("Salvar Acadêmico", "Acadêmico salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Acadêmico", e.getMessage());
		}
	}

}
