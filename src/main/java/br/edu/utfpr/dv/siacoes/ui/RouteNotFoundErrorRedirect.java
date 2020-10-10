package br.edu.utfpr.dv.siacoes.ui;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;

public class RouteNotFoundErrorRedirect extends RouteNotFoundError {

	@Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        event.rerouteTo("404");
		
        return HttpServletResponse.SC_NOT_FOUND;
    }
	
}
