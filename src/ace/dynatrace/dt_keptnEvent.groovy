@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
 
import groovyx.net.http.HTTPBuilder
import groovy.json.JsonOutput
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

/***************************\
  This function assumes we run on a Jenkins Agent that has curl command available.
\***************************/
@NonCPS
def processEvent( Map args ) {
 
 /* -- Inputs --
   'keptn_url'
   'keptn_api_token'
   'keptn_project'
   'keptn_service'
   'keptn_stage'
   'keptn_event_type'  // eg. 'sh.keptn.event.start-evaluation'
   'keptn_event_method' // "SEND" or "GET"
   'start_time'
   'end_time'
   'timeframe'
   'debug_mode'
    
   -- Variables --
   String strKeptnURL;
   String strKeptnAPIToken;
   String strKeptnProject;
   String strKeptnService;
   String strKeptnStage;
   String strKeptnEventType;
   String strKeptnEventMethod;
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
   echo "[dt_sendEvent.groovy] Keptn Event Method is: " + strKeptnEventMethod;
   echo "[dt_processEvent.groovy] Start Time is: " + strStartTime;
   echo "[dt_processEvent.groovy] End Time is: " + strEndTime;
   echo "[dt_processEvent.groovy] Timeframe is: " + strTimeframe;
 }
 
 // TODO - Error Checking
 
  def returnValue = "";
  def http = new HTTPBuilder( strKeptnURL + '/v1/event' );
  http.ignoreSSLIssues(); // TODO - REMOVE?
  
  if ("SEND" == strKeptnEventMethod) {
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
       echo "[dt_processEvent.groovy] Keptn Context: ${env.keptnContext}";
       echo "[dt_processEvent.groovy] Success: ${json} ++ Keptn Context: ${json.keptnContext}";
       echo "[dt_processEvent.groovy] Setting returnValue to: ${json.keptnContext}";
       returnValue = json.keptnContext;
     }
    
     response.failure = { resp, json ->
       println "Failure: ${resp} ++ ${json}";
       echo "[dt_processEvent.groovy] Setting returnValue to: ${json}";
       returnValue = json;
     }
   }
 
  echo "[dt_processEvent.groovy] Returning: ${returnValue}";
  } // End if "SEND" Keptn Event event
 
  if ("GET" == strKeptnEventMethod) {
    echo "[dt_processEvent.groovy] GETting Keptn Event...";
    echo "[dt_processEvent.groovy] Keptn Context: $keptnContext";
    echo "[dt_processEvent.groovy] Keptn Context: " + env.keptnContext;
   /*
    def html = http.get( path : '/search', query : [q:'Groovy'] )
    http.request( GET, JSON ) { req ->
      path = '?keptnContext='
      headers.'x-token' = strKeptnAPIToken
      headers.'Content-Type' = 'application/json'
      
      response.success = { resp, json ->
        echo "[dt_processEvent.groovy] Success: ${json}";
        echo "[dt_processEvent.groovy] Setting returnValue to: ${json}";
        returnValue = json.keptnContext;
      }
    
      response.failure = { resp, json ->
        echo "[dt_processEvent.groovy] Failure: ${resp} ++ ${json}";
        echo "[dt_processEvent.groovy] Setting returnValue to: ${json}";
        returnValue = json;
     }
    }
    */
    returnValue = "doing GET...";
  }
 
  return returnValue;
}
