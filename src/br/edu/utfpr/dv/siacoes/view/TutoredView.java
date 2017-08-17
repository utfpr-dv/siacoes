package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.CertificateBO;
import br.edu.utfpr.dv.siacoes.bo.TutoredBO;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.Tutored;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.EditTutoredWindow;

public class TutoredView extends ListView {
	
	public static final String NAME = "tutored";
	
	private final Button buttonStatementStage1;
	private final Button buttonStatementStage2;
	
	public TutoredView(){
		super(SystemModule.SIGET);
		
		this.setProfilePerimissions(UserProfile.PROFESSOR);
		
		this.buttonStatementStage1 = new Button("Declaração TCC 1", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement(1);
            }
        });
		
		this.buttonStatementStage2 = new Button("Declaração TCC 2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadProfessorStatement(2);
            }
        });
		
		this.setFiltersVisible(false);
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.addActionButton(this.buttonStatementStage1);
		this.addActionButton(this.buttonStatementStage2);
		
		this.setEditCaption("Visualizar");
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Aluno", String.class);
		this.getGrid().addColumn("TCC", Integer.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Sem.", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		
		this.getGrid().getColumns().get(1).setWidth(75);
		this.getGrid().getColumns().get(3).setWidth(75);
		this.getGrid().getColumns().get(4).setWidth(100);
		
		try {
			TutoredBO bo = new TutoredBO();
	    	List<Tutored> list = bo.listBySupervisor(Session.getUser().getIdUser());
	    	
	    	for(Tutored t : list){
				Object itemId = this.getGrid().addRow(t.getStudent().getName(), t.getStage(), t.getTitle(), t.getSemester(), t.getYear());
				this.addRowId(itemId, t.getProposal().getIdProposal());
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Listar Orientados", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void downloadProfessorStatement(int stage){
		Object value = getIdSelected();
		
		if(value == null){
			Notification.show("Gerar Declaração", "Selecione um registro para gerar a declaração.", Notification.Type.WARNING_MESSAGE);
		}else{
			try{
				CertificateBO bo = new CertificateBO();

				byte[] report = bo.getThesisProfessorStatement(Session.getUser().getIdUser(), (int)value, stage);
				
				Session.putReport(report);
				
				getUI().getPage().open("#!" + CertificateView.NAME + "/session/" + UUID.randomUUID().toString(), "_blank");
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
	        	Notification.show("Gerar Declaração", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		UI.getCurrent().addWindow(new EditTutoredWindow((int)id, this));
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
