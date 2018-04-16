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
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
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
			
			Notification.show("Listar Acadêmicos", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void addClick() {
		User user = new User();
		
		user.setProfiles(new ArrayList<UserProfile>());
		user.getProfiles().add(UserProfile.STUDENT);
		user.setActive(true);
		user.setExternal(false);
		user.setDepartment(Session.getUser().getDepartment());
		user.setPassword(StringUtils.generateSHA3Hash(""));
		
		UI.getCurrent().addWindow(new EditStudentWindow(user, this));
	}

	@Override
	public void editClick(Object id) {
		try {
			UserBO bo = new UserBO();
			User user = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditStudentWindow(user, this));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Editar Acadêmico", e.getMessage(), Notification.Type.ERROR_MESSAGE);
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
