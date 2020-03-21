@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7' )
 
import groovyx.net.http.HTTPBuilder
import groovy.json.JsonOutput
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

/***************************\
  This function assumes we run on a Jenkins Agent that has curl command available.
  Returns either 0(=no errors), 1(=create/update management zone failed)
\***************************/
@NonCPS
def dt_sendEvent( Map args ) {

 /*
   String strKeptnURL;
   String strKeptnAPIToken;
   String strKeptnProject;
   String strKeptnService;
   String strKeptnStage;
   String strKeptnEventType;
   String strStartTime;
   String strEndTime;
   String strTimeframe;
 */
 
 String strKeptnURL = args.containsKey("strKeptnURL") ? args.strKeptnURL : "DEFAULT";
 String strKeptnAPIToken = args.containsKey("strKeptnAPIToken") ? args.strKeptnURL : "${KEPTN_API_TOKEN}";
 String strKeptnProject = args.containsKey("strKeptnURL") ? args.strKeptnURL : "${KEPTN_PROJECT}";
 String strKeptnService = args.containsKey("strKeptnURL") ? args.strKeptnURL : "${KEPTN_SERVICE}";
 String strKeptnStage = args.containsKey("strKeptnURL") ? args.strKeptnURL : "${KEPTN_STAGE}";
 String strKeptnEventType = args.containsKey("strKeptnURL") ? args.strKeptnURL : "";
 String strStartTime = args.containsKey("strKeptnURL") ? args.strKeptnURL : "";
 String strEndTime = args.containsKey("strKeptnURL") ? args.strKeptnURL : "";
 String strTimeframe = args.containsKey("strKeptnURL") ? args.strKeptnURL : "";
 boolean bDebug = args.containsKey("debug") ? args.strKeptnURL : false;
 
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
 
}
