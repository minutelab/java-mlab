package io.minutelab.mlab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import io.minutelab.mlab.MlabRule;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;

public class TestRule {
    @Rule
    public MlabRule mlab = new MlabRule(Resource.filename(this,"/postgres.mlab"),
                                        "-schema",
                                        Resource.filename(this,"/test.sql"));

    @Test
    public void testDB() throws Exception {
        Connection con = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "");
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT VERSION()");

        if (rs.next()) {
            System.out.println("Database version: " + rs.getString(1));
        } else {
            fail("Could not find version");
        }

        rs = st.executeQuery("SELECT val FROM test where id = 1");
        if (!rs.next()) {
            fail("Could not find data in database");
        }
        assertEquals("Should get the right value",rs.getString(1),"test");
    }
}
