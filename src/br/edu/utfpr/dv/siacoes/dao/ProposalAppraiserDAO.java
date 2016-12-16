package br.edu.utfpr.dv.siacoes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;

public class ProposalAppraiserDAO {

	public List<ProposalAppraiser> listAppraisers(int idProposal) throws SQLException{
		Statement stmt = ConnectionDAO.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT proposalappraiser.*, appraiser.name as appraiserName FROM proposalappraiser inner join user appraiser on appraiser.idUser=proposalappraiser.idAppraiser WHERE idProposal = " + String.valueOf(idProposal));
		List<ProposalAppraiser> list = new ArrayList<ProposalAppraiser>();
		
		while(rs.next()){
			list.add(this.loadObject(rs));
		}
		
		return list;
	}
	
	public ProposalAppraiser findById(int id) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT proposalappraiser.*, appraiser.name as appraiserName FROM proposalappraiser inner join user appraiser on appraiser.idUser=proposalappraiser.idAppraiser WHERE idProposalAppraiser = ?");
		
		stmt.setInt(1, id);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public ProposalAppraiser findByAppraiser(int idProposal, int idAppraiser) throws SQLException{
		PreparedStatement stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("SELECT proposalappraiser.*, appraiser.name as appraiserName FROM proposalappraiser inner join user appraiser on appraiser.idUser=proposalappraiser.idAppraiser WHERE idProposal = ? AND idAppraiser = ?");
		
		stmt.setInt(1, idProposal);
		stmt.setInt(2, idAppraiser);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			return this.loadObject(rs);
		}else{
			return null;
		}
	}
	
	public int save(ProposalAppraiser appraiser) throws SQLException{
		boolean insert = (appraiser.getIdProposalAppraiser() == 0);
		PreparedStatement stmt;
		
		if(insert){
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("INSERT INTO proposalappraiser(idProposal, idAppraiser, feedback, comments, allowEditing) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}else{
			stmt = ConnectionDAO.getInstance().getConnection().prepareStatement("UPDATE proposalappraiser SET idProposal=?, idAppraiser=?, feedback=?, comments=?, allowEditing=? WHERE idProposalAppraiser=?");
		}
		
		stmt.setInt(1, appraiser.getProposal().getIdProposal());
		stmt.setInt(2, appraiser.getAppraiser().getIdUser());
		stmt.setInt(3, appraiser.getFeedback().getValue());
		stmt.setString(4, appraiser.getComments());
		stmt.setInt(5, (appraiser.isAllowEditing() ? 1 : 0));
		
		if(!insert){
			stmt.setInt(6, appraiser.getIdProposalAppraiser());
		}
		
		stmt.execute();
		
		if(insert){
			ResultSet rs = stmt.getGeneratedKeys();
			
			if(rs.next()){
				appraiser.setIdProposalAppraiser(rs.getInt(1));
			}
		}
		
		return appraiser.getIdProposalAppraiser();
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
		
		return p;
	}
	
}
