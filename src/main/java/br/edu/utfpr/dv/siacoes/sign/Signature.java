package br.edu.utfpr.dv.siacoes.sign;

import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.User;

public class Signature {
	
	private int idSignature;
	private Document document;
	private User user;
	private byte[] signature;
	private Date signatureDate;
	private boolean revoked;
	private Date revokedDate;
	private User revokedUser;
	
	public int getIdSignature() {
		return idSignature;
	}
	public void setIdSignature(int idSignature) {
		this.idSignature = idSignature;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public byte[] getSignature() {
		return signature;
	}
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	public Date getSignatureDate() {
		return signatureDate;
	}
	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}
	public boolean isRevoked() {
		return revoked;
	}
	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}
	public Date getRevokedDate() {
		return revokedDate;
	}
	public void setRevokedDate(Date revokedDate) {
		this.revokedDate = revokedDate;
	}
	public User getRevokedUser() {
		return revokedUser;
	}
	public void setRevokedUser(User revokedUser) {
		this.revokedUser = revokedUser;
	}
	
	public Signature() {
		this.setIdSignature(0);
		this.setDocument(new Document());
		this.setUser(new User());
		this.setSignature(null);
		this.setSignatureDate(null);
		this.setRevoked(false);
		this.setRevokedDate(null);
		this.setRevokedUser(new User());
	}

}
