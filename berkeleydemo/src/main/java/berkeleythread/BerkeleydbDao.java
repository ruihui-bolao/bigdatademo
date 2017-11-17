package berkeleythread;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/18 14:11
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public interface BerkeleydbDao<T> {

    /**
     * open database
     * */
    public void openConnection(String filePath, String databaseName);

    /**
     * 关闭数据库
     * */
    public void closeConnection();

    /**
     * insert
     * */
    public void save(String name, T t);

    /**
     * delete
     * */
    public void delete(String name);

    /**
     * update
     * */
    public void update(String name, T t);

    /**
     * select
     * */
    public T get(String name);

}
