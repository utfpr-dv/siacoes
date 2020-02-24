package br.edu.utfpr.dv.siacoes.sign;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignDataset implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String guid;
	private String validateUrl;
	private String legalText;
	private InputStream qrCode;
	private boolean useDigitalSignature;
	private List<Signature> signatures;

	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getValidateUrl() {
		return validateUrl;
	}
	public void setValidateUrl(String validateUrl) {
		this.validateUrl = validateUrl;
	}
	public InputStream getQrCode() {
		return qrCode;
	}
	public void setQrCode(InputStream qrCode) {
		this.qrCode = qrCode;
	}
	public List<Signature> getSignatures() {
		return signatures;
	}
	public void setSignatures(List<Signature> signatures) {
		this.signatures = signatures;
	}
	public boolean isUseDigitalSignature() {
		return useDigitalSignature;
	}
	public void setUseDigitalSignature(boolean useDigitalSignature) {
		this.useDigitalSignature = useDigitalSignature;
	}
	public String getLegalText() {
		return legalText;
	}
	public void setLegalText(String legalText) {
		this.legalText = legalText;
	}
	
	public SignDataset() {
		this.setGuid(UUID.randomUUID().toString());
		this.setValidateUrl("");
		this.setLegalText("");
		this.setQrCode(null);
		this.setUseDigitalSignature(false);
		this.setSignatures(new ArrayList<SignDataset.Signature>());
	}
	
	public void addSignature(int idUser, String name) {
		this.getSignatures().add(new SignDataset.Signature(idUser, name));
	}
	
	public class Signature implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private int idUser;
		private String name;
		private InputStream signature;
		private InputStream rubric;
		
		public int getIdUser() {
			return idUser;
		}
		public void setIdUser(int idUser) {
			this.idUser = idUser;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public InputStream getSignature() {
			return signature;
		}
		public void setSignature(InputStream signature) {
			this.signature = signature;
		}
		public InputStream getRubric() {
			return rubric;
		}
		public void setRubric(InputStream rubric) {
			this.rubric = rubric;
		}
		
		public Signature() {
			this.setIdUser(0);
			this.setName("");
			this.setSignature(null);
			this.setRubric(null);
		}
		
		public Signature(int idUser, String name) {
			this.setIdUser(idUser);
			this.setName(name);
			this.setSignature(null);
			this.setRubric(null);
		}
		
	}

}
