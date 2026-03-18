package cn.edu.hdu.iamisfcaubackend.dto;

public class ActivityJoinResponse {

    private Integer activityId;
    private String userId;
    private Boolean joined;
    private String message;

    public ActivityJoinResponse() {
    }

    public ActivityJoinResponse(Integer activityId, String userId, Boolean joined, String message) {
        this.activityId = activityId;
        this.userId = userId;
        this.joined = joined;
        this.message = message;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getJoined() {
        return joined;
    }

    public void setJoined(Boolean joined) {
        this.joined = joined;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}