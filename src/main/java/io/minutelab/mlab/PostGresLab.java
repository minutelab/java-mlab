package io.minutelab.mlab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author user
 */
public class PostGresLab extends Lab  {
    public PostGresLab(String schema){
        super(PostGresLab.class.getResource("/postgres.mlab"),"-schema", schema,"-port","0");
    }
    
    public String getDBURL() throws MLabException{
        boolean remote = isRemoteProcess();
        String ip = remote?  getIP() : "localhost";
        int port = remote? 5432 : getPort(5432) ;
        if (ip == "" || port < 0){
            throw new MLabException("failed obtainning DB URL");
        }
        return "jdbc:postgresql://"+ip+":"+port+"/postgres";   
    }
    public Connection getConnection(String user, String password)throws MLabException,InterruptedException{
        String address = getDBURL();
        try {
            return DriverManager.getConnection(address, user, password);
        } catch (SQLException ex) {
            throw new MLabException("could not get connection on "+address+": "+ex.getMessage());
        }
    }
    
    public Connection getConnection()throws MLabException,InterruptedException{
        return getConnection("postgres","");
    }
}
