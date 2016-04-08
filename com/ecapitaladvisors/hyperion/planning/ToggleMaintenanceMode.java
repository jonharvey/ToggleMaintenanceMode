 /***********************************************
 *  Author: Jon Harvey                          *
 *          eCapital Advisors, LLC.             *
 *          jharvey@ecapitaladvisors.com        *
 *  Date:     04/06/10                          *
 *  Modification log:                           *
 *          09/03/10, Jon Harvey                *
 *          Name of cookie given by Planning    *
 *          post-authentication has changed     *
 *          when Planning was patched to the    *
 *          latest version (9.3.1.13)           *
 *                                              *
 *          03/14/12, Jon Harvey, v11 Upgrade   *
 *          Updated for v11.1.2.1               *
 ***********************************************/

package com.ecapitaladvisors.hyperion.planning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class ToggleMaintenanceMode {
    
    // Member variables to store decoded 
    // arguments passed in at runtime
    private static String _app = null;
    private static String _user = null;
    private static String _password = null;
    private static String _server = null;
    private static int _mode = -1;
    private static String _oracleSchema = null;
    private static String _oraclePW = null;
    private static String _oracleServer = null;
    private static String _oracleSID = null;
    private static int _oraclePort = -1;
    private static String _propertiesFile = null;
    private static boolean _auth = false;
    private static int returnCode = 0;
    
    // JAR entry point - main program functionality
    public static void main(String[] args) throws Exception {

        // Verify that all necessary variables have
        // been passed in and formatted correctly
        if(ValidateArgs(args)){
            
            try{
                // Create a web client object
                DefaultHttpClient httpclient = new DefaultHttpClient();
    
                // Build the POST string for the HspLogOn servlet
                String content = "Application=" + _app + 
                                 "&Username=" + _user + 
                                 "&isMig=" + "N" + 
                                 "&Password=" + _password +
                                 "&Server=" + "localhost" +
                                 "&Browser=" + "unknown" + 
                                 "&validate=" + "true" + 
                                 "&isWorkspace=" + "false" +
                                 "&Redirect=";
                
                // Create a GET request pointed to the HspLogOn servlet
                HttpGet httpget = new HttpGet("http://" + _server + ":8300/HyperionPlanning/servlet/HspLogOn?" + content);
        
                // Invoke the servlet to validate the session
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    entity.consumeContent();
                }
                
                // Build a list of cookies that were assigned to us during 
                // session with the HspLogOn servlet.  This is necessary
                // in order to retrieve the cookie containing our authenticated JSESSIONID
                List <NameValuePair> nvps = new ArrayList <NameValuePair>();
                List <Cookie> cookies = httpclient.getCookieStore().getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                    
                    // Check for the ORA_HP_MRUApplication cookie - this cookie
                    // only appears if authentication was successful.
                    // If the cookie exists, set the _auth flag = true
                    if(cookies.get(i).getName().equalsIgnoreCase("ORA_HP_MRUAPPLICATION"))
                        _auth = true;
                    
                    // Add the cookie to the NVP array
                    nvps.add(new BasicNameValuePair(cookies.get(i).getName(), cookies.get(i).getValue()));
                }
                
                // Check to see if authentication was successful,
                // exit with returnCode = 1 if it wasn't
                if(!_auth){
                    
                       // Set returnCode = 1 so the OS knows this was not a successful run of the app
                    returnCode = 1;
                    System.out.println("\nERROR: Error authenticating with credentials provided in properties file...");
                }
                    
                // Build our POST string for the HspSaveUserPrefs servlet
                AppSettingsBean bean = new AppSettingsBean();
                bean.setPlanningRepositoryServer(_oracleServer);
                bean.setPlanningRepositorySID(_oracleSID);
                bean.setOracleSchema(_oracleSchema);
                bean.setOraclePort(_oraclePort);
                bean.setOraclePassword(_oraclePW);
                bean.setApplication(_app);
                bean.setLoginLevel(_mode);
                bean.loadBean();
                content = bean.toString();
                
                // Create a POST request
                HttpPost httpost = new HttpPost("http://" + _server + ":8300/HyperionPlanning/servlet/HspSaveUserPrefs?" + content);
                
                // Assign our list of cookies to the new POST request
                httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                
                // Invoke the HspSaveUserPrefs servlet to save our options (in the POST string)
                response = httpclient.execute(httpost);
                entity = response.getEntity();
                if (entity != null) {
                    entity.consumeContent();
                }
            }
            catch(SQLException sqle){
                   // Set returnCode = 1 so the OS knows this was not a successful run of the app
                returnCode = 1;
                System.out.println("\nERROR: Errors occurred querying the Oracle repository...");            
            }
            catch(Exception e){
                   // Set returnCode = 1 so the OS knows this was not a successful run of the app
                returnCode = 1;
                System.out.println("\nERROR: Errors were encountered invoking the Planning servlets...");
            }
        }
        else{    
            // If arguments didn't validate, print some help
            CreateTemplate();
            PrintUsage();
        }
        
        // Exit with 0 for success, 1 for error encountered
        System.exit(returnCode);
    }

    // Reads in the arguments passed in at the command line
    // and assigns them to the appropriate variables
    private static boolean ValidateArgs(String[] args){
        try{
            
            // Read in args from the command line and assign local variables
            for(int i=0;i<args.length-1;i=i+2){
                if(args[i].charAt(0) == '-'){
                    switch(args[i].charAt(1)){
                        case 'f':
                            _propertiesFile = args[i+1];
                            break;
                        case 'm':
                            if(args[i+1].equalsIgnoreCase("T"))
                                _mode = 2;
                            else 
                                if(args[i+1].equalsIgnoreCase("F"))
                                    _mode = 0;
                                else
                                    throw new Exception("\nERROR: Bad flag/argument exception...");
                            break;
                        default:
                            throw new Exception("\nERROR: Bad flag/argument exception...");
                    }
                }
                // if a flag or argument is out of order
                else
                    throw new Exception("\nERROR: Bad argument format exception...");
            }

            // Read in variables from properties file
            FileInputStream fstream = new FileInputStream(_propertiesFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            String[] readArgs;
            while((line = br.readLine()) != null){
                
                // skip processing comment lines and blank lines
                if((line.length()!=0)){
                    
                    if(line.trim().charAt(0) != ';'){
                            
                        // Split the NV pair into a string array
                        readArgs = line.split("=");
                        
                        // Each non-comment line should have exactly one equals sign
                        if(readArgs.length != 2){
                            returnCode = 1;
                            throw new Exception("\nERROR: Incorrectly formatted line in properties file...");
                        }
                        else{
    
                            // Assign the appropriate value based on name 
                            if(readArgs[0].trim().equalsIgnoreCase("APPLICATION"))
                                _app = readArgs[1].trim();
                            if(readArgs[0].trim().equalsIgnoreCase("USERNAME"))
                                _user = readArgs[1].trim();
                            if(readArgs[0].trim().equalsIgnoreCase("PASSWORD"))
                                _password = Encryption.EncryptPassword(readArgs[1].trim());
                            if(readArgs[0].trim().equalsIgnoreCase("SERVER"))
                                _server = readArgs[1].trim();
                            if(readArgs[0].trim().equalsIgnoreCase("ORACLESCHEMA"))
                                _oracleSchema = readArgs[1].trim();
                            if(readArgs[0].trim().equalsIgnoreCase("ORACLEPASSWORD"))
                                _oraclePW = readArgs[1].trim();
                            if(readArgs[0].trim().equalsIgnoreCase("ORACLESERVER"))
                                _oracleServer = readArgs[1].trim();
                            if(readArgs[0].trim().equalsIgnoreCase("ORACLESID"))
                                _oracleSID = readArgs[1].trim();
                            if(readArgs[0].trim().equalsIgnoreCase("ORACLEPORT"))
                                _oraclePort = Integer.parseInt(readArgs[1].trim());
                            
                            // Throw an error if an invalid non-comment row is encountered
                            if( (!(readArgs[0].trim().equalsIgnoreCase("APPLICATION"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("USERNAME"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("PASSWORD"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("SERVER"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("ORACLESCHEMA"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("ORACLEPASSWORD"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("ORACLESERVER"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("ORACLESID"))) &&
                                (!(readArgs[0].trim().equalsIgnoreCase("ORACLEPORT")))){
                                returnCode = 1;
                                throw new Exception("\nERROR: A non-comment line was encountered in the properties file with no valid variable assignment...");
                            }                        
                        }
                    }
                }
            }

            // Close the input stream
            br.close();
            in.close();
            fstream.close();
            
            // If any of the required arguments weren't passed, throw an exception
            if((_user == null) || 
               (_password == null) || 
               (_server == null) || 
               (_app == null) || 
               (_mode == -1) || 
               (_oracleSchema == null) ||
               (_oraclePW == null) || 
               (_oracleServer == null) ||
               (_oracleSID == null) || 
               (_oraclePort == -1))
                throw new Exception("\nERROR: Mandatory argument found in properties file...");           
        }

        // Catch the exception before it hits the main processing loop
        // so the function can return false and call PrintUsage()
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        
        // return true on success
        return true;
    }

    // Prints out instructions to the user on how and 
    // what to pass in at the command line
    private static void PrintUsage(){
        System.out.println("\nUSAGE: java -jar ToggleMaintenanceMode.jar [options]\n");
        System.out.println("Flag                Option");      
        System.out.println("===========================================================================================================================");
        System.out.println("-f       (required) Path to the properties file containing arguments");
        System.out.println("-m       (required) \"T\" or \"F\" representing whether or not to put the app in maintenance mode");
        System.out.println("Arguments must be passed in a \"-flag argument\" paired format (ie - not \"ToggleMaintenanceMode.jar -upas arg1 arg2...\")" + System.getProperty("line.separator"));
        System.out.println("");
        System.out.println("Please see the file sample.properties for help on creating a properties file");
    }
    
    // Prints out a file called sample.properties containing
    // definition and samples of fields in the properties file
    private static void CreateTemplate() throws IOException{
        
        // Create a file stream to write to
        FileWriter fstream = new FileWriter("sample.properties");
        BufferedWriter out = new BufferedWriter(fstream);
        
        // Write the sample file
        out.write(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" + System.getProperty("line.separator"));
           out.write(";                                    Sample.properties                                        ;" + System.getProperty("line.separator"));
           out.write(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" + System.getProperty("line.separator"));
           out.write("; AUTHOR: Jon Harvey                                                                          ;" + System.getProperty("line.separator"));
           out.write(";         eCapital Advisors, LLC.                                                             ;" + System.getProperty("line.separator"));
           out.write(";         jharvey@ecapitaladvisors.com                                                        ;" + System.getProperty("line.separator"));
           out.write(";                                                                                             ;" + System.getProperty("line.separator"));
           out.write("; PURPOSE: This is a sample file showing what the properties file should look like            ;" + System.getProperty("line.separator"));
           out.write(";                                                                                             ;" + System.getProperty("line.separator"));
           out.write("; FORMAT RULES:                                                                               ;" + System.getProperty("line.separator"));
           out.write(";   1) Comment lines start with a semicolon (';')                                             ;" + System.getProperty("line.separator"));
           out.write(";   2) All of the arguments listed are required, and all valid arguments are listed below     ;" + System.getProperty("line.separator"));
           out.write(";   3) Whitespace between text and the equals sign ('=') is OK, but not required              ;" + System.getProperty("line.separator"));
           out.write(";   4) Blank lines are OK                                                                     ;" + System.getProperty("line.separator"));
           out.write(";   5) Variables are case sensitive                                                           ;" + System.getProperty("line.separator"));
           out.write(";   6) This file can be named anything and given any extension                                ;" + System.getProperty("line.separator"));
           out.write(";      (as long as correct name is referenced at runtime)                                     ;" + System.getProperty("line.separator"));
           out.write(";                                                                                             ;" + System.getProperty("line.separator"));
           out.write(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; APPLICATION is the name of the Hyperion Planning application to be changed" + System.getProperty("line.separator"));
           out.write("Application = Sample" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; USERNAME is a valid admin username on the specified Hyperion Planning application" + System.getProperty("line.separator"));
           out.write("Username = admin" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; PASSWORD is the password that corresponds with the specified username" + System.getProperty("line.separator"));
           out.write("Password = password" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; SERVER is the Hyperion Planning server URL" + System.getProperty("line.separator")); 
           out.write("; (do not include a leading \"http://\", port specifications like \":8300\" or request extensions like \"/HyperionPlanning\")" + System.getProperty("line.separator"));
           out.write("Server = planning.ecapitaladvisors.com" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; ORACLESCHEMA is the schema name that the Hyperion Planning application is deployed to" + System.getProperty("line.separator"));
           out.write("OracleSchema = SampleApp" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; ORACLEPASSWORD is the password for the schema specified above" + System.getProperty("line.separator"));
           out.write("OraclePassword = SamplePassword" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; ORACLESERVER is the server that the Oracle instance is running on" + System.getProperty("line.separator"));
           out.write("OracleServer = oracledb.ecapitaladvisors.com" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; ORACLESID is the SID identifier of the Oracle instance" + System.getProperty("line.separator"));
           out.write("OracleSID = oradb" + System.getProperty("line.separator"));
           out.write("" + System.getProperty("line.separator"));
           out.write("; ORACLEPORT is the port number the Oracle instance is running on (typical value is 1521)" + System.getProperty("line.separator"));
           out.write("OraclePort = 1521" + System.getProperty("line.separator"));

           // Close the file stream
           out.close();
           fstream.close();
           
           // Set returnCode = 1 so the OS knows this was not a successful run of the app
           returnCode = 1;
    }

}
