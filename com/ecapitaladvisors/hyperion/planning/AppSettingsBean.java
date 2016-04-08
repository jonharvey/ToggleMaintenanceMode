 /***********************************************
 *  Author: Jon Harvey                          *
 *          eCapital Advisors, LLC.             *
 *          jharvey@ecapitaladvisors.com        *
 *  Date:     04/06/10                          *
 ***********************************************/

package com.ecapitaladvisors.hyperion.planning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppSettingsBean {

    private static String _Action = "Save";
    private static String _SettingMode = "1";
    private static String _selectSettings = "1";
    private String _EmailServer = "";
    private String _emailCharset = "0";
    private String _HubServer = "";
    private String _usefullname = "0";
    private static String _subsVarDisplayOpt = "1";
    private String _makeAppOwner = "";
    private String _CurrentApplicationOwnerId = "";
    private String _tlId0 = "-1";
    private String _tlDesc0 = "";
    private String _tlName0 = "";
    private String _tlURL0 = "";
    private String _tlUserType0 = "0";
    private String _tlId1 = "-1";
    private String _tlDesc1 = "";
    private String _tlName1 = "";
    private String _tlURL1 = "";
    private String _tlUserType1 = "0";
    private String _tlId2 = "-1";
    private String _tlDesc2 = "";
    private String _tlName2 = "";
    private String _tlURL2 = "";
    private String _tlUserType2 = "0";
    private String _tlId3 = "-1";
    private String _tlDesc3 = "";
    private String _tlName3 = "";
    private String _tlURL3 = "";
    private String _tlUserType3 = "0";
    private String _tlId4 = "-1";
    private String _tlDesc4 = "";
    private String _tlName4 = "";
    private String _tlURL4 = "";
    private String _tlUserType4 = "0";
    private String _tlId5 = "-1";
    private String _tlDesc5 = "";
    private String _tlName5 = "";
    private String _tlURL5 = "";
    private String _tlUserType5 = "0";
    private String _tlId6 = "-1";
    private String _tlDesc6 = "";
    private String _tlName6 = "";
    private String _tlURL6 = "";
    private String _tlUserType6 = "0";
    private String _tlId7 = "-1";
    private String _tlDesc7 = "";
    private String _tlName7 = "";
    private String _tlURL7 = "";
    private String _tlUserType7 = "0";
    private String _tlId8 = "-1";
    private String _tlDesc8 = "";
    private String _tlName8 = "";
    private String _tlURL8 = "";
    private String _tlUserType8 = "0";
    private String _tlId9 = "-1";
    private String _tlDesc9 = "";
    private String _tlName9 = "";
    private String _tlURL9 = "";
    private String _tlUserType9 = "0";
    private static String _SavePreferencesBtn = "Save";
    private static String _ResetBtn = "Reset";

    // Properties needed for connecting to the Planning 
    // repository (currently only Oracle DB supported)
    private String _planningRepositoryServer;
    private String _planningRepositorySID;
    private int _oraclePort = 1521;
    private String _oracleSchema;
    private String _oraclePW;
    
    // Other properties to be loaded from calling class
    private String _loginLevel = "0";
    private String _Application = null;
    
    // SET functions for property variables
    public void setPlanningRepositoryServer(String server){
        this._planningRepositoryServer = server;
    }
    public void setPlanningRepositorySID(String SID){
        this._planningRepositorySID = SID;
    }
    public void setOraclePort(int port){
        this._oraclePort = port;
    }
    public void setOracleSchema(String schema){
        this._oracleSchema = schema;
    }
    public void setOraclePassword(String pw){
        this._oraclePW = pw;
    }
    public void setLoginLevel(int newLevel){
        this._loginLevel = Integer.toString(newLevel);
    }
    public void setApplication(String app){
        this._Application = app;
    }
    
    // Loads existing values from Planning repository
    public void loadBean() throws SQLException{
        String m_query = "SELECT " +
                             "EMAIL_SERVER, " +
                             "EMAIL_CHARSET, " + 
                             "EIE_SERVER, " +
                             "USEFULLNAME " +
                         "FROM " + _oracleSchema + ".HSP_SYSTEMCFG";

        // Build the Oracle DB connection with the Oracle JDBC driver for the 1.6 JRE
        DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
        Connection m_connection = DriverManager.getConnection("jdbc:oracle:thin:@" + _planningRepositoryServer + ":" + _oraclePort + ":" + _planningRepositorySID, _oracleSchema, _oraclePW);

        // Build and execute the query object
        Statement m_stmt = m_connection.createStatement();
        ResultSet m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._EmailServer = m_rs.getString("EMAIL_SERVER");
            if(this._EmailServer==null)
                this._EmailServer = "";
            this._emailCharset = Integer.toString(m_rs.getInt("EMAIL_CHARSET"));
            this._HubServer = m_rs.getString("EIE_SERVER");
            if(this._HubServer==null)
                this._HubServer = "";
            this._usefullname = Integer.toString(m_rs.getInt("USEFULLNAME"));
        }
        
        // Dump the recordset
        m_stmt.close();

        // Build the query to return get the app owner's HSP_OBJECT.OBJECT_ID
        m_query = "SELECT a.USER_ID " + 
                  "FROM " + _oracleSchema + ".HSP_USER_PREFS a " +
                  "JOIN " + _oracleSchema + ".HSP_USERS b ON a.USER_ID = b.USER_ID " + 
                  "WHERE b.ROLE = 3";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._makeAppOwner = Integer.toString(m_rs.getInt("USER_ID"));
            this._CurrentApplicationOwnerId = Integer.toString(m_rs.getInt("USER_ID"));
        }        

        // Dump the recordset
        m_stmt.close();

        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 1";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId0 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc0 = m_rs.getString("LINK_DESC");
            this._tlName0 = m_rs.getString("LINK_NAME");
            this._tlURL0 = m_rs.getString("LINK_URL");
            this._tlUserType0 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();
        
        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 2";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId1 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc1 = m_rs.getString("LINK_DESC");
            this._tlName1 = m_rs.getString("LINK_NAME");
            this._tlURL1 = m_rs.getString("LINK_URL");
            this._tlUserType1 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();
            
        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 3";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId2 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc2 = m_rs.getString("LINK_DESC");
            this._tlName2 = m_rs.getString("LINK_NAME");
            this._tlURL2 = m_rs.getString("LINK_URL");
            this._tlUserType2 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();

        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 4";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);
        
        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId3 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc3 = m_rs.getString("LINK_DESC");
            this._tlName3 = m_rs.getString("LINK_NAME");
            this._tlURL3 = m_rs.getString("LINK_URL");
            this._tlUserType3 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();

        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 5";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId4 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc4 = m_rs.getString("LINK_DESC");
            this._tlName4 = m_rs.getString("LINK_NAME");
            this._tlURL4 = m_rs.getString("LINK_URL");
            this._tlUserType4 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();
        
        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 6";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId5 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc5 = m_rs.getString("LINK_DESC");
            this._tlName5 = m_rs.getString("LINK_NAME");
            this._tlURL5 = m_rs.getString("LINK_URL");
            this._tlUserType5 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();
        
        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 7";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId6 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc6 = m_rs.getString("LINK_DESC");
            this._tlName6 = m_rs.getString("LINK_NAME");
            this._tlURL6 = m_rs.getString("LINK_URL");
            this._tlUserType6 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();
        
        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 8";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId7 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc7 = m_rs.getString("LINK_DESC");
            this._tlName7 = m_rs.getString("LINK_NAME");
            this._tlURL7 = m_rs.getString("LINK_URL");
            this._tlUserType7 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();

        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 9";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);
        
        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId8 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc8 = m_rs.getString("LINK_DESC");
            this._tlName8 = m_rs.getString("LINK_NAME");
            this._tlURL8 = m_rs.getString("LINK_URL");
            this._tlUserType8 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();

        // Build the query to grab one of the links from the HSP_LINKS table
        m_query = "SELECT * " +
                  "FROM " + _oracleSchema + ".HSP_LINKS " +
                  "WHERE LINK_ID = 10";
        m_stmt = m_connection.createStatement();
        m_rs = m_stmt.executeQuery(m_query);

        // Assign the values we retrieved to local variables
        while(m_rs.next()){
            this._tlId9 = Integer.toString(m_rs.getInt("LINK_ID"));
            this._tlDesc9 = m_rs.getString("LINK_DESC");
            this._tlName9 = m_rs.getString("LINK_NAME");
            this._tlURL9 = m_rs.getString("LINK_URL");
            this._tlUserType9 = Integer.toString(m_rs.getInt("USER_TYPE"));
        }        

        // Dump the recordset
        m_stmt.close();

        // Dump the connection
        m_connection.close();
    }
    
    // Takes all of the bean data and returns it in NV pair format
    public String toString(){
        String content = "Action=" + _Action + 
        "&SettingMode=" + _SettingMode + 
        "&Application=" + _Application + 
        "&selectSettings=" + _selectSettings +     
        "&EmailServer=" + _EmailServer +             
        "&emailCharset=" + _emailCharset +          
        "&HubServer=" + _HubServer +
        "&usefullname=" + _usefullname +
        "&loginLevel="+ _loginLevel + 
        "&subsVarDisplayOpt=" + _subsVarDisplayOpt + 
        "&makeAppOwner=" + _makeAppOwner +
        "&CurrentApplicationOwnerId="+ _CurrentApplicationOwnerId + 
        "&tlId0=" + _tlId0 +
        "&tlDesc0=" + _tlDesc0 +
        "&tlName0="+ _tlName0 +
        "&tlURL0="+ _tlURL0 + 
        "&tlUserType0="+ _tlUserType0 +
        "&tlId1="+ _tlId1 + 
        "&tlDesc1=" + _tlDesc1 + 
        "&tlName1=" + _tlName1 + 
        "&tlURL1=" + _tlURL1 + 
        "&tlUserType1="+ _tlUserType1 +
        "&tlId2="+ _tlId2 +
        "&tlDesc2=" + _tlDesc2 + 
        "&tlName2=" + _tlName2 + 
        "&tlURL2=" + _tlURL2 + 
        "&tlUserType2="+ _tlUserType2 + 
        "&tlId3=" + _tlId3 + 
        "&tlDesc3=" + _tlDesc3 + 
        "&tlName3="+ _tlName3 + 
        "&tlURL3=" + _tlURL3 + 
        "&tlUserType3="+ _tlUserType3 + 
        "&tlId4=" + _tlId4 + 
        "&tlDesc4="+ _tlDesc4 +
        "&tlName4="+ _tlName4 + 
        "&tlURL4="+ _tlURL4 + 
        "&tlUserType4=" + _tlUserType4 + 
        "&tlId5=" + _tlId5 + 
        "&tlDesc5="+ _tlDesc5 +
        "&tlName5="+ _tlName5 + 
        "&tlURL5="+ _tlURL5 + 
        "&tlUserType5=" + _tlUserType5 + 
        "&tlId6=" + _tlId6 + 
        "&tlDesc6="+ _tlDesc6 +
        "&tlName6="+ _tlName6 + 
        "&tlURL6="+ _tlURL6 + 
        "&tlUserType6=" + _tlUserType6 + 
        "&tlId7=" + _tlId7 + 
        "&tlDesc7="+ _tlDesc7 +
        "&tlName7="+ _tlName7 + 
        "&tlURL7="+ _tlURL7 + 
        "&tlUserType7=" + _tlUserType7 + 
        "&tlId8=" + _tlId8 + 
        "&tlDesc8="+ _tlDesc8 +
        "&tlName8="+ _tlName8 + 
        "&tlURL8="+ _tlURL8 + 
        "&tlUserType8=" + _tlUserType8 + 
        "&tlId9=" + _tlId9 + 
        "&tlDesc9="+ _tlDesc9 +
        "&tlName9="+ _tlName9 + 
        "&tlURL9="+ _tlURL9 + 
        "&tlUserType9=" + _tlUserType9 + 
        "&SavePreferencesBtn=" + _SavePreferencesBtn + 
        "&ResetBtn=" + _ResetBtn;
        return content;
    }

}
