@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
 
import groovyx.net.http.HTTPBuilder
import groovy.json.JsonOutput
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

/*
 * This method is used to SEND or GET a keptn event
 * The result will always contain two key:value pairs.
 * The first K/V pair is 'result' which is either 'success' or 'fail'.
 * The second K/V pair is 'data' which is either:
 *   - The keptn return data (for 'result: success) or
 *   - The error message (for 'result: fail)
 *
 * Also note that there is a convenience / helper return K/V to judge the Keptn result.
 * This is only returned for the GET calls and only when the call didn't fail.
 * This Key for this K/V is 'keptnResult'. It provides the output of the Keptn evaluation (pass, warning or fail)
 * Note that the raw Keptn data is still returned in the 'data' Key.
 */
@NonCPS
def processEvent( Map args ) {
 
 /* -- Inputs --
   'keptn_url'
   'keptn_api_token'
   'keptn_project' // eg. 'website'
   'keptn_service' // eg. 'front-end'
   'keptn_stage'   // eg. 'quality'
   'keptn_event_type'  // eg. 'sh.keptn.event.start-evaluation'
   'keptn_event_method' // "SEND" or "GET"
   'keptn_context' // Optional for "SEND" (context is returned by send). Mandatory for "GET".
   'start_time' // Format: "2020-03-20T11:36:31"
   'end_time' // Format: "2020-03-20T11:36:31"
   'timeframe' // Minimum is 2m (2 minutes)
   'debug_mode' // boolean
   'timeout' // timeout in seconds. Default = 30s
    
   -- Variables --
   String strKeptnURL;
   String strKeptnAPIToken;
   String strKeptnProject;
   String strKeptnService;
   String strKeptnStage;
   String strKeptnEventType;
   String strKeptnEventMethod;
   String strKeptnContext;
   String strStartTime;
   String strEndTime;
   String strTimeframe;
   int iTimeout;
   boolean bDebug;
 */
 
 String strKeptnURL = args.containsKey("keptn_url") ? args.keptn_url : "${KEPTN_URL}";
 String strKeptnAPIToken = args.containsKey("keptn_api_token") ? args.keptn_api_token : "${KEPTN_API_TOKEN}";
 String strKeptnProject = args.containsKey("keptn_project") ? args.keptn_project : "${KEPTN_PROJECT}";
 String strKeptnService = args.containsKey("keptn_service") ? args.keptn_service : "${KEPTN_SERVICE}";
 String strKeptnStage = args.containsKey("keptn_stage") ? args.keptn_stage : "${KEPTN_STAGE}";
 String strKeptnEventType = args.containsKey("keptn_event_type") ? args.keptn_event_type : "";
 String strKeptnEventMethod = args.containsKey("keptn_event_method") ? args.keptn_event_method : "";
 String strKeptnContext = args.containsKey("keptn_context") ? args.keptn_context : "";
 String strStartTime = args.containsKey("start_time") ? args.start_time : "${START_TIME}";
 String strEndTime = args.containsKey("end_time") ? args.end_time : "${END_TIME}";
 String strTimeframe = args.containsKey("timeframe") ? args.timeframe : "${TIMEFRAME}";
 int iTimeout = args.containsKey("timeout") ? args.timeout : 30; // Default timeout is 30 seconds
 boolean bDebug = args.containsKey("debug_mode") ? args.debug_mode : false;
 
 echo "[dt_processEvent.groovy] Debug Mode: " + bDebug;
 
 if (bDebug) {
   echo "[dt_processEvent.groovy] Keptn URL is: " + strKeptnURL;
   echo "[dt_processEvent.groovy] Keptn API Token is: " + strKeptnAPIToken;
   echo "[dt_processEvent.groovy] Keptn Project is: " + strKeptnProject;
   echo "[dt_processEvent.groovy] Keptn Service is: " + strKeptnService;
   echo "[dt_processEvent.groovy] Keptn Stage is: " + strKeptnStage;
   echo "[dt_processEvent.groovy] Keptn Event Type is: " + strKeptnEventType;
   echo "[dt_processEvent.groovy] Keptn Event Method is: " + strKeptnEventMethod;
   echo "[dt_processEvent.groovy] Keptn Context is: " + strKeptnContext;
   echo "[dt_processEvent.groovy] Start Time is: " + strStartTime;
   echo "[dt_processEvent.groovy] End Time is: " + strEndTime;
   echo "[dt_processEvent.groovy] Timeframe is: " + strTimeframe;
   echo "[dt_processEvent.groovy] Timeout is: " + strTimeframe;
 }
 
 // TODO - Error Checking
 
  def returnValue = "";
  def http = new HTTPBuilder( strKeptnURL + '/v1/event' );
  if (bDebug) http.ignoreSSLIssues();

 //----------------------------------
 //-------- SEND KEPTN EVENT --------
 //----------------------------------
 
  if ("SEND" == strKeptnEventMethod) {
   try {
    http.request( POST, JSON ) { req ->
      headers.'x-token' = strKeptnAPIToken
      headers.'Content-Type' = 'application/json'
      body = [
        type: strKeptnEventType,
        source: "Pipeline",
        data: [
            start: strStartTime,
            end: strEndTime,
            project: strKeptnProject,
            service: strKeptnService,
            stage: strKeptnStage,
            teststrategy: "manual"
          ]
      ]
     response.success = { resp, json ->
      if (bDebug) echo "[dt_processEvent.groovy] Success: ${json} ++ Keptn Context: ${json.keptnContext}";
       returnValue = [[key: 'result', value: 'fail'], [key: 'data', value: json.keptnContext]];
     }
    
     response.failure = { resp, json ->
       println "Failure: ${resp} ++ ${json}";
       if (bDebug) echo "[dt_processEvent.groovy] Setting returnValue to: 'ERROR: SEND KEPTN EVENT FAILED'";
       returnValue = [[key: 'result', value: 'fail'], [key: 'data', value: 'ERROR: SEND KEPTN EVENT FAILED']];
     }
    }
   }
    catch (Exception e) {
      echo "[dt_processEvent.groovy] SEND EVENT: Exception caught: " + e.getMessage();
      returnValue = [[key: 'result', value: 'fail'], [key: 'data', value: 'ERROR: ' + e.getMessage() ]];
    }
  } // End if "SEND" Keptn Event
 
 //---------------------------------
 //-------- GET KEPTN EVENT --------
 //---------------------------------
 
  if ("GET" == strKeptnEventMethod) {  
   try {

     int iIterationCount = 1;
     /* Max iterations is timeout in seconds + 1.
      * If it wasn't +1 we'd sleep and not test the final time.
      */
     int iMaxIterations = (int) (iTimeout / 10) + 1;
     echo "Max Iterations = " + iMaxIterations;
    
     if (bDebug) echo "[dt_processEvent.groovy] Keptn Context: " + strKeptnContext;
    
     while (iIterationCount < iMaxIterations) {
      echo "Iteration Count: " + iIterationCount;
     
     http.request( GET, JSON ) {
     
      uri.query = [ keptnContext: strKeptnContext, type: strKeptnEventType ]
      headers.'x-token' = strKeptnAPIToken;
      headers.'Content-Type' = 'application/json';
      
      response.success = { resp, json ->
       if (bDebug) echo "[dt_processEvent.groovy] Success: ${json}";
       
       /* An HTTP 500 code is given if we're still waiting for the keptn Event.
        * If we have a valid return code, set the iteration count to something arbitrarily high so we break out of the loop and return values to the user.
        */
       if (json.code != 500) iIterationCount = 10000;
       
       returnValue = [ [key: 'result', value: 'success'], [key: 'data', value: json.toString() ], [key: 'keptnResult', value: "${json.data.result}"]];
      }
    
      response.failure = { resp, json ->
        if (bDebug) echo "[dt_processEvent.groovy] Setting returnValue to: ${json}";
        returnValue = [[key: 'result', value: 'fail'], [key: 'data', value: 'ERROR: ' + json ]];
       }
      } // end http GET
      
      // If, at this point, we have a valid result iIterationCount will be 10000, so if we're still waiting for the keptn event, sleep for 10s then retry.
      if (iIterationCount >= iMaxIterations) {
        echo "[dt_processEvent.groovy] Got a valid result or reached the timeout. Returning to pipeline...";
      }
      else if (iIterationCount < iMaxIterations) {
       echo "[dt_processEvent.groovy] Still waiting for keptn event. Script will sleep for 10s then try again";
       Thread.sleep(10000); // Sleep for 10s before retrying the http GET
      }
      
      iIterationCount++;
    } // end while loop
   } // End try
   catch (Exception e) {
     echo "[dt_processEvent.groovy] GET EVENT: Exception caught: " + e.getMessage();
     returnValue = [[key: 'result', value: 'fail'], [key: 'data', value: e.getMessage()]];
   }
  } // End if "GET" Keptn Event
 
  return returnValue;
}
