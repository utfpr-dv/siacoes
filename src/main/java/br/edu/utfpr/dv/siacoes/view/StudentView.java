package br.edu.utfpr.dv.siacoes.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.util.StringUtils;
import br.edu.utfpr.dv.siacoes.window.EditStudentWindow;

public class StudentView extends ListView {
	
	public static final String NAME = "student";
	
	private final TextField textName;
	
	public StudentView(){
		super(SystemModule.SIGES);
		
		this.setCaption("Acadêmicos");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.textName = new TextField("Nome:");
		this.textName.setWidth("400px");
		
		this.addFilterField(this.textName);
		
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Nome", String.class);
		this.getGrid().addColumn("R.A.", String.class);
		
		try {
			UserBO bo = new UserBO();
	    	List<User> list = bo.list(this.textName.getValue(), UserProfile.STUDENT.getValue(), true, false);
	    	
	    	for(User u : list){
				Object itemId = this.getGrid().addRow(u.getName(), u.getStudentCode());
				this.addRowId(itemId, u.getIdUser());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acadêmicos", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		User user = new User();
		
		user.setProfiles(new ArrayList<UserProfile>());
		user.getProfiles().add(UserProfile.STUDENT);
		user.setActive(true);
		user.setExternal(false);
		user.setPassword(StringUtils.generateSHA3Hash(""));
		
		UserDepartment department = new UserDepartment();
		
		department.setDepartment(Session.getSelectedDepartment().getDepartment());
		department.setProfile(UserProfile.STUDENT);
		
		UI.getCurrent().addWindow(new EditStudentWindow(user, department, this));
	}

	@Override
	public void editClick(Object id) {
		try {
			User user = new UserBO().findById((int)id);
			UserDepartment department = new UserDepartmentBO().find(user.getIdUser(), UserProfile.STUDENT, Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			UI.getCurrent().addWindow(new EditStudentWindow(user, department, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Acadêmico", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
