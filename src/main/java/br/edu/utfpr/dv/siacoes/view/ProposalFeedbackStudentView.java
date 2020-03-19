package br.edu.utfpr.dv.siacoes.view;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.window.CommentWindow;

public class ProposalFeedbackStudentView extends ListView {
	
	public static final String NAME = "proposalfeedbackstudent";
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	
	public ProposalFeedbackStudentView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Pareceres da Proposta de TCC 1");
		
		this.setProfilePerimissions(UserProfile.STUDENT);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.setEditCaption("Observações");
		
		this.comboSemester = new SemesterComboBox();
		
		this.textYear = new YearField();
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
	}

	@Override
	protected void loadGrid() {
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Avaliador", String.class);
		this.getGrid().addColumn("Parecer", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(5).setWidth(150);
		
		try {
			ProposalBO bo = new ProposalBO();
			ProposalAppraiserBO abo = new ProposalAppraiserBO();
	    	List<Proposal> list = null;
	    	
	    	if(Session.isUserStudent()){
	    		list = bo.listByStudent(Session.getUser().getIdUser(), Session.getSelectedDepartment().getDepartment().getIdDepartment());
	    	}else{
	    		list = bo.listBySupervisor(Session.getUser().getIdUser());
	    	}
	    	
	    	for(Proposal p : list){
	    		List<ProposalAppraiser> appraisers = abo.listAppraisers(p.getIdProposal());
	    		
	    		for(ProposalAppraiser a : appraisers){
	    			Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getStudent().getName(), p.getTitle(), a.getAppraiser().getName(), a.getFeedback().toString());
					this.addRowId(itemId, a.getIdProposalAppraiser());	
	    		}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			this.showErrorNotification("Listar Propostas", e.getMessage());
		}
	}

	@Override
	public void addClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editClick(Object id) {
		try {
			ProposalAppraiserBO bo = new ProposalAppraiserBO();
			ProposalAppraiser appraiser = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new CommentWindow("Observações", appraiser.getComments()));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Carregar Observações", e.getMessage());
		}
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
