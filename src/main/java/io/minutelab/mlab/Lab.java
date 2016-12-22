package io.minutelab.mlab;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Shape;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Start a minuteLab lab
 *
 * Lab wraps a minuteLab lab, handling starting the lab,
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
    private File conID;
    private String ID="";
    private ProcessBuilder pb;
          

    /**
     * Create a lab object.
     *
     * The lab is not initialized until the {@link #start} method is called.
     *
     * @param args mlab script and its arguments.
     */
    public Lab(String... args) {
      init(args);        
    }
     /**
     * Create a lab object.
     *
     * The lab is not initialized until the {@link #start} method is called.
     *
     * @param resource accepts an mlab script retrieved with myObj.getClass().getResource("/file.mlab") @param args other arguments.
     */
    public Lab(URL resource,String...args){
         String script = null;
        
        try{
        script = pathFromURL(resource);
        }catch(IOException ex){
            ex.printStackTrace();
            return;
        }
        if (args.length < 1){
            init(script);
            return;
        }
      
        String[] newArgs = new String[args.length+1];
        for (int i = args.length-1;i >= 0;i--){
            newArgs[i+1] = args[i];
        }
        newArgs[0] = script;
     
        init(newArgs);
    }
  
    private void init(String... args){
         try {
            conID = File.createTempFile("conId", null);
           // conID.deleteOnExit();
        } catch (IOException ex) {
            log("failed creating temp file for container ID",ex.getMessage());
        }
        this.args = new ArrayList<String>();
        this.args.add("mlab");
        this.args.add("run");
        this.args.add("-w");
        this.args.add("--id");
        this.args.add(conID.getAbsolutePath());
        //this.args.add("-ddebug");
        //this.args.add("-Lc:\\share2\\mlab2.log");
        this.args.addAll(Arrays.asList(args));
        this.net = new NetConfig();
        log("initialized");
    }
    
    @Override
    protected void finalize() throws Throwable {
       this.close();
       super.finalize();
    }
    /**
     * Start a lab and wait for it to be ready
     *
     * The method blocks until the lab is detached or exits.
     *
     * If the lab could not be started or exits without detaching, it throws an IOException
     */
    public void start() throws IOException,InterruptedException {
        log("starting lab");
        pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        Process p = pb.start();
       
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        try{
            while ((line = in.readLine()) != null) 
                System.out.println(">: "+ line);
        } catch(Exception ex){
            ex.printStackTrace();
        }
        
        boolean exited = waitFor(p,50);
        if (exited) {
            String err = IOUtils.toString(p.getErrorStream(),"utf-8");
            log("failed starting",err);
            throw new IOException("process is dead: , Exit code: " + p.exitValue() + "\n" + err);
        }
        
        getNetConfig();
        log("lab is ready");
        this.p = p;
    }

    /**
     * Clean up the lab (if it is still running)
     */
    public void close() {
        log("closing lab");
        if (conID != null){
            conID.delete();
        }
        if ( p==null ) {
            log("in close lab process is null");
            return;
        }
        
        try {
            p.getOutputStream().close();
        } catch (IOException ex) {
           ex.printStackTrace();
        }
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
   
    private boolean isDir(URL path){
        return new File(path.getFile()).isDirectory();
    }
    
    private String pathFromURL (URL resource)throws IOException{
       
        String res = resource.getPath();
        String separator = res.lastIndexOf("/")>res.lastIndexOf("\\") ? "/" : "\\";
        String shortName = res.substring(res.lastIndexOf(separator)+1);
        String mlabScript = null;
        
        File tmp = File.createTempFile("xxx", null);
        String dir = tmp.getAbsolutePath().substring(0,tmp.getAbsolutePath().lastIndexOf("xxx"));
        separator = dir.lastIndexOf("/")>dir.lastIndexOf("\\") ? "/" : "\\";
        tmp.delete();
        if (isDir(resource)){
          mlabScript = prepareFiles(dir,res,separator);
        } else if (res.endsWith(".mlab")){
             log("this is the script path" ,dir+shortName);
             mlabScript = dir+shortName;
             InputStream in = resource.openStream();
             Files.copy(in, Paths.get(mlabScript), StandardCopyOption.REPLACE_EXISTING);
             in.close();
        }
        else{
            throw new IllegalArgumentException("no mlab script found");
        }
        return mlabScript;
    }
    
    private String prepareFiles(String dir, String res, String separator)throws IOException{
        File script = null;
        log("prepare Files in", dir + "folder");
        Path dirPath = Files.createDirectories(Paths.get(dir+"folder"));
        dirPath.toFile().deleteOnExit();
        
        File src;
        Path trgt;
        InputStream is = null;
        File[] files = new File(res).listFiles();
        for (int i = 0; i <  files.length;i++){
           src = files[i];
           trgt = Paths.get(dirPath+separator+src.getName());
           is = new FileInputStream(src.getAbsolutePath());
           log("file path",dirPath + separator + src.getName());
           Files.copy(is ,trgt, StandardCopyOption.REPLACE_EXISTING);
           if (src.getName().endsWith(".mlab")){
               script = trgt.toFile();
            }
        }
        if (is != null){
            is.close();
        }
        return script.getAbsolutePath();
    }
    
    private void getNetConfig()throws IOException,InterruptedException{
        BufferedReader reader = new BufferedReader(new FileReader(conID.getAbsolutePath()));
        int byteChar = 0;
        while((byteChar = reader.read())!= -1){
            ID += (char)byteChar;
        }
        String[]inspect = new String[]{"mlab","inspect", ID};
        ProcessBuilder pb = new ProcessBuilder(inspect).redirectErrorStream(true);
        Process cmd = pb.start();
        reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
        int exitCode = cmd.waitFor();
        switch (exitCode){
            case 0:
                net=new NetConfig();
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
                    log(reader.readLine().replace("mlab: ", "mlab inspect "));
                break;
            default:
                log("mlab inspect returned "+exitCode+" with no known reason");
        }
    }
    
    private void setIP(String key, String value){
        net.interfaces.put(key, value);
    }
     
    private void setPort(int internal, int external){
        net.exposedPorts.put(internal, external);
    }
    /**
     * @return the IP address of the lab, or an empty string if no IP address is listed for this lab
     */
    public String getIP(){
        if (net.interfaces.containsKey("eth0"))
            return net.interfaces.get("eth0"); 
        for (String ip:net.interfaces.values())
            return ip;
        return "";
    }
    
     /**
     * @return the exposed port corresponding with the given internal or -1 if there are no registered ports or a non-existing internal port was supplied.
     * @param internal the internal port to which the exposed port is redirected.
     */
    public int getPort(int internal){
        if (net.exposedPorts.isEmpty())
            return -1;
        Integer ext = net.exposedPorts.get(internal);
       return ext == null ? -1 : ext;
    }
    
    /**
     * @return the first exposed port if exists or  -1 if there are no registered ports.
     */
    public int getPort(){
       if (net.exposedPorts.isEmpty())
            return -1;
       return net.exposedPorts.values().iterator().next();
    }
    
    boolean isRemoteProcess(){
       Map <String,String> env = pb.environment();
       String val = env.get("MLAB_HOST");
       return (val != null && val.startsWith("unix:"));
    }
        
    /**
     * Print outputs all the arguments used to run the lab, mainly for debugging purposes.
     */
    public void Print() {
        System.out.println(args);
    }
     /**
     * @return the ID of this running lab as stored in the hosting machine.
     */
    public String getConID(){
        return this.ID;
    }
      
    void log(String... args){
        System.out.print("mlab.Lab -> ");
        for(int i = 0 ; i<args.length;i++)
            System.out.print(args[i]+" ");
        System.out.println();
    }
    
    class NetConfig{
        Hashtable <String,String> interfaces = new Hashtable<String, String>();
        Hashtable<Integer,Integer> exposedPorts = new Hashtable<Integer, Integer>();
    }
       
    public class MLabException extends Exception{
        public MLabException(String specs){
            super("mlab error: "+specs);
        }
        public MLabException(Exception ex){
            this(ex.getMessage());
        }
    }
 }
