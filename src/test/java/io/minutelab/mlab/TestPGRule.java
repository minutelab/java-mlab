package io.minutelab.mlab;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPGRule {
    @Rule
    public MlabRule pgMlab = new MlabRule(new PostGresLab(ResourcePrepare.filename(this,"/test.sql")));


    @Test
    public void testDBPgMlab() throws Exception {
        Connection con = ((PostGresLab)pgMlab.lab).getConnection("mlab","1234");
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

    @Test
    public void testDBPgMlabDefaultUser() throws Exception {
        Connection con = ((PostGresLab)pgMlab.lab).getConnection();
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
