package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CityBO;
import br.edu.utfpr.dv.siacoes.components.CountryComboBox;
import br.edu.utfpr.dv.siacoes.components.StateComboBox;
import br.edu.utfpr.dv.siacoes.model.City;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditCityWindow extends EditWindow {
	
	private final City city;
	
	private final CountryComboBox comboCountry;
	private final StateComboBox comboState;
	private final TextField textName;
	
	public EditCityWindow(City city, ListView parentView){
		super("Editar Cidade", parentView);
		
		if(city == null){
			this.city = new City();
		}else{
			this.city = city;
		}
		
		this.comboState = new StateComboBox();
		this.comboState.setRequired(true);
		
		this.comboCountry = new CountryComboBox();
		this.comboCountry.setRequired(true);
		this.comboCountry.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(comboCountry.getCountry() == null){
					comboState.setIdCountry(0);
				}else{
					comboState.setIdCountry(comboCountry.getCountry().getIdCountry());	
				}
			}
		});
		
		this.textName = new TextField("Cidade");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
		this.addField(this.comboCountry);
		this.addField(this.comboState);
		this.addField(this.textName);
		
		this.loadCity();
		this.comboCountry.focus();
	}
	
	private void loadCity(){
		this.comboCountry.setCountry(this.city.getState().getCountry());
		this.comboState.setIdCountry(this.city.getState().getCountry().getIdCountry());
		this.comboState.setState(this.city.getState());
		this.textName.setValue(this.city.getName());
	}

	@Override
	public void save() {
		try{
			CityBO bo = new CityBO();
			
			this.city.setState(this.comboState.getStateValue());
			this.city.setName(this.textName.getValue());
			
			bo.save(Session.getIdUserLog(), this.city);
			
			this.showSuccessNotification("Salvar Cidade", "Cidade salva com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Cidade", e.getMessage());
		}
	}

}
