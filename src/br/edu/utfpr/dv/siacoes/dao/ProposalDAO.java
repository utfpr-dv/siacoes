package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.Project;
import br.edu.utfpr.dv.siacoes.model.Proposal;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.Thesis;
import br.edu.utfpr.dv.siacoes.model.User;
import br.edu.utfpr.dv.siacoes.model.Document.DocumentType;

public class ProposalDAO {
	
	public Proposal findById(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM proposal inner join \"user\" student on student.idUser=proposal.idStudent inner join \"user\" supervisor on supervisor.idUser=proposal.idSupervisor left join \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor WHERE idProposal = ?");
		
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Proposal findByProject(int idProject) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT idProposal FROM project WHERE idProject=?");
		
			stmt.setInt(1, idProject);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.findById(rs.getInt("idProposal"));
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Proposal findCurrentProposal(int idStudent, int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			ThesisDAO tdao = new ThesisDAO();
			Thesis thesis = tdao.findCurrentThesis(idStudent, idDepartment, semester, year);
			
			ProjectDAO pdao = new ProjectDAO();
			Project project;
			
			if(thesis == null){
				project = pdao.findCurrentProject(idStudent, idDepartment, semester, year);
			}else{
				project = pdao.findById(thesis.getProject().getIdProject());	
			}
			
			if(project == null){
				stmt = conn.prepareStatement("SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM proposal inner join \"user\" student on student.idUser=proposal.idStudent inner join \"user\" supervisor on supervisor.idUser=proposal.idSupervisor left join \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor WHERE proposal.idStudent = ? AND proposal.idDepartment=? AND proposal.semester = ? AND proposal.year = ?");
				
				stmt.setInt(1, idStudent);
				stmt.setInt(2, idDepartment);
				stmt.setInt(3, semester);
				stmt.setInt(4, year);
				
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next()){
					return this.loadObject(rs);
				}else{
					return null;
				}	
			}else{
				return this.findById(project.getProposal().getIdProposal());
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public Proposal findLastProposal(int idStudent, int idDepartment) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM proposal inner join \"user\" student on student.idUser=proposal.idStudent inner join \"user\" supervisor on supervisor.idUser=proposal.idSupervisor left join \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor WHERE proposal.idStudent = ? AND proposal.idDepartment=? ORDER BY year DESC, semester DESC");
		
			stmt.setInt(1, idStudent);
			stmt.setInt(2, idDepartment);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Proposal> listBySemester(int idDepartment, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement(
				"SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName " +
				"FROM proposal INNER JOIN \"user\" student ON student.idUser=proposal.idStudent " +
				"INNER JOIN \"user\" supervisor ON supervisor.idUser=proposal.idSupervisor " +
				"LEFT JOIN \"user\" cosupervisor ON cosupervisor.idUser=proposal.idCosupervisor " +
				"WHERE proposal.idDepartment=? AND proposal.semester = ? AND proposal.year = ? ORDER BY proposal.title");
		
			stmt.setInt(1, idDepartment);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
			ResultSet rs = stmt.executeQuery();
			List<Proposal> list = new ArrayList<Proposal>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Proposal> listByAppraiser(int idAppraiser, int semester, int year) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM proposal inner join \"user\" student on student.idUser=proposal.idStudent inner join \"user\" supervisor on supervisor.idUser=proposal.idSupervisor inner join proposalappraiser appraiser on appraiser.idProposal=proposal.idProposal left join \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor WHERE appraiser.idAppraiser = ? AND semester = ? AND year = ? ORDER BY title");
		
			stmt.setInt(1, idAppraiser);
			stmt.setInt(2, semester);
			stmt.setInt(3, year);
			
