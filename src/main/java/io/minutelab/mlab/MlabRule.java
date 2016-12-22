package io.minutelab.mlab;

import java.io.IOException;
import java.net.URL;
import org.junit.rules.ExternalResource;

/** A JUnit rule that run a minuteLab lab for the duration of a test or a test suite.
 *
 * @see <a href="https://github.com/junit-team/junit/wiki/Rules">Junit Rules</a>
 */
public class MlabRule extends ExternalResource {
    
    public Lab lab;

    /**
     * @param args minute lab script and args
     * @see Lab#Lab
     */
    public MlabRule(String... args) {
        lab = new Lab(args);
    }
     /**
      * @param  resource minute lab script in form of inner resource
     * @param args minute lab run arguments
     * @see Lab#Lab
     */
    public MlabRule(URL resource,String... args){
      lab = new Lab(resource, args);
    }
    @Override
    protected void before() throws IOException,InterruptedException {
        lab.start();
        lab.log("info:","IP-",lab.getIP(),"port-",""+lab.getPort(),"container ID-",lab.getConID());
    }

    @Override
    protected void after() {
        lab.close();
    }
}
