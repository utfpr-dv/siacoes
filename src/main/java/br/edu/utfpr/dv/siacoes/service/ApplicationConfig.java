package br.edu.utfpr.dv.siacoes.service;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/service")
public class ApplicationConfig extends Application {

	@Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("jersey.config.server.provider.packages", "br.edu.utfpr.dv.siacoes.service");
        return properties;
    }
	
}
