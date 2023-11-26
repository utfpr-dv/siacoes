package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem;
import br.edu.utfpr.dv.siacoes.model.EvaluationItem.EvaluationItemType;

public class EvaluationItemDAO {
	
	private Connection conn;
	
	public EvaluationItemDAO() throws SQLException{
		this.conn = ConnectionDAO.getInstance().getConnection();
	}
	
	public EvaluationItemDAO(Connection conn) throws SQLException{
		if(conn == null){
			this.conn = ConnectionDAO.getInstance().getConnection();	
		}else{
			this.conn = conn;
		}
	}
	
	public EvaluationItem findById(int id) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT * FROM evaluationitem WHERE idEvaluationItem = ?");
		
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
	
	public boolean hasScores(int idEvaluationItem) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = this.conn.prepareStatement("SELECT COUNT(*) as total FROM juryappraiserscore WHERE idEvaluationItem=?");
		
			stmt.setInt(1, idEvaluationItem);
			
			rs = stmt.executeQuery();
			
			rs.next();
			return (rs.getInt("total") > 0);
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	public List<EvaluationItem> listByFormat(int idFormat, boolean onlyActives) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM evaluationitem WHERE idThesisFormat=" + String.valueOf(idFormat) + (onlyActives ? " AND active = 1 " : "") + " ORDER BY stage, type, sequence");
			List<EvaluationItem> list = new ArrayList<EvaluationItem>();
			
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
	
	public List<EvaluationItem> listByStage(int stage, int idFormat, boolean onlyActives) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = this.conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM evaluationitem WHERE idThesisFormat=" + String.valueOf(idFormat) + " AND stage = " + String.valueOf(stage) + (onlyActives ? " AND active = 1 " : "") + " ORDER BY type, sequence");
			List<EvaluationItem> list = new ArrayList<EvaluationItem>();
			
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
	
	public int save(int idUser, EvaluationItem item) throws SQLException{
		boolean insert = (item.getIdEvaluationItem() == 0);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			if(!insert && this.hasScores(item.getIdEvaluationItem())){
				stmt = this.conn.prepareStatement("UPDATE evaluationitem SET active=? WHERE idEvaluationItem=?");
				
				stmt.setInt(1, (item.isActive() ? 1 : 0));
				stmt.setInt(2, item.getIdEvaluationItem());
				
				stmt.execute();
				
				new UpdateEvent(this.conn).registerUpdate(idUser, item);
			}else{
				if(insert){
					stmt = this.conn.prepareStatement("INSERT INTO evaluationitem(idThesisFormat, description, ponderosity, stage, active, sequence, type) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					
					Statement stmt2 = this.conn.createStatement();
					rs = stmt2.executeQuery("SELECT COUNT(*) as total FROM evaluationitem WHERE idThesisFormat=" + String.valueOf(item.getFormat().getIdThesisFormat()) + " AND stage = " + String.valueOf(item.getStage()));
					rs.next();
					item.setSequence(rs.getInt("total") + 1);
					rs.close();
					stmt2.close();
				}else{
					stmt = this.conn.prepareStatement("UPDATE evaluationitem SET idThesisFormat=?, description=?, ponderosity=?, stage=?, active=?, sequence=?, type=? WHERE idEvaluationItem=?");
				}
				
				stmt.setInt(1, item.getFormat().getIdThesisFormat());
				stmt.setString(2, item.getDescription());
				stmt.setDouble(3, item.getPonderosity());
				stmt.setInt(4, item.getStage());
				stmt.setInt(5, (item.isActive() ? 1 : 0));
				stmt.setInt(6, item.getSequence());
				stmt.setInt(7, item.getType().getValue());
				
				if(!insert){
					stmt.setInt(8, item.getIdEvaluationItem());
				}
				
				stmt.execute();
				
				if(insert){
					rs = stmt.getGeneratedKeys();
					
					if(rs.next()){
						item.setIdEvaluationItem(rs.getInt(1));
					}

					new UpdateEvent(this.conn).registerInsert(idUser, item);
				} else {
					new UpdateEvent(this.conn).registerUpdate(idUser, item);
				}
			}
			
			return item.getIdEvaluationItem();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}
	
	private EvaluationItem loadObject(ResultSet rs) throws SQLException{
		EvaluationItem item = new EvaluationItem();
		
		item.setIdEvaluationItem(rs.getInt("idEvaluationItem"));
		item.getFormat().setIdThesisFormat(rs.getInt("idThesisFormat"));
		item.setDescription(rs.getString("description"));
		item.setPonderosity(rs.getDouble("ponderosity"));
		item.setStage(rs.getInt("stage"));
		item.setActive(rs.getInt("active") == 1);
		item.setSequence(rs.getInt("sequence"));
		item.setType(EvaluationItemType.valueOf(rs.getInt("type")));
		
		return item;
	}
	
	public boolean delete(int idUser, int id) throws SQLException{
		Statement stmt = null;
		EvaluationItem item = this.findById(id);
		
		try{
			stmt = this.conn.createStatement();
		
			boolean ret = stmt.execute("DELETE FROM evaluationitem WHERE idEvaluationItem = " + String.valueOf(id));
			
			new UpdateEvent(conn).registerDelete(idUser, item);
			
			return ret;
		}finally{
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
		}
	}

}
