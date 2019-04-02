package br.edu.utfpr.dv.siacoes.window;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextField;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.DepartmentBO;
import br.edu.utfpr.dv.siacoes.components.CampusComboBox;
import br.edu.utfpr.dv.siacoes.components.FileUploader;
import br.edu.utfpr.dv.siacoes.components.FileUploaderListener;
import br.edu.utfpr.dv.siacoes.model.Department;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditDepartmentWindow extends EditWindow {
	
	private final Department department;
	
	private final CampusComboBox comboCampus;
	private final TextField textName;
	private final TextField textFullName;
	private final CheckBox checkActive;
	private final FileUploader uploadLogo;
	private final Image imageLogo;
	private final TextField textSite;
	private final TextField textInitials;

	public EditDepartmentWindow(Department d, ListView parentView){
		super("Editar Departamento", parentView);
		
		if(d == null){
			this.department = new Department();
		}else{
			this.department = d;
		}
		
		this.comboCampus = new CampusComboBox();
		this.comboCampus.setFilterOnlyActives(false);
		this.comboCampus.setRequired(true);
		
		this.textName = new TextField("Nome");
		this.textName.setWidth("400px");
		this.textName.setMaxLength(100);
		this.textName.setRequired(true);
		
		this.textFullName = new TextField("Nome Detalhado");
		this.textFullName.setWidth("800px");
		this.textFullName.setMaxLength(255);
		
		this.textSite = new TextField("Site");
		this.textSite.setWidth("800px");
		this.textSite.setMaxLength(255);
		
		this.textInitials = new TextField("Sigla");
		this.textInitials.setWidth("400px");
		this.textInitials.setMaxLength(50);
		
		this.checkActive = new CheckBox("Ativo");
		
		this.uploadLogo = new FileUploader("Enviar Logotipo");
		this.uploadLogo.getAcceptedDocumentTypes().add(DocumentType.JPEG);
		this.uploadLogo.getAcceptedDocumentTypes().add(DocumentType.PNG);
		this.uploadLogo.setMaxBytesLength(300 * 1024);
		this.uploadLogo.setFileUploadListener(new FileUploaderListener() {
			@Override
			public void uploadSucceeded() {
				if(uploadLogo.getFileUploadListener() != null) {
					department.setLogo(uploadLogo.getUploadedFile());
				}
				
				loadLogo();
			}
		});
		
		this.imageLogo = new Image();
		this.imageLogo.setSizeUndefined();
		this.imageLogo.setHeight("200px");
		
		this.addField(new HorizontalLayout(this.textName, this.comboCampus));
		this.addField(this.textFullName);
		this.addField(new HorizontalLayout(this.textInitials, this.checkActive));
		this.addField(this.textSite);
		this.addField(new HorizontalLayout(this.uploadLogo, this.imageLogo));
		
		this.loadDepartment();
		this.textName.focus();
	}
	
	private void loadDepartment(){
		this.comboCampus.setCampus(this.department.getCampus());
		this.textName.setValue(this.department.getName());
		this.checkActive.setValue(this.department.isActive());
		this.textSite.setValue(this.department.getSite());
		this.textFullName.setValue(this.department.getFullName());
		this.textInitials.setValue(this.department.getInitials());
		
		this.loadLogo();
	}
	
	private void loadLogo(){
		if(this.department.getLogo() != null){
			StreamResource resource = new StreamResource(
	            new StreamResource.StreamSource() {
	                @Override
	                public InputStream getStream() {
	                    return new ByteArrayInputStream(department.getLogo());
	                }
	            }, "filename" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(DateUtils.getNow().getTime()) + ".png");
	
			resource.setCacheTime(0);
		    this.imageLogo.setSource(resource);
		}
	}
	
	@Override
	public void save() {
		try{
			DepartmentBO bo = new DepartmentBO();
			
			this.department.setCampus(this.comboCampus.getCampus());
			this.department.setName(this.textName.getValue());
			this.department.setActive(this.checkActive.getValue());
			this.department.setSite(this.textSite.getValue());
			this.department.setFullName(this.textFullName.getValue());
			this.department.setInitials(this.textInitials.getValue());
			
			if(this.uploadLogo.getUploadedFile() != null) {
				this.department.setLogo(this.uploadLogo.getUploadedFile());
			}
			
			bo.save(Session.getIdUserLog(), this.department);
			
			this.showSuccessNotification("Salvar Departamento", "Departamento salvo com sucesso.");
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Salvar Departamento", e.getMessage());
		}
	}
	
}
