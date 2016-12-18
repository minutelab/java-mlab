package io.minutelab.mlab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author user
 */
public class PostGresLab extends MlabRule {
    public PostGresLab(String schema){
        super(Resource.filename(PostGresLab.class,"/postgres.mlab"),"-schema", schema,"-port","0");
    }
    public Connection getConnection(String user, String password)throws MLabException{
        Integer port = getPort();
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:"+port+"/postgres", user, password);
        } catch (SQLException ex) {
            throw new MLabException("MLab - could not get connection:"+ex.getMessage());
        }
    }
    
    public Connection getConnection()throws MLabException{
        Integer port = getPort();
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:"+port+"/postgres", "postgres", "");
        } catch (SQLException ex) {
            throw new MLabException("MLab - could not get connection:"+ex.getMessage());
        }
    }
    
    class MLabException extends Exception{
        public MLabException(String specs){
            super(specs);
        }
    }
}
