package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;
//Em primeiro momento foi constatado que o código abaixo, este bem detalhado,
//fácil entendimento e visualização, porem há muitas validações para fechamento de conexão com banco de dados, então visto isso
//encontrei uma outra maneira para fazer, utilizando try-with-resources
import br.edu.utfpr.dv.siacoes.model.Department;


//código corrigido, criado classe public para sair da conexão, foi uma forma que encontrei para correção do problema
public class CloseException extends Exception {
    public CloseException(Throwable e) {
        super(e);
    }
}    

public class ActivityUnitDAO {
	
	
	
	public List<ActivityUnit> listAll() throws SQLException{
		try(
            Connection conn = ConnectionDAO.getInstance().getConnection();
		    PreparedStatement stmt = (PreparedStatement) conn.createStatement();
			){
		
		try (ResultSet rs = stmt.executeQuery("SELECT * FROM activityunit ORDER BY description")){
			
			List<ActivityUnit> list = new ArrayList<ActivityUnit>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
		
			
			return list;
		}//ao inves de usar o finally ou mesmo chamar o método close(), para fechamento de recursos, 
			 //usamos apenas catch (SQLException e)
		 catch (SQLException e) {
                     throw new CloseException(e);
		}
		
		}
	

	//criado classe privada chamando a consulta do activity e ao gerar a conexão utilizamos a variavel criada
         private static final String SQLAct = 
            "SELECT * FROM activityunit WHERE idActivityUnit=?";
	
	public ActivityUnit findById(int id) throws SQLException, CloseException{
		try (
		      Connection conn = ConnectionDAO.getInstance().getConnection();
		      PreparedStatement stmt = conn.prepareStatement(SQLAct);
	    ){
		stmt.setInt(1, id);
		try (ResultSet rs = stmt.executeQuery()){
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}}
		}//ao inves de usar o finally ou mesmo chamar o método close(), para fechamento de recursos, 
			 //usamos apenas catch (SQLException e)
		 catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	public int save(int idUser, ActivityUnit unit) throws SQLException, CloseException{
		boolean insert = (unit.getIdActivityUnit() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?");
			}
			
			stmt.setString(1, unit.getDescription());
			stmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
			stmt.setString(3, unit.getAmountDescription());
			
			if(!insert){
				stmt.setInt(4, unit.getIdActivityUnit());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					unit.setIdActivityUnit(rs.getInt(1));
				}
				
				new UpdateEvent(conn).registerInsert(idUser, unit);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, unit);
			}
			
			return unit.getIdActivityUnit();
		}//ao inves de usar o finally ou mesmo chamar o método close(), para fechamento de recursos, 
			 //usamos apenas catch (SQLException e)
		 catch (SQLException e) {
                     throw new CloseException(e);
		}
	}
	
	private ActivityUnit loadObject(ResultSet rs) throws SQLException{
		ActivityUnit unit = new ActivityUnit();
		
		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));
		
		return unit;
	}

}
