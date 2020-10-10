package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.InternshipJuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.InternshipPosterAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiser;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class JuryAppraiserDataSource extends BasicDataSource {

	private int idUser;
	private String member;
	private String appraiser;
	private DocumentType fileType;
	private DocumentType additionalFileType;
	
	public JuryAppraiserDataSource(InternshipJuryAppraiser appraiser) {
		this.setId(appraiser.getIdInternshipJuryAppraiser());
		this.setIdUser(appraiser.getAppraiser().getIdUser());
		this.setAppraiser(appraiser.getAppraiser().getName());
		this.setFileType(appraiser.getFile() == null ? DocumentType.UNDEFINED : DocumentType.PDF);
		this.setAdditionalFileType(appraiser.getAdditionalFile() == null ? DocumentType.UNDEFINED : DocumentType.ZIP);
		
		if(appraiser.isChair()) {
			this.setMember("Presidente");
		} else if (appraiser.isSubstitute()) {
			this.setMember("Substituto");
		} else {
			this.setMember("Membro");
		}
	}
	
	public JuryAppraiserDataSource(InternshipPosterAppraiserRequest appraiser) {
		this.setId(appraiser.getIdInternshipPosterAppraiserRequest());
		this.setIdUser(appraiser.getAppraiser().getIdUser());
		this.setAppraiser(appraiser.getAppraiser().getName());
		this.setFileType(DocumentType.UNDEFINED);
		this.setAdditionalFileType(DocumentType.UNDEFINED);
		
		if (appraiser.isSubstitute()) {
			this.setMember("Substituto");
		} else {
			this.setMember("Membro");
		}
	}
	
	public JuryAppraiserDataSource(JuryAppraiser appraiser) {
		this.setId(appraiser.getIdJuryAppraiser());
		this.setIdUser(appraiser.getAppraiser().getIdUser());
		this.setAppraiser(appraiser.getAppraiser().getName());
		this.setFileType(appraiser.getFile() == null ? DocumentType.UNDEFINED : DocumentType.PDF);
		this.setAdditionalFileType(appraiser.getAdditionalFile() == null ? DocumentType.UNDEFINED : DocumentType.ZIP);
		
		if(appraiser.isChair()) {
			this.setMember("Presidente");
		} else if (appraiser.isSubstitute()) {
			this.setMember("Substituto");
		} else {
			this.setMember("Membro");
		}
	}
	
	public JuryAppraiserDataSource(JuryAppraiserRequest appraiser) {
		this.setId(appraiser.getIdJuryAppraiserRequest());
		this.setIdUser(appraiser.getAppraiser().getIdUser());
		this.setAppraiser(appraiser.getAppraiser().getName());
		this.setFileType(DocumentType.UNDEFINED);
		this.setAdditionalFileType(DocumentType.UNDEFINED);
		
		if(appraiser.isChair()) {
			this.setMember("Presidente");
		} else if (appraiser.isSubstitute()) {
			this.setMember("Substituto");
		} else {
			this.setMember("Membro");
		}
	}
	
	public static List<JuryAppraiserDataSource> loadInternshipJury(List<InternshipJuryAppraiser> list, boolean includeChair, boolean includeMember, boolean includeSubstitute) {
		List<JuryAppraiserDataSource> ret = new ArrayList<JuryAppraiserDataSource>();
		int substitute = 1, member = 1;
		
		for(InternshipJuryAppraiser appraiser : list) {
			JuryAppraiserDataSource a = new JuryAppraiserDataSource(appraiser);
			
			if(appraiser.isSubstitute()) {
				a.setMember("Substituto " + String.valueOf(substitute));
				substitute++;
			} else if(!appraiser.isChair()) {
				a.setMember("Membro " + String.valueOf(member));
				member++;
			}
			
			if((appraiser.isChair() && includeChair) || (!appraiser.isSubstitute() && includeMember) || (appraiser.isSubstitute() && includeSubstitute)) {
				ret.add(a);
			}
		}
		
		return ret;
	}
	
	public static List<JuryAppraiserDataSource> loadInternshipJury(List<InternshipJuryAppraiser> list) {
		return JuryAppraiserDataSource.loadInternshipJury(list, true, true, true);
	}
	
	public static List<JuryAppraiserDataSource> loadInternshipPosterRequest(List<InternshipPosterAppraiserRequest> list, boolean includeMember, boolean includeSubstitute) {
		List<JuryAppraiserDataSource> ret = new ArrayList<JuryAppraiserDataSource>();
		int substitute = 1, member = 1;
		
		for(InternshipPosterAppraiserRequest appraiser : list) {
			JuryAppraiserDataSource a = new JuryAppraiserDataSource(appraiser);
			
			if(appraiser.isSubstitute()) {
				a.setMember("Substituto " + String.valueOf(substitute));
				substitute++;
			} else {
				a.setMember("Membro " + String.valueOf(member));
				member++;
			}
			
			if((!appraiser.isSubstitute() && includeMember) || (appraiser.isSubstitute() && includeSubstitute)) {
				ret.add(a);
			}
		}
		
		return ret;
	}
	
	public static List<JuryAppraiserDataSource> loadInternshipPosterRequest(List<InternshipPosterAppraiserRequest> list) {
		return JuryAppraiserDataSource.loadInternshipPosterRequest(list, true, true);
	}
	
	public static List<JuryAppraiserDataSource> loadJury(List<JuryAppraiser> list) {
		List<JuryAppraiserDataSource> ret = new ArrayList<JuryAppraiserDataSource>();
		int substitute = 1, member = 1;
		
		for(JuryAppraiser appraiser : list) {
			JuryAppraiserDataSource a = new JuryAppraiserDataSource(appraiser);
			
			if(appraiser.isSubstitute()) {
				a.setMember("Substituto " + String.valueOf(substitute));
				substitute++;
			} else if(!appraiser.isChair()) {
				a.setMember("Membro " + String.valueOf(member));
				member++;
			}
			
			ret.add(a);
		}
		
		return ret;
	}
	
	public static List<JuryAppraiserDataSource> loadJuryRequest(List<JuryAppraiserRequest> list, boolean includeMember, boolean includeSubstitute) {
		List<JuryAppraiserDataSource> ret = new ArrayList<JuryAppraiserDataSource>();
		int substitute = 1, member = 1;
		
		for(JuryAppraiserRequest appraiser : list) {
			JuryAppraiserDataSource a = new JuryAppraiserDataSource(appraiser);
			
			if(appraiser.isSubstitute()) {
				a.setMember("Substituto " + String.valueOf(substitute));
				substitute++;
			} else {
				a.setMember("Membro " + String.valueOf(member));
				member++;
			}
			
			if((!appraiser.isSubstitute() && includeMember) || (appraiser.isSubstitute() && includeSubstitute)) {
				ret.add(a);
			}
		}
		
		return ret;
	}
	
	public static List<JuryAppraiserDataSource> loadJuryRequest(List<JuryAppraiserRequest> list) {
		return JuryAppraiserDataSource.loadJuryRequest(list, true, true);
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
	public String getAppraiser() {
		return appraiser;
	}
	public void setAppraiser(String appraiser) {
		this.appraiser = appraiser;
	}
	public DocumentType getFileType() {
		return fileType;
	}
	public void setFileType(DocumentType fileType) {
		this.fileType = fileType;
	}
	public DocumentType getAdditionalFileType() {
		return additionalFileType;
	}
	public void setAdditionalFileType(DocumentType additionalFileType) {
		this.additionalFileType = additionalFileType;
	}
	
}
