package net.minutelab.mlab;

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
 * Lab
 *
 * Start a minuteLab lab: Start the lab and wait for it to be ready.
 *
 */
public class Lab implements Closeable
{
    private List<String> args;
    private Process p;

    public Lab(String... args) {
        this.args = new ArrayList<String>();
        this.args.add("mlab");
        this.args.add("run");
        this.args.add("-w");

        this.args.addAll(Arrays.asList(args));
    }

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

    public void close() throws IOException {
        if ( p==null ) {
            return;
        }
        p.getOutputStream().close();
        waitFor(p,50);
    }

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
