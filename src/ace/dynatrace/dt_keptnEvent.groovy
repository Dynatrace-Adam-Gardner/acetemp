@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
 
import groovyx.net.http.HTTPBuilder
import groovy.json.JsonOutput
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

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
 }
 
 // TODO - Error Checking
 
  def returnValue = "";
  def http = new HTTPBuilder( strKeptnURL + '/v1/event' );
  http.ignoreSSLIssues(); // TODO - REMOVE?

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
      if (bDebug) {
        echo "[dt_processEvent.groovy] Success: ${json} ++ Keptn Context: ${json.keptnContext}";
        echo "[dt_processEvent.groovy] Setting returnValue to: ${json.keptnContext}";
      }
       returnValue = json.keptnContext;
     }
    
     response.failure = { resp, json ->
       println "Failure: ${resp} ++ ${json}";
       if (bDebug) echo "[dt_processEvent.groovy] Setting returnValue to: ${json}";
       returnValue = json;
     }
    }
   }
    catch (Exception e) {
      echo "[dt_processEvent.groovy] SEND EVENT: Exception caught: " + e.getMessage();
      returnValue = e.getMessage();
      return -1;
    }
  if (bDebug) echo "[dt_processEvent.groovy] Returning: ${returnValue}";
  } // End if "SEND" Keptn Event
 
 //---------------------------------
 //-------- GET KEPTN EVENT --------
 //---------------------------------
 
  if ("GET" == strKeptnEventMethod) {  
   try {
    int iCount = 0;
    /* Loop until a valid result is returned.
     * returnValue is empty on first iteration
     */
    echo "[dt_processEvent.groovy] RETURN VALUE: " + returnValue;
    
    if (returnValue = "") echo "[dt_processEvent.groovy] YES IT'S EMPTY";
    else echo "[dt_processEvent.groovy] NO IT'S NOT EMPTY";
    
    while (returnValue = "" || returnValue.contains("500"))
    {
     echo "[dt_processEvent.groovy] GET iteration count: " + iCount;
     
    http.request( GET, JSON ) {
      uri.query = [ keptnContext:strKeptnContext, type: strKeptnEventType ]
      headers.'x-token' = strKeptnAPIToken
      headers.'Content-Type' = 'application/json'
      
      response.success = { resp, json ->
       if (bDebug) {
        echo "[dt_processEvent.groovy] Success: ${json}";
        echo "[dt_processEvent.groovy] Setting returnValue to: ${json}";
       }
        returnValue = json;
      }
    
      response.failure = { resp, json ->
       if (bDebug) {
        echo "[dt_processEvent.groovy] Failure: ${resp} ++ ${json}";
        echo "[dt_processEvent.groovy] Setting returnValue to: ${json}";
       }
        returnValue = json;
       }
      }
     iCount++;
     sleep(10000); // Sleep for 10s before trying again.
    } // End while loop
   } // End try
   catch (Exception e) {
     echo "[dt_processEvent.groovy] GET EVENT: Exception caught: " + e.getMessage();
     returnValue = e.getMessage();
     return -1;
   }
  } // End if "GET" Keptn Event
 
  return returnValue;
}
