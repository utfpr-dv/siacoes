package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Country;

public class CountryDataSource extends BasicDataSource {

	private String name;

	public CountryDataSource(Country country) {
		this.setId(country.getIdCountry());
		this.setName(country.getName());
	}
	
	public static List<CountryDataSource> load(List<Country> list) {
		List<CountryDataSource> ret = new ArrayList<CountryDataSource>();
		
		for(Country country : list) {
			ret.add(new CountryDataSource(country));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
