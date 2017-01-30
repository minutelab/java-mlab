package io.minutelab.mlab;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/** Utility class to ease converting a file name out of a resource name
 *
 * just calling obj.getClass().getResource(resource).getFile()
 * does not work on windows (it add an extra slash, and has problems with spaces)
 */
public class ResourcePrepare
{
    //static File tmp;
    public static String filename(Object obj, String resource) {
        try {
            //create temp file to store the mlab script content in order to hand over to mlab
            return doFileName(obj,resource);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    private static String doFileName(Object obj, String resource)throws Exception{
        String seperator = resource.lastIndexOf("/")>resource.lastIndexOf("\\")?"/":"\\";
        String shortName = resource.substring(resource.lastIndexOf(seperator)+1);
        File tmp = File.createTempFile("xxx", null);
        String dir = tmp.getAbsolutePath().substring(0,tmp.getAbsolutePath().lastIndexOf("xxx"));
        File f = new File(dir+shortName);
        f.createNewFile();
        
        URL url = obj.getClass().getResource(resource);
        if (url == null){
            System.out.println("url is null" + resource);
            return "";
        }
        
        Files.copy(url.openStream(), f.toPath(),StandardCopyOption.REPLACE_EXISTING);
        return f.getAbsolutePath();
    }
}
