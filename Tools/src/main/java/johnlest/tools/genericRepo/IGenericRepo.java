package johnlest.tools.genericRepo;

import java.sql.SQLException;
import java.util.List;

public interface IGenericRepo {
    /**
     * Get Row by ID 
     * @param id primary Key value
     * @return objects list
     * @throws SQLException
     */
    public List<Object> GetByID(int id) throws SQLException;
    
    /**
     * Get all table
     * @return Objects table
     * @throws SQLException
     */
    public List<List<Object>> GetAll() throws SQLException;
   
    /**
     * Get all table with condition
     * @param where Condition 
     * @return Objects table
     * @throws SQLException
     */
    public List<List<Object>> GetAllWhere(String where) throws SQLException;
    
    /**
     * Get some column 
     * @param elem Columns list
     * @return Objects table
     * @throws SQLException
     */
    public List<List<Object>> GetColumn(List<String> elem) throws SQLException;
   
    /**
     * Get some columns with condition
     * @param elem Columns list
     * @param where condition 
     * @return Objects tables
     * @throws SQLException
     */
    public List<List<Object>> GetColumn(List<String> elem, String where) throws SQLException;
    
    /**
     * Get the first row with condition
     * @param where Condition
     * @return Objexts List
     * @throws SQLException
     */
    public List<Object> GetFirst(String where) throws SQLException;
    
    /**
     * Get some elements in the first row with condition
     * @param elem Column list tu search
     * @param where Condition
     * @return Object List
     * @throws SQLException
     */
    public List<Object> GetFirst(List<String> elem, String where) throws SQLException;
    
    /**
     * Count number of row in table
     * @return Number
     * @throws SQLException
     */
    public int Count() throws SQLException;
    
    /**
     * Count number of row in table with condition 
     * @param where Condition
     * @return Number
     * @throws SQLException
     */
    public int Count(String where) throws SQLException;
}

