package io.minutelab.mlab;

import java.io.IOException;
import org.junit.rules.ExternalResource;

/** A JUnit rule that run a minuteLab lab for the duration of a test or a test suite.
 *
 * @see <a href="https://github.com/junit-team/junit/wiki/Rules">Junit Rules</a>
 */
public class MlabRule extends ExternalResource {
    private Lab lab;

    /**
     * @param args mintue lab script and args
     * @see Lab#Lab
     */
    public MlabRule(String... args) {
        lab = new Lab(args);
    }

    @Override
    protected void before() throws IOException {
        lab.start();
    }

    @Override
    protected void after() {
        try {
            lab.close();
        } catch (IOException e) {
            System.out.println("Error closing lab: "+e);
        }
    }
}