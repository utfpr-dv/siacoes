package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.User;

public class CompanySupervisorDataSource extends BasicDataSource {

	private String name;
	private String company;
	private String phone;
	private String email;
	
	public CompanySupervisorDataSource(User user) {
		this.setId(user.getIdUser());
		this.setName(user.getName());
		this.setCompany(user.getCompany().getName());
		this.setPhone(user.getPhone());
		this.setEmail(user.getEmail());
	}
	
	public static List<CompanySupervisorDataSource> load(List<User> list) {
		List<CompanySupervisorDataSource> ret = new ArrayList<CompanySupervisorDataSource>();
		
		for(User user : list) {
			ret.add(new CompanySupervisorDataSource(user));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
