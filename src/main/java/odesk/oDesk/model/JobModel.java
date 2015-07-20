package odesk.oDesk.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Ni on 05.11.2014.
 */
public class JobModel {
    @JsonProperty("skills")
    public String[] skills;
    @JsonProperty("budget")
    public String budjet;
    @JsonProperty("workload")
    public String workload;
    @JsonProperty("client")
    public Client client;
    @JsonProperty("date_created")
    public String date;
    @JsonProperty("snippet")
    public String snippet;
    @JsonProperty("job_status")
    public String jobStaatus;
    @JsonProperty("url")
    public String url;
    @JsonProperty("id")
    public String id;
    @JsonProperty("title")
    public String title;
    @JsonProperty("category")
    public String category;
    @JsonProperty("duration")
    public String duration;
    @JsonProperty("job_type")
    public String jobType;
    @JsonProperty("subcategory")
    public String subCategory;

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(title + "\n");
        result.append(url + "\n");
        result.append("Skills:\n");
        for (String s : skills) {
            if (result.length() + s.length() <= 139) {
                result.append(s + "\n");
            }
        }
        return result.toString();
    }
}

class Client {
    @JsonProperty("feedback")
    public int feedback;
    @JsonProperty("reviews_count")
    public int reviewsCount;
    @JsonProperty("payment_verification_status")
    public String payment_verification;
    @JsonProperty("jobs_posted")
    public int jobsPoster;
    @JsonProperty("past_hires")
    public int pastHires;
    @JsonProperty("country")
    public String country;
}
