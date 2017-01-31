package io.minutelab.mlab;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by user on 31-Jan-17.
 */
public class TestRuleUrlBased {
    @Rule
    public MlabRule mlab = new MlabRule(Lab.class.getResource("/postgres.mlab"),
            "-schema",
            ResourcePrepare.filename(this,"/test.sql"),"-port","0");

    @Test
    public void testDBStoreAndFetch() throws Exception {
        Integer port = mlab.lab.getPort();
        System.out.println("testDB using port "+port);
        Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:"+port+"/postgres", "postgres", "");
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT VERSION()");

        if (rs.next()) {
            System.out.println("Database version: " + rs.getString(1));
        } else {
            fail("Could not find version");
        }
        st.executeUpdate("INSERT INTO TEST values (2,'test2')");
        rs = st.executeQuery("SELECT val FROM test where id = 2");
        if (!rs.next()) {
            fail("Could not find data in database");
        }
        assertEquals("Should get the right value",rs.getString(1),"test2");
    }
}
