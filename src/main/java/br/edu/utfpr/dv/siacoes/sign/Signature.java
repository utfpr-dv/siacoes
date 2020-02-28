package br.edu.utfpr.dv.siacoes.sign;

import java.util.Date;

import br.edu.utfpr.dv.siacoes.model.User;

public class Signature {
	
	public enum SignatureStatus{
		NONE(0), VALID(1), INVALID(2), REVOKED(3);
		
		private final int value; 
		SignatureStatus(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static SignatureStatus valueOf(int value){
			for(SignatureStatus p : SignatureStatus.values()){
				if(p.getValue() == value){
					return p;
				}
			}
			
			return null;
		}
		
		public String toString(){
			switch(this){
				case NONE:
					return "Nenhum";
				case VALID:
					return "Autenticada";
				case INVALID:
					return "Inválida";
				case REVOKED:
					return "Revogada";
				default:
					return "Nenhum";
			}
		}
	}
	
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
	public SignatureStatus getStatus() {
		if (this.isRevoked()) {
			return SignatureStatus.REVOKED;
		} else if((this.getDocument() == null) || (this.getDocument().getIdDocument() == 0) || (this.getSignature() == null)) {
			return SignatureStatus.NONE;
		} else {
			try {
				if(SignatureKey.verify(this.getUser().getLogin(), this.getDocument().getDataset(), this.getSignature())) {
					return SignatureStatus.VALID;
				} else {
					return SignatureStatus.INVALID;
				}
			} catch(Exception e) {
				return SignatureStatus.INVALID;
			}
		}
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
