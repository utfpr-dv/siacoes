package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditStudentWindow extends EditWindow {
	
	private final User user;
	
	private final TextField textName;
	private final TextField textStudentCode;
	
	public EditStudentWindow(User user, ListView parentView){
		super("Editar Acadêmico", parentView);
		
		if(user == null){
			this.user = new User();
		}else{
			this.user = user;
		}
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("410px");
		this.textName.setMaxLength(100);
		
		this.textStudentCode = new TextField("R.A.");
		this.textStudentCode.setWidth("200px");
		this.textStudentCode.setMaxLength(45);
		
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
			
			bo.save(user);
			
			Notification.show("Salvar Acadêmico", "Acadêmico salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Acadêmico", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}
