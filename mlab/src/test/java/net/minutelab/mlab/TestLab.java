
import java.io.IOException;
import net.minutelab.mlab.Lab;
import static org.junit.Assert.*;
import org.junit.Test;


public class TestLab {

    @Test
    public void testLab() throws IOException {
        System.out.println("Start testLab test");
        String script=this.getClass().getResource("tst.mlab").getPath();
        Lab lab = new Lab(script);

        try {
            assertFalse("Lab was not started",lab.isAlive());
            lab.start();
            assertTrue("Lab was not started",lab.isAlive());
        } finally {
            lab.close();
        }
        assertFalse("Lab was closed",lab.isAlive());
    }

    @Test
    public void testFailedLab() throws IOException {
        System.out.println("Start failedLab test");

        Lab lab = new Lab(this.getClass().getResource("tst.mlab").getPath(),"-fail");

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
