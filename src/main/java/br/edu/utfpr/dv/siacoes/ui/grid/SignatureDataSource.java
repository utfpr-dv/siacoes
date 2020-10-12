package br.edu.utfpr.dv.siacoes.ui.grid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.sign.Signature;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class SignatureDataSource extends BasicDataSource {

	private String name;
	private LocalDateTime date;
	private String status;
	
	public SignatureDataSource(Signature sign) {
		this.setId(sign.getIdSignature());
		this.setName(sign.getUser().getName());
		this.setDate(DateUtils.convertToLocalDateTime(sign.getSignatureDate()));
		this.setStatus(sign.getStatus().toString());
	}
	
	public static List<SignatureDataSource> load(List<Signature> list) {
		return SignatureDataSource.load(list, false);
	}
	
	public static List<SignatureDataSource> load(List<Signature> list, boolean onlySigned) {
		List<SignatureDataSource> ret = new ArrayList<SignatureDataSource>();
		
		for(Signature sign : list) {
			if((sign.getSignature() != null) || !onlySigned) {
				ret.add(new SignatureDataSource(sign));
			}
		}
		
		return ret;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
