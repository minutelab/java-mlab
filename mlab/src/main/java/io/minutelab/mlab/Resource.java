package io.minutelab.mlab;

import java.io.File;
import java.lang.Object;
import java.lang.IllegalArgumentException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;

/** Utility class to ease converting a file name out of a resource name
 *
 * just calling obj.getClass().getResource(resource).getFile()
 * does not work on windows (it add an extra slash, and has problems with spaces)
 */
public class Resource
{
    public static String filename(Object obj, String resource)  {
        URL url = obj.getClass().getResource(resource);
        URI uri;
        try {
            uri=url.toURI();
        } catch (java.net.URISyntaxException e) {
            throw new IllegalArgumentException("could not get resource: "+url);
        }
        File file = new File(uri.getPath());
        return file.getAbsolutePath();
    }
}
