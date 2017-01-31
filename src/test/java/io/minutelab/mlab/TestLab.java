package io.minutelab.mlab;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class TestLab {

   @Test
   public void testLab() throws IOException,InterruptedException {
       System.out.println("Start testLab test");
       String script=ResourcePrepare.filename(this,"/tst.mlab");
       Lab lab = new Lab(script);

       try {
           assertFalse("Lab was not started",lab.isAlive());
           lab.start();
           assertTrue("Lab was not started",lab.isAlive());
       } finally {
           System.out.println("network specs: "+lab.getIP()+"  "+lab.getPort(80));
            lab.close();
       }
       assertFalse("Lab was closed",lab.isAlive());
   }

    @Test
    public void testLabAsResource() throws IOException,InterruptedException {
        System.out.println("Start testLab test");
      // String script=ResourcePrepare.filename(this,"/tst.mlab");
        Lab lab = new Lab(this.getClass().getResource("/tst.mlab"));

        try {
            assertFalse("Lab was not started",lab.isAlive());
            lab.start();
            assertTrue("Lab was not started",lab.isAlive());
        } finally {
            System.out.println("network specs: "+lab.getIP()+"  "+lab.getPort(80));
            lab.close();
        }
        assertFalse("Lab was closed",lab.isAlive());
    }

   @Test
   public void testFailedLab() throws IOException,InterruptedException {
       System.out.println("Start failedLab test");

       Lab lab = new Lab(ResourcePrepare.filename(this,"/tst.mlab"),"-fail");

       try {
           assertFalse("Lab was not started",lab.isAlive());
           lab.start();
           fail("Lab should have failed");
       } catch (IOException ex) {
           System.out.println("OK:lab failed: "+ex);
       } finally {
           lab.close();
       }
       assertFalse("Lab was closed",lab.isAlive());
   }

}
