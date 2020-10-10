package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Company;

public class CompanyDataSource extends BasicDataSource {

	private String name;
	private String city;
	private String phone;
	private String email;
	
	public CompanyDataSource(Company company) {
		this.setId(company.getIdCompany());
		this.setName(company.getName());
		this.setCity(company.getCity().getName());
		this.setPhone(company.getPhone());
		this.setEmail(company.getEmail());
	}
	
	public static List<CompanyDataSource> load(List<Company> list) {
		List<CompanyDataSource> ret = new ArrayList<CompanyDataSource>();
		
		for(Company company : list) {
			ret.add(new CompanyDataSource(company));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
