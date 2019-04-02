package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CampusBO;
import br.edu.utfpr.dv.siacoes.bo.DocumentBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.DepartmentComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Document;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditDocumentWindow extends EditWindow {

	private final Document document;
	
	private final CampusComboBox comboCampus;
	private final DepartmentComboBox comboDepartment;
	private final TextField textName;
	private final FileUploader uploadFile;
	
	public EditDocumentWindow(Document document, ListView parentView){
		super("Editar Documento", parentView);
		
		if(document == null){
			this.document = new Document();
			this.document.setDepartment(Session.getSelectedDepartment().getDepartment());
		}else{
			this.document = document;	
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setEnabled(false);
		
		this.comboDepartment = new DepartmentComboBox(0);
		this.comboDepartment.setEnabled(false);
		
		this.textName = new TextField("Nome", "");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
		this.uploadFile = new FileUploader("");
		
		this.addField(this.comboCampus);
		this.addField(this.comboDepartment);
		this.addField(this.textName);
		this.addField(this.uploadFile);
		
		this.loadDocument();
		this.textName.focus();
	}
	
	public void loadDocument(){
		try{
			CampusBO bo = new CampusBO();
			Campus campus = bo.findByDepartment(this.document.getDepartment().getIdDepartment());
			
			this.comboCampus.setCampus(campus);
			
			this.comboDepartment.setIdCampus(campus.getIdCampus());
			
			this.comboDepartment.setDepartment(this.document.getDepartment());
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
		}
		
		this.textName.setValue(this.document.getName());
	}

	@Override
	public void save() {
		DocumentBO bo = new DocumentBO();
		
		try {
			this.document.setName(this.textName.getValue());
			
			if(this.uploadFile.getUploadedFile() != null) {
				this.document.setFile(this.uploadFile.getUploadedFile());
				this.document.setType(this.uploadFile.getFileType());
			}
			
			bo.save(Session.getIdUserLog(), this.document);
			
			this.showSuccessNotification("Salvar Documento", "Documento salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Documento", e.getMessage());
		}
	}
	
}
