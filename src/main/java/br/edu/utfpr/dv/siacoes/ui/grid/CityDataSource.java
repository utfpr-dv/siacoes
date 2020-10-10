package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.City;

public class CityDataSource extends BasicDataSource {

	private String name;
	private String state;
	private String country;
	
	public CityDataSource(City city) {
		this.setId(city.getIdCity());
		this.setName(city.getName());
		this.setState(city.getState().getInitials());
		this.setCountry(city.getState().getCountry().getName());
	}
	
	public static List<CityDataSource> load(List<City> list) {
		List<CityDataSource> ret = new ArrayList<CityDataSource>();
		
		for(City city : list) {
			ret.add(new CityDataSource(city));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
}
