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
//    @Rule
//    public PostGresLab pgMlab = new PostGresLab(Resource.filename(this,"/test.sql"));
//       
//    @Rule
//    public MlabRule mlab = new MlabRule(Resource.filename(this,"/postgres.mlab"),
//                                        "-schema",
//                                        Resource.filename(this,"/test.sql"),"-port","0");
//
//    @Test
//    public void testDB() throws Exception {
//        Integer port = mlab.getPort();
//        System.out.println("testDB using port "+port);
//        Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:"+port+"/postgres", "postgres", "");
//        Statement st = con.createStatement();
//        ResultSet rs = st.executeQuery("SELECT VERSION()");
//
//        if (rs.next()) {
//            System.out.println("Database version: " + rs.getString(1));
//        } else {
//            fail("Could not find version");
//        }
//
//        rs = st.executeQuery("SELECT val FROM test where id = 1");
//        if (!rs.next()) {
//            fail("Could not find data in database");
//        }
//        assertEquals("Should get the right value",rs.getString(1),"test");
//    }
//    
//    @Test
//    public void testDBPgMlab() throws Exception {
//        Connection con = pgMlab.getConnection("mlab","1234");
//        Statement st = con.createStatement();
//        ResultSet rs = st.executeQuery("SELECT VERSION()");
//
//        if (rs.next()) {
//            System.out.println("Database version: " + rs.getString(1));
//        } else {
//            fail("Could not find version");
//        }
//
//        rs = st.executeQuery("SELECT val FROM test where id = 1");
//        if (!rs.next()) {
//            fail("Could not find data in database");
//        }
//        assertEquals("Should get the right value",rs.getString(1),"test");
//    }
//    
//    @Test
//    public void testDBPgMlabDefaultUser() throws Exception {
//        Connection con = pgMlab.getConnection();
//        Statement st = con.createStatement();
//        ResultSet rs = st.executeQuery("SELECT VERSION()");
//
//        if (rs.next()) {
//            System.out.println("Database version: " + rs.getString(1));
//        } else {
//            fail("Could not find version");
//        }
//
//        rs = st.executeQuery("SELECT val FROM test where id = 1");
//        if (!rs.next()) {
//            fail("Could not find data in database");
//        }
//        assertEquals("Should get the right value",rs.getString(1),"test");
//    }
}
