package br.edu.utfpr.dv.siacoes.ui.components;

import com.vaadin.flow.component.select.Select;

import br.edu.utfpr.dv.siacoes.model.Jury.JuryFormat;

public class JuryFormatComboBox extends Select<JuryFormat> {
	
	public JuryFormatComboBox(String caption) {
		this.setLabel(caption);
		this.setWidth("250px");
		this.setItemLabelGenerator(JuryFormat::getDescription);
		this.loadComboFormat();
	}
	
	public JuryFormat getFormat() {
		return this.getValue();
	}
	
	public void setFormat(JuryFormat format) {
		this.setValue(format);
	}
	
	private void loadComboFormat() {
		this.setItems(JuryFormat.values());
	}

}
