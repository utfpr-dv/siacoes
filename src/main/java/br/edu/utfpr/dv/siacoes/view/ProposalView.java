package br.edu.utfpr.dv.siacoes.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.ProposalAppraiserBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SemesterBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.SemesterComboBox;
import br.edu.utfpr.dv.siacoes.components.YearField;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SigetConfig;
import br.edu.utfpr.dv.siacoes.model.Module.SystemModule;
import br.edu.utfpr.dv.siacoes.model.User.UserProfile;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.window.EditProposalWindow;

public class ProposalView extends ListView {

	public static final String NAME = "proposal";
	
	private SigetConfig config;
	
	private final SemesterComboBox comboSemester;
	private final YearField textYear;
	private final Button buttonDownload;
	private final Button buttonInvalidate;
	private final Button buttonCloseFeedback;
	
	private Button.ClickListener listenerClickDownload;
	
	public ProposalView(){
		super(SystemModule.SIGET);
		
		this.setCaption("Propostas de TCC 1");
		
		this.setProfilePerimissions(UserProfile.MANAGER);
		
		this.setAddVisible(false);
		this.setDeleteVisible(false);
		
		this.config = new SigetConfig();
		try {
			this.config = new SigetConfigBO().findByDepartment(Session.getSelectedDepartment().getDepartment().getIdDepartment());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.buttonDownload = new Button("Down. da Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	downloadFile();
            }
        });
		this.buttonDownload.setIcon(FontAwesome.DOWNLOAD);
		if(this.config.isRegisterProposal()) {
			this.addActionButton(this.buttonDownload);
		}
		
		this.buttonInvalidate = new Button("Invalidar Proposta", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	invalidateProposal();
            }
        });
		this.buttonInvalidate.setIcon(FontAwesome.REMOVE);
		this.buttonInvalidate.addStyleName(ValoTheme.BUTTON_DANGER);
		if(this.config.isRegisterProposal()) {
			this.addActionButton(this.buttonInvalidate);
		}
		this.buttonCloseFeedback = new Button("Encerrar Avaliações", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	closeFeedback();
            }
        });
		this.buttonCloseFeedback.setIcon(FontAwesome.CALENDAR_TIMES_O);
		if(this.config.isRegisterProposal()) {
			this.addActionButton(this.buttonCloseFeedback);
		}
		
		Semester semester;
		try {
			semester = new SemesterBO().findByDate(Session.getSelectedDepartment().getDepartment().getCampus().getIdCampus(), DateUtils.getToday().getTime());
		} catch (Exception e) {
			semester = new Semester();
		}
		
		this.comboSemester = new SemesterComboBox();
		this.comboSemester.select(semester.getSemester());
		
		this.textYear = new YearField();
		this.textYear.setYear(semester.getYear());
		
		this.addFilterField(new HorizontalLayout(this.comboSemester, this.textYear));
		
		if(!this.config.isRegisterProposal()) {
			this.setEditCaption("Visualizar");
			this.setEditIcon(FontAwesome.SEARCH);
		}
	}
	
	protected void loadGrid(){
		this.getGrid().addColumn("Semestre", Integer.class);
		this.getGrid().addColumn("Ano", Integer.class);
		this.getGrid().addColumn("Acadêmico", String.class);
		this.getGrid().addColumn("Orientador", String.class);
		this.getGrid().addColumn("Título", String.class);
		this.getGrid().addColumn("Registro", Date.class).setRenderer(new DateRenderer(new SimpleDateFormat("dd/MM/yyyy")));
		this.getGrid().addColumn("Prop. Env.", String.class);
		this.getGrid().addColumn("Feedback Orient.", String.class);
		
		this.getGrid().getColumns().get(0).setWidth(100);
		this.getGrid().getColumns().get(1).setWidth(100);
		this.getGrid().getColumns().get(5).setWidth(100);
		this.getGrid().getColumns().get(6).setWidth(100);
		this.getGrid().getColumns().get(7).setWidth(125);
		
		if(!this.config.isSupervisorAgreement()) {
			this.getGrid().getColumns().get(7).setHidden(true);
		}
		
		try {
			ProposalBO bo = new ProposalBO();
	    	List<Proposal> list = bo.listBySemester(Session.getSelectedDepartment().getDepartment().getIdDepartment(), this.comboSemester.getSemester(), this.textYear.getYear());
	    	
	    	for(Proposal p : list){
				Object itemId = this.getGrid().addRow(p.getSemester(), p.getYear(), p.getStudent().getName(), p.getSupervisor().getName(), p.getTitle(), p.getSubmissionDate(), (p.isFileUploaded() ? "Sim" : "Não"), ((p.getSupervisorFeedback() == ProposalFeedback.APPROVED) ? "Favorável" : ((p.getSupervisorFeedback() == ProposalFeedback.DISAPPROVED) ? "Desfavorável" : "Nenhum")));
				this.addRowId(itemId, p.getIdProposal());
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			this.showErrorNotification("Listar Propostas", e.getMessage());
		}
    }
	
	private void downloadFile(){
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Download da Proposta", "Selecione um registro para baixar a proposta.");
		}else{
			try{
				ProposalBO bo = new ProposalBO();
            	Proposal p = bo.findById((int)value);
				
            	if(p.getFile() != null) {
            		this.showReport(p.getFile());
            	} else {
            		this.showWarningNotification("Download da Proposta", "O acadêmico ainda não efetuou a submissão da proposta.");
            	}
			}catch(Exception e){
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
	        	
				this.showErrorNotification("Download da Proposta", e.getMessage());
			}
		}
	}
	
	private void invalidateProposal() {
		Object value = getIdSelected();
		
		if(value == null){
			this.showWarningNotification("Invalidar Proposta", "Selecione um registro para invalidar a proposta.");
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a invalidação da proposta?\n\nIsso fará com que o acadêmico tenha que enviar uma nova proposta.", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	try {
                    		ProposalBO bo = new ProposalBO();
							bo.invalidated(Session.getIdUserLog(), (int)value);
							
							refreshGrid();
							
							showSuccessNotification("Invalidar Proposta", "Proposta invalidada com sucesso.");
						} catch (Exception e) {
							showErrorNotification("Invalidar Proposta", e.getMessage());
						}
                    }
                }
            });
		}
	}
	
	private void closeFeedback() {
		ConfirmDialog.show(UI.getCurrent(), "Confirma o encerramento das avaliações de Proposta de TCC 1 para o semestre " + 
				String.valueOf(this.comboSemester.getSemester()) + "/" + String.valueOf(this.textYear.getYear()) + "?", new ConfirmDialog.Listener() {
            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                	try {
                		ProposalAppraiserBO bo = new ProposalAppraiserBO();
                		
						bo.closeFeedback(Session.getSelectedDepartment().getDepartment().getIdDepartment(), comboSemester.getSemester(), textYear.getYear());
						
						showSuccessNotification("Encerrar Avaliações", "Avaliações encerradas com sucesso.");
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
						
						showErrorNotification("Encerrar Avaliações", e.getMessage());
					}
                }
            }
        });
	}
	
	@Override
	public void addClick() {
		
	}

	@Override
	public void editClick(Object id) {
		try {
			ProposalBO bo = new ProposalBO();
			Proposal p = bo.findById((int)id);
			
			UI.getCurrent().addWindow(new EditProposalWindow(p, this, this.config.isRegisterProposal()));
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			this.showErrorNotification("Editar Proposta", e.getMessage());
		}
	}

	@Override
	public void deleteClick(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filterClick() {
		// TODO Auto-generated method stub
		
	}

}
