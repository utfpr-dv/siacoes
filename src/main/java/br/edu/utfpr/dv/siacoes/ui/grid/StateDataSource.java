package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.State;

public class StateDataSource extends BasicDataSource {

	private String name;
	private String country;
	private String initials;
	
	public StateDataSource(State state) {
		this.setId(state.getIdState());
		this.setName(state.getName());
		this.setCountry(state.getCountry().getName());
		this.setInitials(state.getInitials());
	}
	
	public static List<StateDataSource> load(List<State> list) {
		List<StateDataSource> ret = new ArrayList<StateDataSource>();
		
		for(State state : list) {
			ret.add(new StateDataSource(state));
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}
	
}
