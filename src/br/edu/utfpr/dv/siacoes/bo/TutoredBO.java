package br.edu.utfpr.dv.siacoes.bo;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.Tutored;
import br.edu.utfpr.dv.siacoes.util.DateUtils;

public class TutoredBO {
	
	/*public List<Tutored> listAll(){
		
	}*/
	
	public List<Tutored> listBySupervisor(int idSupervisor) throws Exception{
		List<Tutored> list = new ArrayList<Tutored>();
		
		ProposalBO bo = new ProposalBO();
		List<Proposal> proposals = bo.listBySupervisor(idSupervisor);
		
		for(Proposal p : proposals){
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
		
		for(Project p : projects){
			for(Tutored t : list){
				if(p.getProposal().getIdProposal() == t.getProposal().getIdProposal()){
					t.setProject(p);
					t.setSupervisor(p.getSupervisor());
					t.setCosupervisor(p.getCosupervisor());
					t.setTitle(p.getTitle());
					t.setSemester(p.getSemester());
					t.setYear(p.getYear());
					
					if((p.getYear() < DateUtils.getYear()) || ((p.getYear() == DateUtils.getYear()) && (p.getSemester() < DateUtils.getSemester()))){
						t.setStage(2);
					}
				}
			}
		}
		
		ThesisBO tbo = new ThesisBO();
		List<Thesis> thesis = tbo.listBySupervisor(idSupervisor);
		
		for(Thesis th : thesis){
			for(Tutored t : list){
				if(th.getProject().getIdProject() == t.getProject().getIdProject()){
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
		
		return list;
	}

}
