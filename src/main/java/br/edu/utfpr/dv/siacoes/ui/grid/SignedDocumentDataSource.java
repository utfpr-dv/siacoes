package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.sign.Document;
import br.edu.utfpr.dv.siacoes.sign.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.sign.Signature;
import br.edu.utfpr.dv.siacoes.sign.Signature.SignatureStatus;

public class SignedDocumentDataSource {
	
	private int idDocument;
	private DocumentType type;
	private Date generatedDate;
	private Date signatureDate;
	private SignatureStatus status;
	
	public SignedDocumentDataSource(Document doc, Signature sign) {
		this.setIdDocument(doc.getIdDocument());
		this.setType(doc.getType());
		this.setGeneratedDate(doc.getGeneratedDate());
		this.setSignatureDate(sign.getSignatureDate());
		this.setStatus(sign.getStatus());
	}
	
	public static List<SignedDocumentDataSource> load(List<Document> list) {
		List<SignedDocumentDataSource> ret = new ArrayList<SignedDocumentDataSource>();
		
		for(Document doc : list) {
    		for(Signature sign : doc.getSignatures()) {
    			if(sign.getUser().getIdUser() == Session.getUser().getIdUser()) {
    				ret.add(new SignedDocumentDataSource(doc, sign));
    			}
    		}
    	}
		
		return ret;
	}
	
	public int getIdDocument() {
		return idDocument;
	}
	public void setIdDocument(int idDocument) {
		this.idDocument = idDocument;
	}
	public DocumentType getType() {
		return type;
	}
	public void setType(DocumentType type) {
		this.type = type;
	}
	public Date getGeneratedDate() {
		return generatedDate;
	}
	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}
	public Date getSignatureDate() {
		return signatureDate;
	}
	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}
	public SignatureStatus getStatus() {
		return status;
	}
	public void setStatus(SignatureStatus status) {
		this.status = status;
	}

}
