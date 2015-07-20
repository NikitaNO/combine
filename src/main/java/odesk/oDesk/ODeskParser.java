//package odesk.oDesk;
//
//import bootstrap.liftweb.ParseSession;
//import com.oDesk.api.*;
//import com.oDesk.api.Routers.Auth;
//import com.oDesk.api.Routers.Jobs.Search;
//import com.oDesk.api.Routers.Mc;
//import com.oDesk.api.Routers.Organization.Users;
//import com.oDesk.api.Routers.Reports.Time;
//import net.liftweb.common.Logger;
//import odesk.oDesk.model.JobModel;
//import odesk.oDesk.model.Jobs;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.net.URLDecoder;
//import java.util.HashMap;
//import java.util.Properties;
//import java.util.Scanner;
//
////-------------------------not used now--------------------
//public class ODeskParser  {
//    public String token = "";
//    public OAuthClient oauClient(){
//        Properties props = new Properties();
//        try {
//            props.load(new FileInputStream(new File("C:\\Users\\jojer_000\\Desktop\\java-odesk-master\\example\\odesk.properties")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Config conf = new Config(props);
//        OAuthClient client = new OAuthClient(conf);
//        return client;
//    }
//
//    public OAuthClient client = oauClient();
//
//
//    public void getJobs(int days) {
//        String aToken = null;
//        String aSecret = null;
//        // authorize application and get access token
//        if (aToken == null && aSecret == null) {
//            Scanner scanner = new Scanner(System.in);
//            String authzUrl = client.getAuthorizationUrl();
//            System.out.println(authzUrl);
//            System.out.println("1. Copy paste the following url in your browser : ");
//            System.out.println(authzUrl);
//            System.out.println("2. Grant access ");
//            System.out.println("3. Copy paste the oauth_verifier parameter here :");
//
//            String oauth_verifier = scanner.nextLine();
//
//            String verifier = null;
//            try {
//                verifier = URLDecoder.decode(oauth_verifier, "UTF-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            HashMap<String, String> token = client.getAccessTokenSet(verifier);
//
//            scanner.close();
//            System.out.println(token);
//        } else {
//            // set known access token-secret pair
//            client.setTokenWithSecret(aToken, aSecret);
//        }
//        Jobs jobsList = null;
//        try {
//            Search jobs = new Search(client);
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("q", "python");
//            params.put("skills", "python");
//            params.put("budget", "-1000");
////            params.put("days_posted", "days");
//            JSONObject jobsJSON = jobs.find(params);
//            ObjectMapper om = new ObjectMapper();
//            try {
//                jobsList = om.readValue(jobsJSON.toString(), Jobs.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        System.out.println(jobsList.jobs[0]);
//    }
//
//     public JobModel[] withToken(String OAuthToken, int days) {
//
//        String verifier = null;
////        try {
////            verifier = URLDecoder.decode(OAuthToken, "UTF-8");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        HashMap<String, String> token = client.getAccessTokenSet(verifier);
//////         client.setTokenWithSecret(token., aSecret);
////        System.out.println(token);
//        // set known access token-secret pair
//         String aToken = "07f56e211c76c48a02545b1d0ab1f9a9";
//         String aSecret = "af04a28b81ab2874";
//         if(ParseSession.firstParsing()) {
//             ParseSession.setFirstParsing(false);
//             client.setTokenWithSecret(aToken, aSecret);
//         }
//        Jobs jobsList = null;
//        try {
//            Auth auth = new Auth(client);
//            Search jobs = new Search(client);
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("skills", "python");
//            params.put("category", "Web Development");
////            params.put("budget", "-1000");
////            params.put("days_posted","" + days);
//            JSONObject jobsJSON = jobs.find(params);
//            ObjectMapper om = new ObjectMapper();
//            try {
//                jobsList = om.readValue(jobsJSON.toString(), Jobs.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//         catch (Exception e){
//             e.printStackTrace();
//         }
//        return jobsList.jobs;
//    }
//}
//
