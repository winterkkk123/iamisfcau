package cn.edu.hdu.iamisfcaubackend.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CampusAiToolService {

    private final JdbcTemplate jdbcTemplate;

    public CampusAiToolService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool(
            name = "get_my_profile",
            description = "根据当前用户ID查询用户基本信息。适合回答我是谁、我的学院、我的角色、我的电话等问题。"
    )
    public Map<String, Object> getUserProfile(
            @ToolParam(description = "用户ID") String userId) {

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select id, name, role, gender, department, college, phone
                from users
                where id = ?
                limit 1
                """, userId);

        return rows.isEmpty() ? Map.of("found", false) : rows.get(0);
    }

    @Tool(
            name = "search_activities",
            description = "按关键词查询活动列表。适合回答有哪些活动、最近活动、讲座、竞赛、时间地点等问题。"
    )
    public List<Map<String, Object>> searchActivities(
            @ToolParam(description = "活动关键词，没有就传空字符串") String keyword) {

        String kw = keyword == null ? "" : keyword.trim();
        String like = "%" + kw + "%";

        return jdbcTemplate.queryForList("""
                select id, title, organizer, leader_name, start_at, end_at, location, summary
                from activities
                where (? = ''
                    or title like ?
                    or organizer like ?
                    or location like ?
                    or summary like ?
                    or content like ?)
                order by start_at desc
                limit 8
                """, kw, like, like, like, like, like);
    }

    @Tool(
            name = "get_activity_detail",
            description = "根据活动ID查询活动详情。适合回答某个活动的负责人、完整内容、具体时间和地点。"
    )
    public Map<String, Object> getActivityDetail(
            @ToolParam(description = "活动ID") Integer activityId) {

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                select id, title, organizer, leader_id, leader_name, activity_date, activity_time,
                       start_at, end_at, location, summary, content
                from activities
                where id = ?
                limit 1
                """, activityId);

        return rows.isEmpty() ? Map.of("found", false) : rows.get(0);
    }

    @Tool(
            name = "list_my_activities",
            description = "查询某个用户关联的活动。适合回答我参加了哪些活动、我管理了哪些活动。relationType 可传 join、manage、all。"
    )
    public List<Map<String, Object>> listUserActivities(
            @ToolParam(description = "用户ID") String userId,
            @ToolParam(description = "关系类型：join、manage、all") String relationType) {

        String type = (relationType == null || relationType.isBlank()) ? "all" : relationType.trim();

        return jdbcTemplate.queryForList("""
                select a.id, a.title, a.organizer, a.leader_name, uar.relation_type,
                       a.start_at, a.end_at, a.location, a.summary
                from user_activity_rel uar
                join activities a on a.id = uar.activity_id
                where uar.user_id = ?
                  and (? = 'all' or uar.relation_type = ?)
                order by a.start_at desc
                """, userId, type, type);
    }

    @Tool(
            name = "search_notices",
            description = "按关键词查询通知公告。适合回答考试通知、系统维护公告等问题。"
    )
    public List<Map<String, Object>> searchNotices(
            @ToolParam(description = "通知关键词，没有就传空字符串") String keyword) {

        String kw = keyword == null ? "" : keyword.trim();
        String like = "%" + kw + "%";

        return jdbcTemplate.queryForList("""
                select id, title, content, notice_time, created_at
                from notices
                where (? = ''
                    or title like ?
                    or content like ?)
                order by notice_time desc
                limit 8
                """, kw, like, like);
    }

    @Tool(
            name = "get_attendance_summary",
            description = "查询活动出勤统计。适合回答多少人已出勤、多少人请假、多少人未出勤。"
    )
    public Map<String, Object> getActivityAttendanceSummary(
            @ToolParam(description = "活动ID") Integer activityId) {

        Map<String, Object> summary = jdbcTemplate.queryForMap("""
                select count(*) as totalCount,
                       sum(case when attend_status = '已出勤' then 1 else 0 end) as attendedCount,
                       sum(case when attend_status = '请假' then 1 else 0 end) as leaveCount,
                       sum(case when attend_status = '未出勤' then 1 else 0 end) as absentCount
                from activity_attendance
                where activity_id = ?
                """, activityId);

        List<Map<String, Object>> details = jdbcTemplate.queryForList("""
                select user_id, user_name, attend_status, sign_in_at, is_late
                from activity_attendance
                where activity_id = ?
                order by user_id
                """, activityId);

        Map<String, Object> result = new LinkedHashMap<>(summary);
        result.put("details", details);
        return result;
    }

    @Tool(
            name = "get_my_applications",
            description = "查询当前用户的申请记录，包括资料变更申请、活动请假申请、活动创建申请。"
    )
    public Map<String, Object> getMyApplications(
            @ToolParam(description = "当前用户ID") String userId) {

        Map<String, Object> result = new LinkedHashMap<>();

        List<Map<String, Object>> profileChanges = jdbcTemplate.queryForList("""
                select apply_id, change_type, before_value, after_value, submitted_at, status, approval_comment
                from profile_change_applications
                where applicant_id = ? or target_user_id = ?
                order by submitted_at desc
                limit 10
                """, userId, userId);

        List<Map<String, Object>> leaveApps = jdbcTemplate.queryForList("""
                select apply_id, activity_id, activity_title, teacher_name, submitted_at, status, approval_comment
                from activity_leave_applications
                where applicant_id = ?
                order by submitted_at desc
                limit 10
                """, userId);

        List<Map<String, Object>> createApps = jdbcTemplate.queryForList("""
                select apply_id, title, organizer, submitted_at, status, auditor_name, comment
                from activity_create_applications
                where leader_id = ?
                order by submitted_at desc
                limit 10
                """, userId);

        result.put("profileChangeApplications", profileChanges);
        result.put("activityLeaveApplications", leaveApps);
        result.put("activityCreateApplications", createApps);
        return result;
    }

    @Tool(
            name = "search_posts",
            description = "按关键词查询社区帖子。适合回答最近有哪些和竞赛、讲座、勤工俭学有关的帖子。"
    )
    public List<Map<String, Object>> searchCommunityPosts(
            @ToolParam(description = "帖子关键词，没有就传空字符串") String keyword) {

        String kw = keyword == null ? "" : keyword.trim();
        String like = "%" + kw + "%";

        return jdbcTemplate.queryForList("""
                select p.id, p.type, p.content, p.views_count, p.created_at,
                       p.related_activity_id, u.name as author_name
                from community_posts p
                join users u on u.id = p.created_by_user_id
                where (? = ''
                    or p.content like ?
                    or p.type like ?
                    or u.name like ?)
                order by p.created_at desc
                limit 8
                """, kw, like, like, like);
    }
}