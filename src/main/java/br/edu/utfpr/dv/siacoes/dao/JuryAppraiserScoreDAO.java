package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserScore;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;

public class JuryAppraiserScoreDAO {
	
	private Connection conn;
	
	public JuryAppraiserScoreDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public JuryAppraiserScoreDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public int findIdJury(int idJuryAppraiserScore) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try {
			stmt = this.conn.prepareStatement("SELECT juryappraiser.idjury FROM juryappraiser " +
					"INNER JOIN juryappraiserscore ON juryappraiserscore.idjuryappraiser=juryappraiser.idjuryappraiser " +
					"WHERE juryappraiserscore.idjuryappraiserscore=?");
			
			stmt.setInt(1, idJuryAppraiserScore);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("idjury");
			} else {
				return 0;
			}
		} finally {
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public boolean hasScore(int idJury, int idUser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT juryappraiserscore.idJuryAppraiserScore FROM juryappraiserscore INNER JOIN juryappraiser ON juryappraiser.idJuryAppraiser=juryappraiserscore.idJuryAppraiser WHERE idJury=? AND idAppraiser=?");
			
			stmt.setInt(1, idJury);
			stmt.setInt(2, idUser);
			
			rs = stmt.executeQuery();
			
			return rs.next();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<JuryAppraiserScore> listScores(int idJuryAppraiser) throws SQLException{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT juryappraiserscore.*, evaluationitem.description, evaluationitem.ponderosity, evaluationitem.type FROM juryappraiserscore INNER JOIN evaluationitem ON evaluationitem.idEvaluationItem=juryappraiserscore.idEvaluationItem WHERE idJuryAppraiser=? ORDER BY evaluationitem.type, evaluationitem.sequence");
			
			stmt.setInt(1, idJuryAppraiser);
			
			rs = stmt.executeQuery();
			
			List<JuryAppraiserScore> list = new ArrayList<JuryAppraiserScore>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			if(list.size() == 0){
				rs.close();
				stmt.close();
				
				stmt = this.conn.prepareStatement("SELECT project.idProject, thesis.idThesis, proposal.idDepartment " +
						"FROM jury INNER JOIN juryappraiser ON juryappraiser.idJury=jury.idJury " +
						"LEFT JOIN project ON project.idProject=jury.idProject " +
						"LEFT JOIN thesis ON thesis.idThesis=jury.idThesis " +
						"LEFT JOIN project project2 ON project2.idProject=thesis.idProject " +
						"INNER JOIN proposal ON (proposal.idProposal=project.idProposal OR proposal.idProposal=project2.idProposal) " +
						"WHERE idJuryAppraiser=?");
				
				stmt.setInt(1, idJuryAppraiser);
				
				rs = stmt.executeQuery();
				
				int stage = 0;
				int idDepartment = 0;
				
				if(rs.next()){
					idDepartment = rs.getInt("idDepartment");
					if(rs.getInt("idThesis") != 0){
						stage = 2;
					}else if(rs.getInt("idProject") != 0){
						stage = 1;
					}
				}
				
				rs.close();
				stmt.close();
				
				stmt = this.conn.prepareStatement("SELECT 0 as idJuryAppraiserScore, " + String.valueOf(idJuryAppraiser) + " as idJuryAppraiser, 0 as score, evaluationitem.* FROM evaluationitem WHERE active=1 AND stage=? AND idDepartment=? ORDER BY type, sequence");
				
				stmt.setInt(1, stage);
				stmt.setInt(2, idDepartment);
				
				rs = stmt.executeQuery();
				
				while(rs.next()){
					list.add(this.loadObject(rs));
				}
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public int save(int idUser, JuryAppraiserScore score) throws SQLException{
		boolean insert = (score.getIdJuryAppraiserScore() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(insert){
				stmt = this.conn.prepareStatement("INSERT INTO juryappraiserscore(idJuryAppraiser, idEvaluationItem, score) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = this.conn.prepareStatement("UPDATE juryappraiserscore SET idJuryAppraiser=?, idEvaluationItem=?, score=? WHERE idJuryAppraiserScore=?");
			}
			
			stmt.setInt(1, score.getJuryAppraiser().getIdJuryAppraiser());
			stmt.setInt(2, score.getEvaluationItem().getIdEvaluationItem());
			stmt.setDouble(3, score.getScore());
			
			if(!insert){
				stmt.setInt(4, score.getIdJuryAppraiserScore());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					score.setIdJuryAppraiserScore(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, score);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, score);
			}
			
			return score.getIdJuryAppraiserScore();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private JuryAppraiserScore loadObject(ResultSet rs) throws SQLException{
		JuryAppraiserScore score = new JuryAppraiserScore();
		
		score.setIdJuryAppraiserScore(rs.getInt("idJuryAppraiserScore"));
		score.getJuryAppraiser().setIdJuryAppraiser(rs.getInt("idJuryAppraiser"));
		score.getEvaluationItem().setIdEvaluationItem(rs.getInt("idEvaluationItem"));
		score.getEvaluationItem().setDescription(rs.getString("description"));
		score.getEvaluationItem().setPonderosity(rs.getDouble("ponderosity"));
		score.getEvaluationItem().setType(EvaluationItemType.valueOf(rs.getInt("type")));
		score.setScore(rs.getDouble("score"));
		
		return score;
	}

}
