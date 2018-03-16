package bailuyuan.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * <pre>
 * Created with IntelliJ IDEA.
 * User: ly
 * Date: 2017/4/28
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 * </pre>
 *
 * @author ly
 */
public class TDDataServiceImpl implements TDDataService {
    private static final Log LOG = LogFactory.getLog(TDDataServiceImpl.class);

    protected transient JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean initRecode(String mac) {
        try {
            String sql = "SELECT mac FROM td_data_db WHERE mac = ?";

            final List<String> query = jdbcTemplate.query(sql, new Object[]{
                    mac.toLowerCase()}, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return String.valueOf(rowNum);
                }
            });

            if (query.size() == 0) {
                String sb = "INSERT INTO td_data_db (mac, app_info, " +
                        "game_info, person_info, " +
                        "device_info, city_info, " +
                        "consume_info, create_time) " +
                        "VALUES (?, NULL, NULL, NULL, NULL, NULL, NULL, NULL)";

                jdbcTemplate.update(sb,
                        new Object[]{
                                mac.toLowerCase()
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public String getDataInfo(String mac, String type) {

        String result = "";

        try {
            String sql = "SELECT  " + type + " FROM td_data_db WHERE mac = ?";

            final List<String> query = jdbcTemplate.query(sql, new Object[]{
                    mac.toLowerCase()}, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            });

            result = query.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Integer updateInfo(String mac, String type, String value) {

        try {
            String sql = "UPDATE td_data_db SET " + type + " = ? WHERE mac = ?";

            return jdbcTemplate.update(sql, new Object[]{value, mac.toLowerCase()}, new int[]{Types.VARCHAR, Types.VARCHAR});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void main(String[] args) {

    }
}
