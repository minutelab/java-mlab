package io.minutelab.mlab;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * Start a minuteLab lab
 *
 * Lav wraps a minuteLab lab, handling starting the lab,
 * waiting for it to be ready, and at the end cleaning it.
 *
 * Normal cleaning will happen with the close method,
 * but even if it is not cleaned, the lab will be cleaned once the calling process is dead.
 */
public class Lab implements Closeable
{
    private List<String> args;
    private Process p;

    /**
     * Create a lab object.
     *
     * The lab is not initialized until the {@link #start} method is called.
     *
     * @param  args mlab script and its argument.
     */
    public Lab(String... args) {
        this.args = new ArrayList<String>();
        this.args.add("mlab");
        this.args.add("run");
        this.args.add("-w");

        this.args.addAll(Arrays.asList(args));
    }

    /**
     * Start a lab and wait for it to be ready
     *
     * The method blocks until the lab is detached or exists.
     *
     * If the lab could not be started or exists withotu detaching, it throws an IOException
     */
    public void start() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(args);
        Process p = pb.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(">: "+ line);
        }

        waitFor(p,50);
        if (!p.isAlive()) {
            String err = IOUtils.toString(p.getErrorStream(),"utf-8");
            throw new IOException("process is dead: " + err + "\nExit code: " + p.exitValue());
        }

        this.p = p;
    }

    /**
     * Clean up the lab (if it is still running)
     */
    public void close() throws IOException {
        if ( p==null ) {
            return;
        }
        p.getOutputStream().close();
        waitFor(p,50);
    }

    /** check if the lab is still running
     *
     * @return true if the Lab is still running
     */
    public boolean isAlive() {
        return p!=null && p.isAlive();
    }

    private static void waitFor(Process p, int w) {
        try {
            p.waitFor(w, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            // We don't really care
            // System.out.println("waitFor interrupted: "+ex);
        }
    }

    public void Print() {
        System.out.println(args);
    }
 }
