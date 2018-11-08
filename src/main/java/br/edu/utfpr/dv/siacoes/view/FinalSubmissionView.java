package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.FinalSubmissionBO;
import br.edu.utfpr.dv.siacoes.model.FinalSubmission;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;

public class FinalSubmissionView extends ListView {
	
	public static final String NAME = "finalsubmission";
	
	private final Button buttonFinalReport;
	
	public FinalSubmissionView() {
		super(SystemModule.SIGAC);
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setCaption("Acadêmicos Aprovados");
		
		this.buttonFinalReport = new Button("Relatório Final", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	finalReport();
            }
        });
		this.buttonFinalReport.setIcon(FontAwesome.FILE_PDF_O);
		
		this.setFiltersVisible(false);
		this.setAddVisible(false);
		this.setEditVisible(false);
		this.setDeleteVisible(false);
		this.addActionButton(this.buttonFinalReport);
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Pont. Final", Double.class);
		this.getGrid().addColumn("Data", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Avaliador", String.class);
		
		this.getGrid().getColumns().get(1).setWidth(150);
		this.getGrid().getColumns().get(2).setWidth(150);
		
		try{
			FinalSubmissionBO bo = new FinalSubmissionBO();
			List<FinalSubmission> list = bo.listByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
			
			for(FinalSubmission submission : list) {
				Object itemId = this.getGrid().addRow(submission.getStudent().getName(), submission.getFinalScore(), submission.getDate(), submission.getFeedbackUser().getName());
				
				this.addRowId(itemId, submission.getIdFinalSubmission());
			}
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Listar Acadêmicos", e.getMessage());
		}
	}
	
	private void finalReport() {
		Object value = this.getIdSelected();
    	
    	if(value == null){
    		this.showWarningNotification("Relatório Final", "Selecione o acadêmico para imprimir o relatório final.");
    	}else{
    		try {
    			FinalSubmissionBO bo = new FinalSubmissionBO();
				FinalSubmission submission = bo.findById((int)value);
				
				this.showReport(submission.getReport());
			} catch (Exception e) {
				e.printStackTrace();
				
				this.showErrorNotification("Relatório Final", e.getMessage());
			}
    	}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		// TODO Auto-generated method stub
		
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
