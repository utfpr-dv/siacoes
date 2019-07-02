package br.edu.utfpr.dv.siacoes.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.utfpr.dv.siacoes.dao.TutoredDAO;
import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Semester;
import br.edu.utfpr.dv.siacoes.model.SupervisorChange;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Tutored;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor;
import br.edu.utfpr.dv.siacoes.model.TutoredBySupervisor.TutoredGroupedBySupervisor;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class TutoredBO {
	
	public List<TutoredBySupervisor> listTutoredBySupervisor(int idDepartment, int initialYear, int finalYear, int stage) throws Exception {
		try {
			TutoredDAO dao = new TutoredDAO();
			
			return dao.listTutoredBySupervisor(idDepartment, initialYear, finalYear, stage);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<TutoredGroupedBySupervisor> listTutoredGroupedBySupervisor(int idDepartment, int initialYear, int finalYear, int stage) throws Exception {
		try {
			TutoredDAO dao = new TutoredDAO();
			
			List<TutoredBySupervisor> list = dao.listTutoredBySupervisor(idDepartment, initialYear, finalYear, stage);
			List<TutoredGroupedBySupervisor> ret = new ArrayList<TutoredGroupedBySupervisor>();
			
			for(TutoredBySupervisor item : list) {
				boolean found = false;
				
				for(TutoredGroupedBySupervisor group : ret) {
					if(group.getIdSupervisor() == item.getIdSupervisor()) {
						found = true;
						group.getTutored().add(item);
					}
				}
				
				if(!found) {
					ret.add(item.getGrouped());
				}
			}
			
			for(TutoredGroupedBySupervisor group : ret) {
				int index = 0;
				
				for(int year = initialYear; year <= finalYear; year++) {
					for(int semester = 1; semester <= 2; semester++) {
						boolean found = false;
						
						for(TutoredBySupervisor t : group.getTutored()) {
							if((t.getYear() == year) && (t.getSemester() == semester)) {
								found = true;
							}
						}
						
						if(!found) {
							TutoredBySupervisor t = new TutoredBySupervisor();
							
							t.setYear(year);
							t.setSemester(semester);
							t.setTotal(0);
							t.setIdSupervisor(group.getIdSupervisor());
							t.setSupervisorName(group.getSupervisorName());
							
							group.getTutored().add(index, t);
						}
						
						index++;
					}
				}
			}
			
			return ret;
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			throw new Exception(e.getMessage());
		}
	}
	
	public List<Tutored> listBySupervisor(int idSupervisor, int semester, int year) throws Exception {
		List<Tutored> list = this.listBySupervisor(idSupervisor);
		
		for(int i = list.size() - 1; i >= 0; i--) {
			if((list.get(i).getSemester() != semester) || (list.get(i).getYear() != year)) {
				list.remove(i);
			}
		}
		
		return list;
	}
	
	public List<Tutored> listBySupervisor(int idSupervisor) throws Exception {
		List<Tutored> list = new ArrayList<Tutored>();
		
		ProposalBO bo = new ProposalBO();
		List<Proposal> proposals = bo.listBySupervisor(idSupervisor);
		
		for(Proposal p : proposals) {
			Tutored t = new Tutored();
			
			t.setProposal(p);
			t.setStudent(p.getStudent());
			t.setSupervisor(p.getSupervisor());
			t.setCosupervisor(p.getCosupervisor());
			t.setTitle(p.getTitle());
			t.setSemester(p.getSemester());
			t.setYear(p.getYear());
			t.setStage(1);
			
			list.add(t);
		}
		
		ProjectBO pbo = new ProjectBO();
		List<Project> projects = pbo.listBySupervisor(idSupervisor);
		
		for(Project p : projects) {
			for(Tutored t : list) {
				if(p.getProposal().getIdProposal() == t.getProposal().getIdProposal()) {
					t.setProject(p);
					t.setSupervisor(p.getSupervisor());
					t.setCosupervisor(p.getCosupervisor());
					t.setTitle(p.getTitle());
					t.setSemester(p.getSemester());
					t.setYear(p.getYear());
					
					Semester semester = new SemesterBO().findByDate(new ProjectBO().findIdCampus(p.getIdProject()), DateUtils.getToday().getTime());
					
					if((p.getYear() < semester.getYear()) || ((p.getYear() == semester.getYear()) && (p.getSemester() < semester.getSemester()))) {
						if(((p.getYear() + 1) < semester.getYear()) || (((p.getYear() + 1) == semester.getYear()) && (p.getSemester() < semester.getSemester()))) {
							t.setStage(1);
						} else {
							t.setStage(2);
							t.setSemester(semester.getSemester());
							t.setYear(semester.getYear());
						}
					}
				}
			}
		}
		
		ThesisBO tbo = new ThesisBO();
		List<Thesis> thesis = tbo.listBySupervisor(idSupervisor);
		
		for(Thesis th : thesis) {
			for(Tutored t : list) {
				if(th.getProject().getIdProject() == t.getProject().getIdProject()) {
					t.setThesis(th);
					t.setSupervisor(th.getSupervisor());
					t.setCosupervisor(th.getCosupervisor());
					t.setTitle(th.getTitle());
					t.setSemester(th.getSemester());
					t.setYear(th.getYear());
					t.setStage(2);
				}
			}
		}
		
		SupervisorChangeBO sbo = new SupervisorChangeBO();
		List<SupervisorChange> changes = sbo.list(idSupervisor);
		
		for(SupervisorChange ch : changes) {
			boolean find = false;
			
			for(Tutored t : list) {
				if(t.getProposal().getIdProposal() == ch.getProposal().getIdProposal()) {
					find = true;
				}
			}
			
			if(!find) {
				Tutored t = new Tutored();
				
				t.setProposal(ch.getProposal());
				t.setStudent(ch.getProposal().getStudent());
				t.setSupervisor(ch.getNewSupervisor());
				t.setCosupervisor(ch.getNewCosupervisor());
				t.setTitle(ch.getProposal().getTitle());
				t.setStage(1);
				
				Thesis th = new ThesisBO().findByProposal(ch.getProposal().getIdProposal());
				if(th != null) {
					t.setThesis(th);
					t.setTitle(th.getTitle());
					t.setSemester(th.getSemester());
					t.setYear(th.getYear());
					t.setStage(2);
				} else {
					Project p = new ProjectBO().findByProposal(ch.getProposal().getIdProposal());
					
					if(p != null) {
						t.setTitle(p.getTitle());
						t.setSemester(p.getSemester());
						t.setYear(p.getYear());
						
						Semester semester = new SemesterBO().findByDate(new ProjectBO().findIdCampus(p.getIdProject()), DateUtils.getToday().getTime());
						
						if((p.getYear() < semester.getYear()) || ((p.getYear() == semester.getYear()) && (p.getSemester() < semester.getSemester()))){
							if(((p.getYear() + 1) < semester.getYear()) || (((p.getYear() + 1) == semester.getYear()) && (p.getSemester() < semester.getSemester()))) {
								t.setStage(1);
							} else {
								t.setStage(2);
								t.setSemester(semester.getSemester());
								t.setYear(semester.getYear());
							}
						}
					}
				}
				
				list.add(t);
			}
		}
		
		return list;
	}

}
