@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
 
import groovyx.net.http.HTTPBuilder
import groovy.json.JsonOutput
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

/***************************\
  This function assumes we run on a Jenkins Agent that has curl command available.
\***************************/
@NonCPS
def dt_sendEvent( Map args ) {

 /* -- Inputs --
   'keptn_url'
   'keptn_api_token'
   'keptn_project'
   'keptn_service'
   'keptn_stage'
   'keptn_event_type'
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
 String strStartTime = args.containsKey("start_time") ? args.start_time : "${START_TIME}";
 String strEndTime = args.containsKey("end_time") ? args.end_time : "${END_TIME}";
 String strTimeframe = args.containsKey("timeframe") ? args.timeframe : "${TIMEFRAME}";
 boolean bDebug = args.containsKey("debug_mode") ? args.debug_mode : false;
 
 echo "[dt_sendEvent.groovy] Debug Mode: " + bDebug;
 
 if (bDebug) {
   echo "[dt_sendEvent.groovy] Keptn URL is: " + strKeptnURL;
   echo "[dt_sendEvent.groovy] Keptn API Token is: " + strKeptnAPIToken;
   echo "[dt_sendEvent.groovy] Keptn Project is: " + strKeptnProject;
   echo "[dt_sendEvent.groovy] Keptn Service is: " + strKeptnService;
   echo "[dt_sendEvent.groovy] Keptn Stage is: " + strKeptnStage;
   echo "[dt_sendEvent.groovy] Keptn Event Type is: " + strKeptnEventType;
   echo "[dt_sendEvent.groovy] Start Time is: " + strStartTime;
   echo "[dt_sendEvent.groovy] End Time is: " + strEndTime;
   echo "[dt_sendEvent.groovy] Timeframe is: " + strTimeframe;
 }
 
 // TODO - Error Checking
 
  def returnValue = "";
  def http = new HTTPBuilder( strKeptnURL + '/v1/event' );
 
  http.ignoreSSLIssues(); // TODO - REMOVE?
 
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
      echo "[dt_sendEvent.groovy] Keptn Context: ${env.keptnContext}";
      echo "[dt_sendEvent.groovy] Success: ${json} ++ Keptn Context: ${json.keptnContext}";
      echo "[dt_sendEvent.groovy] HERE 1";
      echo "[dt_sendEvent.groovy] Setting returnValue to: ${json}";
      echo "[dt_sendEvent.groovy] HERE 2";
      returnValue = json.keptnContext;
      echo "[dt_sendEvent.groovy] RV: " + returnValue;
    }
    
    response.failure = { resp, json ->
     println "Failure: ${resp} ++ ${json}";
     echo "[dt_sendEvent.groovy] HERE 3";
     echo "[dt_sendEvent.groovy] Setting returnValue to: ${json}";
     echo "[dt_sendEvent.groovy] HERE 4";
     returnValue = json.keptnContext;
    }
  }
  echo "[dt_sendEvent.groovy] HERE 5";
  //echo ${returnValue};
  echo "[dt_sendEvent.groovy] HERE 6";
  //echo $returnValue;
  echo "[dt_sendEvent.groovy] HERE 7";
  //echo returnValue;
  echo "[dt_sendEvent.groovy] HERE 8";
  //echo "${returnValue}";
  echo "[dt_sendEvent.groovy] HERE 9";
  echo "[dt_sendEvent.groovy] Returning: ${returnValue}";
  echo "[dt_sendEvent.groovy] HERE 10";
  return returnValue;
}
