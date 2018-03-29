package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;

public class ProposalAppraiserDAO {
	
	private Connection conn;
	
	public ProposalAppraiserDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public ProposalAppraiserDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}

	public List<ProposalAppraiser> listAppraisers(int idProposal) throws SQLException{
		ResultSet rs = null;
		Statement stmt = null;
		
		try{
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery("SELECT proposalappraiser.*, appraiser.name as appraiserName FROM proposalappraiser inner join \"user\" appraiser on appraiser.idUser=proposalappraiser.idAppraiser WHERE idProposal = " + String.valueOf(idProposal));
			List<ProposalAppraiser> list = new ArrayList<ProposalAppraiser>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public ProposalAppraiser findById(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT proposalappraiser.*, appraiser.name as appraiserName FROM proposalappraiser inner join \"user\" appraiser on appraiser.idUser=proposalappraiser.idAppraiser WHERE idProposalAppraiser = ?");
			
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public ProposalFeedback findFeedback(int id) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT proposalappraiser.feedback FROM proposalappraiser WHERE idProposalAppraiser = ?");
			
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return ProposalFeedback.valueOf(rs.getInt("feedback"));
			}else{
				return ProposalFeedback.NONE;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public ProposalAppraiser findByAppraiser(int idProposal, int idAppraiser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT proposalappraiser.*, appraiser.name as appraiserName FROM proposalappraiser inner join \"user\" appraiser on appraiser.idUser=proposalappraiser.idAppraiser WHERE idProposal = ? AND idAppraiser = ?");
			
			stmt.setInt(1, idProposal);
			stmt.setInt(2, idAppraiser);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public int save(ProposalAppraiser appraiser) throws SQLException{
		boolean insert = (appraiser.getIdProposalAppraiser() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO proposalappraiser(idProposal, idAppraiser, feedback, comments, allowEditing, supervisorIndication) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE proposalappraiser SET idProposal=?, idAppraiser=?, feedback=?, comments=?, allowEditing=?, supervisorIndication=? WHERE idProposalAppraiser=?");
			}
			
			stmt.setInt(1, appraiser.getProposal().getIdProposal());
			stmt.setInt(2, appraiser.getAppraiser().getIdUser());
			stmt.setInt(3, appraiser.getFeedback().getValue());
			stmt.setString(4, appraiser.getComments());
			stmt.setInt(5, (appraiser.isAllowEditing() ? 1 : 0));
			stmt.setInt(6, (appraiser.isSupervisorIndication() ? 1 : 0));
			
			if(!insert){
				stmt.setInt(7, appraiser.getIdProposalAppraiser());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					appraiser.setIdProposalAppraiser(rs.getInt(1));
				}
			}
			
			return appraiser.getIdProposalAppraiser();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private ProposalAppraiser loadObject(ResultSet rs) throws SQLException{
		ProposalAppraiser p = new ProposalAppraiser();
		
		p.setIdProposalAppraiser(rs.getInt("idProposalAppraiser"));
		p.getProposal().setIdProposal(rs.getInt("idProposal"));
		p.getAppraiser().setIdUser(rs.getInt("idAppraiser"));
		p.getAppraiser().setName(rs.getString("appraiserName"));
		p.setComments(rs.getString("comments"));
		p.setFeedback(ProposalFeedback.valueOf(rs.getInt("feedback")));
		p.setAllowEditing(rs.getInt("allowEditing") == 1);
		p.setSupervisorIndication(rs.getInt("supervisorIndication") == 1);
		
		return p;
	}
	
}
