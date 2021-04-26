package br.edu.utfpr.dv.siacoes.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class TokenField extends CustomField<List<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HorizontalLayout tokensLayout;
	private ComboBox<String> comboBox;
	
	private List<String> tokens;
	private List<String> predefinedTokens;
	
	public TokenField() {
		this.tokensLayout = new HorizontalLayout();
		this.comboBox = new ComboBox<String>();
		this.tokens = new ArrayList<String>();
		this.predefinedTokens = new ArrayList<String>();
		
		this.comboBox.addCustomValueSetListener(event -> comboBox.setValue(event.getDetail()));
		this.comboBox.addValueChangeListener(event -> {
		    if (event.getValue() != null) {
		    	this.addToken(event.getValue());
		    	this.comboBox.setValue(this.comboBox.getEmptyValue());
		    	this.comboBox.clear();
		    	this.setComboItems();
		    }
		});
		
		HorizontalLayout layout = new HorizontalLayout(this.tokensLayout, this.comboBox);
		
		this.add(layout);
	}
	
	public TokenField(String label) {
		this();
		
		this.setLabel(label);
	}
	
	public void addToken(String token) {
		if((token != null) && !token.trim().isEmpty() && !this.tokens.contains(token)) {
			this.tokens.add(token);
			this.buildTokenList();
			this.setComboItems();
		}
	}
	
	public void addTokens(String... tokens) {
		for(String t : tokens) {
			this.addToken(t);
		}
	}
	
	public void addTokens(List<String> tokens) {
		for(String t : tokens) {
			this.addToken(t);
		}
	}
	
	public void removeToken(String token) {
		this.tokens.remove(token);
		this.buildTokenList();
		this.setComboItems();
	}
	
	public void clearTokens() {
		this.tokens.clear();
		this.tokensLayout.removeAll();
		this.setComboItems();
	}
	
	public void setPredefinedTokens(String... tokens) {
		this.predefinedTokens = new ArrayList<String>();
		this.predefinedTokens.addAll(Arrays.asList(tokens));
		
		this.setComboItems();
	}
	
	public void setPredefinedTokens(List<String> tokens) {
		this.predefinedTokens = new ArrayList<String>();
		this.predefinedTokens.addAll(tokens);
		
		this.setComboItems();
	}
	
	private void setComboItems() {
		List<String> list = new ArrayList<String>();
		
		for(String s : this.predefinedTokens) {
			if(!this.tokens.contains(s)) {
				list.add(s);
			}
		}
		
		this.comboBox.setItems(list);
	}
	
	@Override
	protected List<String> generateModelValue() {
		return this.tokens;
	}
	
	@Override
	public List<String> getValue() {
		return this.tokens;
	}
	
	@Override
	public List<String> getEmptyValue() {
		return new ArrayList<String>();
	}

	@Override
	protected void setPresentationValue(List<String> newPresentationValue) {
		this.tokens.clear();
		
		for(String t : newPresentationValue) {
			this.addToken(t);
		}
	}
	
	private void buildTokenList() {
		this.tokensLayout.removeAll();
		
		for(String t : this.tokens) {
			Label label = new Label(t);
			
			Button button = new Button(new Icon(VaadinIcon.CLOSE));
			button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
			button.addClickListener(event -> {
				this.removeToken(t);
			});
			
			HorizontalLayout layout = new HorizontalLayout();
			layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
			layout.getStyle().set("border-radius", "15px");
			layout.getStyle().set("background", "var(--_lumo-button-background-color, var(--lumo-contrast-5pct))");
			layout.add(label, button);
			layout.setSpacing(false);
			layout.setMargin(false);
			layout.setPadding(false);
			
			this.tokensLayout.add(layout);
		}
	}
	
}
