package io.minutelab.mlab;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by user on 31-Jan-17.
 */
public class TestRuleWithFolder {

    @Test
    public void testLabWithFolder() throws IOException,InterruptedException {
        System.out.println("Start testLabWithFolder test");
        Lab lab = new Lab(Lab.class.getResource("/files"));

        try {
            assertFalse("Lab was not started",lab.isAlive());
            lab.start();
            assertTrue("Lab was not started",lab.isAlive());
        } finally {
            System.out.println("Will close lab");
            lab.close();
        }
        assertFalse("Lab was closed",lab.isAlive());
    }
}
