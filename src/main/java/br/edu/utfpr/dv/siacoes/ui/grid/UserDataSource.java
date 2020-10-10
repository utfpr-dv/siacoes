package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.User;

public class UserDataSource extends BasicDataSource {

	private String login;
	private String name;
	private String email;
	private String profile;
	private String institution;
	private String researchArea;
	
	public UserDataSource(User user) {
		this.setId(user.getIdUser());
		this.setName(user.getName());
		this.setLogin(user.getLogin());
		this.setEmail(user.getEmail());
		this.setProfile(user.getProfilesString());
		this.setInstitution(user.getInstitution());
		this.setResearchArea(user.getResearch());
	}
	
	public static List<UserDataSource> load(List<User> list) {
		List<UserDataSource> ret = new ArrayList<UserDataSource>();
		
		for(User user : list) {
			ret.add(new UserDataSource(user));
		}
		
		return ret;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getResearchArea() {
		return researchArea;
	}
	public void setResearchArea(String researchArea) {
		this.researchArea = researchArea;
	}
	
}
