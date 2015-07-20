package odesk.oDesk.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Ni on 05.11.2014.
 */
@JsonIgnoreProperties(value = {"paging"})
public class Jobs {
    @JsonProperty("jobs")
    public JobModel[] jobs;
    @JsonProperty("profile_access")
    public String profileAccess;
    @JsonProperty("auth_user")
    public AuthUser user;
    @JsonProperty("server_time")
    public String time;
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for(JobModel job: jobs){
          sb.append(job.toString());
      }
        return sb.toString();
    }
}

class AuthUser{
    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("timezone")
    String timezone;
    @JsonProperty("last_name")
    String lastName;
    @JsonProperty("timezone_offset")
    String timezoneOffset;
}

