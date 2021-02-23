package br.edu.utfpr.dv.siacoes.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.bo.UserDepartmentBO;
import br.edu.utfpr.dv.siacoes.log.Logger;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.ui.MainLayout;
import br.edu.utfpr.dv.siacoes.ui.grid.StudentDataSource;
import br.edu.utfpr.dv.siacoes.ui.windows.EditStudentWindow;
import br.edu.utfpr.dv.siacoes.model.UserDepartment;
import br.edu.utfpr.dv.siacoes.util.StringUtils;

@PageTitle("Acadêmicos")
@Route(value = "student", layout = MainLayout.class)
public class StudentView extends ListView<StudentDataSource> {
	
	private final TextField textName;
	
	public StudentView(){
		super(SystemModule.SIGES);
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.getGrid().addColumn(StudentDataSource::getName, "Name").setHeader("Nome");
		this.getGrid().addColumn(StudentDataSource::getStudentCode).setHeader("R.A.");
		
		this.textName = new TextField("Nome:");
		this.textName.setWidth("400px");
		
		this.addFilterField(this.textName);
		
		this.setDeleteVisible(false);
	}

	@Override
	protected void loadGrid() {
		try {
			UserBO bo = new UserBO();
	    	List<User> list = bo.list(this.textName.getValue(), UserProfile.STUDENT.getValue(), true, false);
	    	
	    	this.getGrid().setItems(StudentDataSource.load(list));
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
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
		
		EditStudentWindow window = new EditStudentWindow(user, department, this);
		window.open();
	}

	@Override
	public void editClick(int id) {
		try {
			User user = new UserBO().findById((int)id);
			UserDepartment department = new UserDepartmentBO().find(user.getIdUser(), UserProfile.STUDENT, Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			EditStudentWindow window = new EditStudentWindow(user, department, this);
			window.open();
		} catch (Exception e) {
			Logger.log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Acadêmico", e.getMessage());
		}
	}

	@Override
	public void deleteClick(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
