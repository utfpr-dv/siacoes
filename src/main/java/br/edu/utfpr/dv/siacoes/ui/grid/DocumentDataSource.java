package br.edu.utfpr.dv.siacoes.ui.grid;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Document;

public class DocumentDataSource extends BasicDataSource {

	private String type;
	private String name;
	
	public DocumentDataSource(Document doc) {
		this.setId(doc.getIdDocument());
		this.setType(doc.getType().name());
		this.setName(doc.getName());
	}
	
	public static List<DocumentDataSource> load(List<Document> list) {
		List<DocumentDataSource> ret = new ArrayList<DocumentDataSource>();
		
		for(Document doc : list) {
			ret.add(new DocumentDataSource(doc));
		}
		
		return ret;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
