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
 
 String strKeptnURL = args.containsKey("strKeptnURL") ? args.strKeptnURL : "${KEPTN_URL}"
 // TODO - Others...
 
 echo "[dt_sendEvent.groovy] Keptn URL is: " + strKeptnURL;
}
