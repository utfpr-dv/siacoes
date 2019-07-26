package br.edu.utfpr.dv.siacoes.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.edu.utfpr.dv.siacoes.bo.UserBO;
import br.edu.utfpr.dv.siacoes.log.LoginEvent;
import br.edu.utfpr.dv.siacoes.model.AppConfig;
import br.edu.utfpr.dv.siacoes.model.Credential;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

@Path("/login")
public class LoginService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validateLogin(@Context HttpServletRequest requestContext, Credential credentials) {
		if(!AppConfig.getInstance().isMobileEnabled()) {
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}
		
		try {
			UserBO bo = new UserBO();
			User user = bo.validateLogin(credentials);
			
			String token = this.generateToken(user.getLogin());
			
			LoginEvent.registerLogin(user.getIdUser(), requestContext.getRemoteAddr(), credentials.getDevice());
			
			return Response.ok(token).build();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);

			return Response.status(Status.UNAUTHORIZED).build();
		}
	}
	
	private static String secret;
	
	public synchronized String getSecret() {
		if((LoginService.secret == null) || LoginService.secret.isEmpty()) {
			LoginService.secret = UUID.randomUUID().toString();
		}
		
		return LoginService.secret;
	}
	
	public String generateToken(String login) throws IllegalArgumentException, UnsupportedEncodingException {
		Date now = new Date();
		Date expires = DateUtils.addMinute(now, 30);
		Algorithm algorithm = Algorithm.HMAC256(this.getSecret());

		return JWT.create().withIssuedAt(now).withIssuer(login).withExpiresAt(expires).sign(algorithm);
	}
	
	public String validateToken(String token) throws Exception {
		Date now = new Date();
		Algorithm algorithm = Algorithm.HMAC256(this.getSecret());
	    JWTVerifier verifier = JWT.require(algorithm).build();
	    DecodedJWT decodedToken = verifier.verify(token);
		
		if(decodedToken.getIssuedAt().before(now) && decodedToken.getExpiresAt().after(now)) {
			String login = decodedToken.getIssuer();
			
			return login;
		} else {
			return null;
		}
	}
	
	public User getUser(SecurityContext securityContext) throws Exception {
		return new UserBO().findByLogin(securityContext.getUserPrincipal().getName());
	}

}