			ResultSet rs = stmt.executeQuery();
			List<Proposal> list = new ArrayList<Proposal>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Proposal> listByStudent(int idStudent) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM proposal inner join \"user\" student on student.idUser=proposal.idStudent inner join \"user\" supervisor on supervisor.idUser=proposal.idSupervisor left join \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor WHERE idStudent = ? ORDER BY title");
		
			stmt.setInt(1, idStudent);
			
			ResultSet rs = stmt.executeQuery();
			List<Proposal> list = new ArrayList<Proposal>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int getProposalStage(int idProposal) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT idProject FROM project WHERE idProposal=" + String.valueOf(idProposal));
			
			if(rs.next()){
				rs = stmt.executeQuery("SELECT idThesis FROM thesis WHERE idProject=" + String.valueOf(rs.getInt("idProject")));
				
				if(rs.next()){
					return 2;
				}else{
					return 1;
				}
			}else{
				return 0;
			}
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Proposal> listAll() throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT proposal.*, student.name as studentName, supervisor.name as supervisorName, cosupervisor.name as cosupervisorName FROM proposal inner join \"user\" student on student.idUser=proposal.idStudent inner join \"user\" supervisor on supervisor.idUser=proposal.idSupervisor left join \"user\" cosupervisor on cosupervisor.idUser=proposal.idCosupervisor ORDER BY year DESC, semester DESC, title");
			List<Proposal> list = new ArrayList<Proposal>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));			
			}
			
			return list;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public int save(Proposal proposal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			conn.setAutoCommit(false);
			boolean insert = (proposal.getIdProposal() == 0);
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO proposal(idDepartment, semester, year, title, subarea, idStudent, idSupervisor, idCosupervisor, file, fileType, submissionDate) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE proposal SET idDepartment=?, semester=?, year=?, title=?, subarea=?, idStudent=?, idSupervisor=?, idCosupervisor=?, file=?, fileType=?, submissionDate=? WHERE idProposal=?");
			}
			
			stmt.setInt(1, proposal.getDepartment().getIdDepartment());
			stmt.setInt(2, proposal.getSemester());
			stmt.setInt(3, proposal.getYear());
			stmt.setString(4, proposal.getTitle());
			stmt.setString(5, proposal.getSubarea());
			stmt.setInt(6, proposal.getStudent().getIdUser());
			stmt.setInt(7, proposal.getSupervisor().getIdUser());
			if((proposal.getCosupervisor() == null) || (proposal.getCosupervisor().getIdUser() == 0)){
				stmt.setNull(8, Types.INTEGER);
			}else{
				stmt.setInt(8, proposal.getCosupervisor().getIdUser());
			}
			stmt.setBytes(9, proposal.getFile());
			stmt.setInt(10, proposal.getFileType().getValue());
			stmt.setDate(11, new java.sql.Date(proposal.getSubmissionDate().getTime()));
			
			if(!insert){
				stmt.setInt(12, proposal.getIdProposal());
			}
			
			stmt.execute();
			
			if(insert){
				ResultSet rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					proposal.setIdProposal(rs.getInt(1));
				}
			}
			
			if(proposal.getAppraisers() != null){
				ProposalAppraiserDAO dao = new ProposalAppraiserDAO(conn);
				String ids = "";
				
				for(ProposalAppraiser pa : proposal.getAppraisers()){
					pa.setProposal(proposal);
					int paId = dao.save(pa);
					ids = ids + String.valueOf(paId) + ",";
				}
				
				if(!ids.isEmpty()){
					Statement st = conn.createStatement();
					st.execute("DELETE FROM proposalappraiser WHERE idProposal=" + String.valueOf(proposal.getIdProposal()) + " AND idProposalAppraiser NOT IN(" + ids.substring(0, ids.lastIndexOf(",")) + ")");
					st.close();
				}
			}
			
			conn.commit();
			
			return proposal.getIdProposal();
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	private Proposal loadObject(ResultSet rs) throws SQLException{
		Proposal p = new Proposal();
		
		p.setIdProposal(rs.getInt("idProposal"));
		p.setTitle(rs.getString("title"));
		p.setSubarea(rs.getString("subarea"));
		p.setFile(rs.getBytes("file"));
		p.setFileType(DocumentType.valueOf(rs.getInt("fileType")));
		p.getStudent().setIdUser(rs.getInt("idStudent"));
		p.getStudent().setName(rs.getString("studentName"));
		p.setSubmissionDate(rs.getDate("submissionDate"));
		p.getSupervisor().setIdUser(rs.getInt("idSupervisor"));
		p.getSupervisor().setName(rs.getString("supervisorName"));
		p.setCosupervisor(new User());
		p.getCosupervisor().setIdUser(rs.getInt("idCosupervisor"));
		p.getCosupervisor().setName(rs.getString("cosupervisorname"));
		p.setSemester(rs.getInt("semester"));
		p.setYear(rs.getInt("year"));
		p.getDepartment().setIdDepartment(rs.getInt("idDepartment"));
		
		return p;
	}

}
