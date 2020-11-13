package johnlest.tools.genericRepo;

import java.sql.*;
import java.util.*;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import johnlest.tools.*;

/**
 * GenericRepo
 */
public class GenericRepo implements IGenericRepo {

    protected String table;
    protected Connection connection;
    protected Class bean;
    private QueryRunner qRunner;

    /**
     * Constructor
     * 
     * @param connection
     */
    public GenericRepo(String table, Connection connection) {
        this.table = table;
        this.connection = connection;
        this.qRunner = new QueryRunner();
    }

    /**
     * Constructor
     * 
     * @param table
     * @param bean
     * @param connection
     */
    public GenericRepo(String table, Class bean, Connection connection) {
        this.table = table;
        this.connection = connection;
        this.bean = bean;
        this.qRunner = new QueryRunner();
    }

    // #region Public Methode
    public Object GetByID(int id) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d ;", table, GetPrimaryKey(), id);
        return ResultRowBean(query);
    }

    public List<Object> GetAll() throws SQLException {
        String query = String.format("SELECT * FROM %s ;", table);
        return ResultTable(query);
    }

    public List<Object> GetAllWhere(String where) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s ;", table, where);
        return ResultTable(query);
    }

    public List<Object> GetColumn(List<String> elem) throws SQLException {
        String columnList = ColumnList2String(elem);
        String query = String.format("SELECT %s FROM %s ;", columnList, table);
        return ResultTable(query);
    }

    public List<Object> GetColumn(List<String> elem, String where) throws SQLException {
        String columnList = ColumnList2String(elem);
        String query = String.format("SELECT %s FROM %s WHERE %s ;", columnList, table, where);
        return ResultTable(query);
    }

    public Object GetFirst(String where) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s LIMIT 1 ;", table, where);
        return ResultRowBean(query);
    }

    public Object GetFirst(List<String> elem, String where) throws SQLException {
        String columnList = ColumnList2String(elem);
        String query = String.format("SELECT %s FROM %s WHERE %s LIMIT 1 ;", columnList, table, where);
        return ResultRowBean(query);
    }

    public int Count() throws SQLException {
        String query = String.format("SELECT COUNT (*) FROM %s ;", table);
        Object res = Arrays.asList(ResultRowObject(query)).stream().findFirst();
        return (Tools.isNullOrEmpty(res) ? 0 : (Integer) res);
    }

    public int Count(String where) throws SQLException {
        String query = String.format("SELECT COUNT (*) FROM %s WHERE %s ;", table, where);
        Object res = Arrays.asList(ResultRowObject(query)).stream().findFirst();
        return (Tools.isNullOrEmpty(res) ? 0 : (Integer) res);
    }

    public Object UseStorProc() throws SQLException {
        String query = String.format("CALL %s ;", table);
        return ResultRowObject(query);
    }

    public int Update(Object update) throws SQLException {
        String setStr = Bean2string(update, true);
        String query = String.format("UPDATE %s SET %s ;", table, setStr);
        return ResultUpdateInsert(query);
    }

    public int Update(Object update, String where) throws SQLException {
        String setStr = Bean2string(update, true);
        String query = String.format("UPDATE %s SET %s WHERE %s ;", table, setStr, where);
        return ResultUpdateInsert(query);
    }

    //#endregion

    //#region private Methode
    /**
     * Get name of the primary key 
     * @return PrimaryKey Name
     * @throws SQLException
     */
    private String GetPrimaryKey() throws SQLException {
        String query = String.format("SHOW keys FROM %s WHERE key_name = 'PRIMARY' ;", table);
        ResultSet result = ExecuteQuery(query);
        result.next();
        return result.getString("Column_name");
    }
    /**
     * Convert the column list tu string for the querry
     * @param list
     * @return
     */
    private String ColumnList2String(List<String> list){
        String str = "";
        for (String string : list) {
            str = String.format("%s %s,", str, string);
        }
        return Tools.RemoveLastChar(str, 1);
    }
    /**
     * Convert the Object bean in string for thr INSERT or UPDATE 
     * @param obj the Object is a bean
     * @param isUpdate Bool, the request is a update (true) or insert (false)? 
     * @return String for SQL Request
     */
    private String Bean2string(Object obj, Boolean isUpdate) {
        try {
            Properties prop = BaseBean.toProperties(obj);
            HashMap<String, String> hMap = new HashMap<String, String>((Map) prop);
            hMap.remove("class");
            Set<Map.Entry<String, String>>set = hMap.entrySet();
            if(isUpdate)
                return StringForUpdate(set);
            return StringForInsert(set);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Convert Set<Map> to string for update request
     * @param set
     * @return String 
     */
    private String StringForUpdate(Set<Map.Entry<String, String>> set) {
        String setStr = ""; 
        for (Map.Entry<String, String>m: set) {
            setStr = String.format("%s %s = '%s',",setStr, m.getKey(), m.getValue());
        }
        return Tools.RemoveLastChar(setStr, 1);
    }
    /**
     *  Convert Set<Map> to string for insert request
     * @param set
     * @return String
     */
    private String StringForInsert(Set<Map.Entry<String, String>> set) {
        String setStr = ""; 
        for (Map.Entry<String, String>m: set) {
            setStr = String.format("%s %s = '%s',",setStr, m.getKey(), m.getValue());
        }
        return Tools.RemoveLastChar(setStr, 1);
    }
    /**
     * Execute the query
     * @param query
     * @return Statement
     * @throws SQLException
     */
    private ResultSet ExecuteQuery(String query) throws SQLException{
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }
    /**
     * Return the result to a table
     * @param query
     * @return List<bean>
     * @throws SQLException
     */
    private List<Object> ResultTable(String query) throws SQLException {
        List table = new ArrayList();
        table = (List) qRunner.query(
            connection,
            query, 
            new BeanListHandler(bean)
            );
        return table;
    }
    /**
     * Update or Insert the query
     * @param query SQL Query
     * @return nbr or row impact
     * @throws SQLException
     */
    private int ResultUpdateInsert(String query) throws SQLException {
        return qRunner.update(
            connection, 
            query
            );
    }
    /**
     * Return the result to a list
     * @param query
     * @return bean
     * @throws SQLException
     */
    private Object ResultRowBean(String query) throws SQLException{
        Object row = qRunner.query(
            connection,
            query, 
            new BeanHandler(bean)
            );
        return row;
    }
    /**
     * Return the result to a list
     * @param result
     * @return Objects List
     * @throws SQLException
     */
    private Object ResultRowObject(String query) throws SQLException{
        Object row = qRunner.query(
            connection,
            query, 
            new ArrayHandler()
            );
        return row;
    }

    //#endregion
}