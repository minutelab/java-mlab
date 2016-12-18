package io.minutelab.mlab;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
    NetConfig net;
    File temp;

    /**
     * Create a lab object.
     *
     * The lab is not initialized until the {@link #start} method is called.
     *
     * @param  args mlab script and its argument.
     */
    public Lab(String... args) {
        try {
            temp = File.createTempFile("conId", null);
        } catch (IOException ex) {
            System.out.println("failed creating temp file");
            Runtime.getRuntime().exit(2);
        }
        this.args = new ArrayList<String>();
        this.args.add("mlab");
        this.args.add("run");
        this.args.add("-w");
        this.args.add("--id");
        this.args.add(temp.getAbsolutePath());
        this.args.add("-ddebug");
        this.args.add("-Lc:\\share2\\mlab2.log");
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
        try {
            getNetConfig();
        } catch (InterruptedException ex) {
           System.out.println("error getting net config details");
        }
       boolean exited = waitFor(p,50);
        if (exited) {
            String err = IOUtils.toString(p.getErrorStream(),"utf-8");
            throw new IOException("process is dead: , Exit code: " + p.exitValue() + "\n" + err);
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

    private static boolean waitFor(Process p, int w) {
        boolean reason = false;
        try {
           reason = p.waitFor(w, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            // We don't really care
             System.out.println("waitFor interrupted: "+ex);
        }
        return reason;
    }
   
    private void getNetConfig()throws IOException,InterruptedException{
        String conId = new String(Files.readAllBytes(temp.toPath()));
        String[]inspect = new String[]{"mlab","inspect", conId};
        Process cmd = Runtime.getRuntime().exec(inspect);
        int exitCode = cmd.waitFor();
        switch (exitCode){
            case 0:
                net=new NetConfig();
                BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
                String line;
                String all ="";
                while ((line = reader.readLine())!= null) {
                    all+=(line.replace(" ", ""));
                }
                JSONObject network = new JSONObject(all).getJSONObject("config").getJSONObject("Network");
                try{
                    JSONArray exposed = network.getJSONArray("exposed");
                    for (int i = 0;i<exposed.length();i++){
                      JSONObject ex = exposed.getJSONObject(i);
                      setPort(ex.getInt("internal"), ex.getInt("external"));
                  }
                } catch (JSONException ex){
                    System.out.println("no exposed ports ("+ex.getMessage()+")");
                }
                    JSONArray interfaces = network.getJSONArray("interfaces");
                    for (int i = 0;i<interfaces.length();i++){
                        JSONObject in = interfaces.getJSONObject(i);
                        setIP(in.getString("ifname"), in.getString("address"));
                    }
                break;
            case 1:
                if (args.contains("-fail"))
                    System.out.println("lab is down on purpose");
                else
                    System.out.println("mlab inspect returned \"1\" with no known reason");
                break;
            default:
                System.out.println("mlab inspect returned "+exitCode+" with no known reason");
        }
    }
    
    public void setIP(String key, String value){
        net.interfaces.put(key, value);
    }
     
    public void setPort(int internal, int external){
        net.exposedPorts.put(internal, external);
    }
    
    public String getIP(){
        if (net.interfaces.containsKey("eth0"))
            return net.interfaces.get("eth0"); 
        for (String ip:net.interfaces.values())
            return ip;
        return "";
    }
    
    public Integer getPort(int internal){
        if (net.exposedPorts.isEmpty())
            return null;
       return net.exposedPorts.get(internal);
    }
    
    // get the first one
    public Integer getPort(){
        if (net.exposedPorts.isEmpty())
            return null;
       return net.exposedPorts.values().iterator().next();
    }
        
    public void Print() {
        System.out.println(args);
    }
    
    class NetConfig{
        Hashtable <String,String> interfaces = new Hashtable<String, String>();
        Hashtable<Integer,Integer> exposedPorts = new Hashtable<Integer, Integer>();
    }
 }
