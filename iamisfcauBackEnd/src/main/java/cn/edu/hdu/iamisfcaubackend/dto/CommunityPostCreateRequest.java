package cn.edu.hdu.iamisfcaubackend.dto;

import java.util.List;

public class CommunityPostCreateRequest {

    private String content;
    private String type;
    private String relatedActivityId;
    private List<String> images;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedActivityId() {
        return relatedActivityId;
    }

    public void setRelatedActivityId(String relatedActivityId) {
        this.relatedActivityId = relatedActivityId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}