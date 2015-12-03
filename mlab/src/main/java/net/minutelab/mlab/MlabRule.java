package net.minutelab.mlab;

import java.io.IOException;
import org.junit.rules.ExternalResource;

public class MlabRule extends ExternalResource {
    private Lab lab;

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
