package cn.edu.hdu.iamisfcaubackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
public class NoticeEntity {

    @Id
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "JSON")
    private String tags; // 先用 String 接住 ["top","normal"]

    @Column(name = "notice_time")
    private LocalDateTime noticeTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getNoticeTime() { return noticeTime; }
    public void setNoticeTime(LocalDateTime noticeTime) { this.noticeTime = noticeTime; }
}