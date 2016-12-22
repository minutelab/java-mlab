package io.minutelab.mlab;

import java.io.IOException;
import io.minutelab.mlab.Lab;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;


public class TestLab {
//
//    @Test
//    public void testLab() throws IOException {
//        System.out.println("Start testLab test");
//        String script=Resource.filename(this,"/tst.mlab");
//        Lab lab = new Lab(script);
// 
//        try {
//            assertFalse("Lab was not started",lab.isAlive());
//            lab.start();
//            assertTrue("Lab was not started",lab.isAlive());
//        } finally {
//            System.out.println("network specs: "+lab.getIP()+"  "+lab.getPort(80));
//             lab.close();
//        }
//        assertFalse("Lab was closed",lab.isAlive());
//    }
//
//    @Test
//    public void testFailedLab() throws IOException {
//        System.out.println("Start failedLab test");
//
//        Lab lab = new Lab(Resource.filename(this,"/tst.mlab"),"-fail");
//
//        try {
//            assertFalse("Lab was not started",lab.isAlive());
//            lab.start();
//            fail("Lab should have failed");
//        } catch (IOException ex) {
//            System.out.println("OK:lab failed: "+ex);
//        } finally {
//            lab.close();
//        }
//        assertFalse("Lab was closed",lab.isAlive());
//    }

}
