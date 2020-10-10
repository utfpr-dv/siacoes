package br.edu.utfpr.dv.siacoes.ui.old;

import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.edu.utfpr.dv.siacoes.ui.views.ViewFrame;

@PageTitle("Validar Declaração")
@Route(value = "#!authenticate")
public class AuthenticateView extends ViewFrame implements HasUrlParameter<String> {

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		if(parameter != null) {
			String guid = parameter;
			
			if(guid.isEmpty()) {
				this.getUI().ifPresent(ui -> ui.navigate(br.edu.utfpr.dv.siacoes.ui.views.AuthenticateView.class));
			} else {
				this.getUI().ifPresent(ui -> ui.navigate(br.edu.utfpr.dv.siacoes.ui.views.AuthenticateView.class, guid));
			}
		}
	}
	
}
