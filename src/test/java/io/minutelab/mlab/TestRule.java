package io.minutelab.mlab;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestRule {

   @Rule
   public MlabRule mlab = new MlabRule(ResourcePrepare.filename(this,"/tst.mlab"));

   @Test
   public void testLab() throws Exception {
       System.out.println("Starting test (the lab should be running)");
   }
}
