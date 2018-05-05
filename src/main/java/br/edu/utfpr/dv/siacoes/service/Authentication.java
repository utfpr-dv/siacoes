package br.edu.utfpr.dv.siacoes.service;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Secure
@Provider
@Priority(Priorities.AUTHENTICATION)
public class Authentication implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null || !authorizationHeader.toLowerCase().startsWith("bearer ")) {
			throw new NotAuthorizedException("Needs provide authorization header");
		}
		
		String token = authorizationHeader.substring("Bearer".length()).trim();
		
		try {
			String login = new LoginService().validateToken(token);

			if((login == null) || login.trim().isEmpty()) {
				throw new NotAuthorizedException("User not found");
			}

			modifyRequestContext(requestContext, login);
		} catch (Exception e) {
			e.printStackTrace();

			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}
	
	private void modifyRequestContext(ContainerRequestContext requestContext, String login){
		final SecurityContext currentSecurityContext = requestContext.getSecurityContext();

		requestContext.setSecurityContext(new SecurityContext() {
			@Override
			public Principal getUserPrincipal() {
				return new Principal() {
					@Override
					public String getName() {
						return login;
					}
			    };
			}
			
			@Override
			public boolean isUserInRole(String role) {
			    return true;
			}

			@Override
			public boolean isSecure() {
			    return currentSecurityContext.isSecure();
			}

			@Override
			public String getAuthenticationScheme() {
			    return "Bearer";
			}
		});
	}

}
