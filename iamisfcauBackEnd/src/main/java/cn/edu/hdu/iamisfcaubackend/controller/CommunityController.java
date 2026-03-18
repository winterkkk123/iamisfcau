package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.*;
import cn.edu.hdu.iamisfcaubackend.entity.*;
import cn.edu.hdu.iamisfcaubackend.repo.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
@CrossOrigin
public class CommunityController {

    private final CommunityPostRepository postRepo;
    private final CommunityCommentRepository commentRepo;
    private final CommunityReactionRepository reactionRepo;
    private final UserRepository userRepo;
    private final ObjectMapper mapper;

    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public CommunityController(CommunityPostRepository postRepo,
                               CommunityCommentRepository commentRepo,
                               CommunityReactionRepository reactionRepo,
                               UserRepository userRepo,
                               ObjectMapper mapper) {
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
        this.reactionRepo = reactionRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    private String getCurrentUid(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new RuntimeException("未登录或缺少用户标识");
        }
        return uid;
    }

    // 1 帖子列表
    @GetMapping("/posts")
    public List<CommunityPostDto> listPosts(@RequestHeader("X-User-Id") String uid) {
        String currentUid = getCurrentUid(uid);

        List<CommunityPostEntity> posts = postRepo.findAllByOrderByCreatedAtDesc();
        if (posts.isEmpty()) return List.of();

        List<String> postIds = posts.stream().map(CommunityPostEntity::getId).toList();

        Set<String> allUserIds = new HashSet<>();
        allUserIds.addAll(posts.stream().map(CommunityPostEntity::getCreatedByUserId).collect(Collectors.toSet()));

        List<CommunityCommentEntity> allComments = commentRepo.findByPostIdInOrderByCreatedAtAsc(postIds);
        allUserIds.addAll(allComments.stream().map(CommunityCommentEntity::getUserId).collect(Collectors.toSet()));

        Map<String, UserEntity> userMap = userRepo.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));

        Map<String, CommunityReactionRepository.ReactionAgg> reactionAggMap = reactionRepo.aggByPostIds(postIds).stream()
                .collect(Collectors.toMap(CommunityReactionRepository.ReactionAgg::getPostId, x -> x));

        Map<String, Long> commentCntMap = commentRepo.countByPostIds(postIds).stream()
                .collect(Collectors.toMap(CommunityCommentRepository.CommentAgg::getPostId, CommunityCommentRepository.CommentAgg::getCnt));

        Map<String, Set<String>> myReactionTypesByPost = new HashMap<>();
        for (CommunityPostReactionEntity r : reactionRepo.findByUserIdAndPostIdIn(currentUid, postIds)) {
            myReactionTypesByPost.computeIfAbsent(r.getPostId(), k -> new HashSet<>()).add(r.getReactionType());
        }

        Map<String, List<CommunityCommentEntity>> commentsByPost = allComments.stream()
                .collect(Collectors.groupingBy(CommunityCommentEntity::getPostId));

        List<CommunityPostDto> result = new ArrayList<>();
        for (CommunityPostEntity p : posts) {
            CommunityAuthorDto author = toAuthorDto(userMap.get(p.getCreatedByUserId()), p.getCreatedByUserId());

            CommunityReactionRepository.ReactionAgg ra = reactionAggMap.get(p.getId());
            long likes = ra == null || ra.getLikes() == null ? 0 : ra.getLikes();
            long favorites = ra == null || ra.getFavorites() == null ? 0 : ra.getFavorites();

            long commentsCnt = commentCntMap.getOrDefault(p.getId(), 0L);
            long views = p.getViewsCount() == null ? 0 : p.getViewsCount();

            Set<String> types = myReactionTypesByPost.getOrDefault(p.getId(), Set.of());
            CommunityUserStateDto userState = new CommunityUserStateDto(types.contains("like"), types.contains("favorite"));

            List<String> images = parseJsonArray(p.getImages());
            String relatedActivityId = p.getRelatedActivityId() == null ? "" : String.valueOf(p.getRelatedActivityId());

            List<CommunityCommentDto> commentTree =
                    buildCommentTree(commentsByPost.getOrDefault(p.getId(), List.of()), userMap);

            result.add(new CommunityPostDto(
                    p.getId(),
                    author,
                    p.getCreatedByUserId(),
                    p.getCreatedAt().atOffset(offset),
                    p.getType(),
                    relatedActivityId,
                    p.getContent(),
                    images,
                    new CommunityStatsDto(likes, commentsCnt, favorites, views),
                    userState,
                    commentTree
            ));
        }
        return result;
    }

    // 2 帖子详情
    @GetMapping("/posts/{id}")
    @Transactional
    public CommunityPostDto detail(@RequestHeader("X-User-Id") String uid,
                                   @PathVariable String id,
                                   @RequestParam(defaultValue = "true") boolean incViews) {
        String currentUid = getCurrentUid(uid);

        CommunityPostEntity p = postRepo.findById(id).orElseThrow(() -> new RuntimeException("帖子不存在"));

        if (incViews) {
            Integer oldViews = p.getViewsCount() == null ? 0 : p.getViewsCount();
            postRepo.incViews(id);
            p.setViewsCount(oldViews + 1);
        }

        Set<String> userIds = new HashSet<>();
        userIds.add(p.getCreatedByUserId());

        List<CommunityCommentEntity> comments = commentRepo.findByPostIdOrderByCreatedAtAsc(id);
        userIds.addAll(comments.stream().map(CommunityCommentEntity::getUserId).collect(Collectors.toSet()));

        Map<String, UserEntity> userMap = userRepo.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));

        CommunityAuthorDto author = toAuthorDto(userMap.get(p.getCreatedByUserId()), p.getCreatedByUserId());

        long likes = reactionRepo.countByPostIdAndReactionType(id, "like");
        long favorites = reactionRepo.countByPostIdAndReactionType(id, "favorite");
        long commentsCnt = comments.size();
        long views = p.getViewsCount() == null ? 0 : p.getViewsCount();

        boolean liked = reactionRepo.existsByPostIdAndUserIdAndReactionType(id, currentUid, "like");
        boolean favorited = reactionRepo.existsByPostIdAndUserIdAndReactionType(id, currentUid, "favorite");

        List<String> images = parseJsonArray(p.getImages());
        String relatedActivityId = p.getRelatedActivityId() == null ? "" : String.valueOf(p.getRelatedActivityId());

        List<CommunityCommentDto> commentTree = buildCommentTree(comments, userMap);

        return new CommunityPostDto(
                p.getId(),
                author,
                p.getCreatedByUserId(),
                p.getCreatedAt().atOffset(offset),
                p.getType(),
                relatedActivityId,
                p.getContent(),
                images,
                new CommunityStatsDto(likes, commentsCnt, favorites, views),
                new CommunityUserStateDto(liked, favorited),
                commentTree
        );
    }

    // 3 发帖
    @PostMapping("/posts")
    @Transactional
    public CommunityPostDto createPost(@RequestHeader("X-User-Id") String uid,
                                       @RequestBody CommunityPostCreateRequest body) {
        String currentUid = getCurrentUid(uid);

        if (body == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }

        String content = body.getContent() == null ? "" : body.getContent().trim();
        List<String> images = body.getImages() == null ? List.of() : body.getImages();

        if (content.isBlank() && images.isEmpty()) {
            throw new IllegalArgumentException("内容和图片不能同时为空");
        }

        userRepo.findById(currentUid).orElseThrow(() -> new RuntimeException("当前用户不存在"));

        CommunityPostEntity p = new CommunityPostEntity();
        p.setId("post-" + currentUid + "-" + System.currentTimeMillis());
        p.setCreatedByUserId(currentUid);
        p.setCreatedAt(LocalDateTime.now());
        p.setType(body.getType() == null || body.getType().isBlank() ? "user_post" : body.getType().trim());

        if (body.getRelatedActivityId() == null || body.getRelatedActivityId().isBlank()) {
            p.setRelatedActivityId(null);
        } else {
            p.setRelatedActivityId(Integer.parseInt(body.getRelatedActivityId().trim()));
        }

        p.setContent(content);
        p.setImages(toJsonArray(images));
        p.setViewsCount(0);

        postRepo.save(p);

        return detail(uid, p.getId(), false);
    }

    // 4 删除帖子
    @DeleteMapping("/posts/{id}")
    @Transactional
    public void deletePost(@RequestHeader("X-User-Id") String uid,
                           @PathVariable String id) {
        String currentUid = getCurrentUid(uid);

        CommunityPostEntity post = postRepo.findById(id).orElseThrow(() -> new RuntimeException("帖子不存在"));
        UserEntity currentUser = userRepo.findById(currentUid).orElseThrow(() -> new RuntimeException("当前用户不存在"));

        boolean isOwner = currentUid.equals(post.getCreatedByUserId());
        boolean isAdmin = "管理员".equals(currentUser.getRole());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("无权限删除该帖子");
        }

        commentRepo.deleteByPostId(id);
        reactionRepo.deleteByPostId(id);
        postRepo.delete(post);
    }

    // 5 点赞 toggle
    @PostMapping("/posts/{id}/like")
    @Transactional
    public ReactionUpdateDto toggleLike(@RequestHeader("X-User-Id") String uid,
                                        @PathVariable String id) {
        String currentUid = getCurrentUid(uid);

        boolean exists = reactionRepo.existsByPostIdAndUserIdAndReactionType(id, currentUid, "like");
        if (exists) {
            reactionRepo.deleteByPostIdAndUserIdAndReactionType(id, currentUid, "like");
        } else {
            CommunityPostReactionEntity r = new CommunityPostReactionEntity();
            r.setPostId(id);
            r.setUserId(currentUid);
            r.setReactionType("like");
            r.setCreatedAt(LocalDateTime.now());
            reactionRepo.save(r);
        }

        boolean liked = !exists;
        boolean favorited = reactionRepo.existsByPostIdAndUserIdAndReactionType(id, currentUid, "favorite");
        long likes = reactionRepo.countByPostIdAndReactionType(id, "like");
        long favorites = reactionRepo.countByPostIdAndReactionType(id, "favorite");
        return new ReactionUpdateDto(liked, favorited, likes, favorites);
    }

    // 6 收藏 toggle
    @PostMapping("/posts/{id}/favorite")
    @Transactional
    public ReactionUpdateDto toggleFavorite(@RequestHeader("X-User-Id") String uid,
                                            @PathVariable String id) {
        String currentUid = getCurrentUid(uid);

        boolean exists = reactionRepo.existsByPostIdAndUserIdAndReactionType(id, currentUid, "favorite");
        if (exists) {
            reactionRepo.deleteByPostIdAndUserIdAndReactionType(id, currentUid, "favorite");
        } else {
            CommunityPostReactionEntity r = new CommunityPostReactionEntity();
            r.setPostId(id);
            r.setUserId(currentUid);
            r.setReactionType("favorite");
            r.setCreatedAt(LocalDateTime.now());
            reactionRepo.save(r);
        }

        boolean favorited = !exists;
        boolean liked = reactionRepo.existsByPostIdAndUserIdAndReactionType(id, currentUid, "like");
        long likes = reactionRepo.countByPostIdAndReactionType(id, "like");
        long favorites = reactionRepo.countByPostIdAndReactionType(id, "favorite");
        return new ReactionUpdateDto(liked, favorited, likes, favorites);
    }

    // 7 发评论
    @PostMapping("/posts/{id}/comments")
    @Transactional
    public CommunityCommentDto createComment(@RequestHeader("X-User-Id") String uid,
                                             @PathVariable String id,
                                             @RequestBody CommentCreateRequest body) {
        String currentUid = getCurrentUid(uid);

        if (body == null || body.content() == null || body.content().isBlank()) {
            throw new IllegalArgumentException("content 不能为空");
        }

        UserEntity u = userRepo.findById(currentUid).orElseThrow(() -> new RuntimeException("当前用户不存在"));
        postRepo.findById(id).orElseThrow(() -> new RuntimeException("帖子不存在"));

        CommunityCommentEntity c = new CommunityCommentEntity();
        c.setId(UUID.randomUUID().toString());
        c.setPostId(id);
        c.setUserId(currentUid);
        c.setUserName(u.getName());
        c.setCreatedAt(LocalDateTime.now());
        c.setContent(body.content().trim());
        c.setParentCommentId(null);

        commentRepo.save(c);

        return new CommunityCommentDto(
                c.getId(),
                new CommunityCommentUserDto(u.getId(), u.getName(), u.getRole(), u.getAvatar()),
                c.getCreatedAt().atOffset(offset),
                c.getContent(),
                List.of()
        );
    }

    // 8 发回复
    @PostMapping("/comments/{commentId}/replies")
    @Transactional
    public CommunityCommentDto reply(@RequestHeader("X-User-Id") String uid,
                                     @PathVariable String commentId,
                                     @RequestBody CommentCreateRequest body) {
        String currentUid = getCurrentUid(uid);

        if (body == null || body.content() == null || body.content().isBlank()) {
            throw new IllegalArgumentException("content 不能为空");
        }

        CommunityCommentEntity parent = commentRepo.findById(commentId).orElseThrow(() -> new RuntimeException("评论不存在"));
        UserEntity u = userRepo.findById(currentUid).orElseThrow(() -> new RuntimeException("当前用户不存在"));

        CommunityCommentEntity c = new CommunityCommentEntity();
        c.setId(UUID.randomUUID().toString());
        c.setPostId(parent.getPostId());
        c.setUserId(currentUid);
        c.setUserName(u.getName());
        c.setCreatedAt(LocalDateTime.now());
        c.setContent(body.content().trim());
        c.setParentCommentId(commentId);

        commentRepo.save(c);

        return new CommunityCommentDto(
                c.getId(),
                new CommunityCommentUserDto(u.getId(), u.getName(), u.getRole(), u.getAvatar()),
                c.getCreatedAt().atOffset(offset),
                c.getContent(),
                List.of()
        );
    }

    // ---------------- helper ----------------

    private CommunityAuthorDto toAuthorDto(UserEntity u, String userIdFallback) {
        if (u == null) {
            return new CommunityAuthorDto(userIdFallback, userIdFallback, "", "", "");
        }
        return new CommunityAuthorDto(u.getId(), u.getName(), u.getRole(), u.getDepartment(), u.getAvatar());
    }

    private CommunityCommentUserDto toCommentUserDto(UserEntity u, CommunityCommentEntity c) {
        if (u == null) {
            return new CommunityCommentUserDto(c.getUserId(), c.getUserName(), "", "");
        }
        return new CommunityCommentUserDto(u.getId(), u.getName(), u.getRole(), u.getAvatar());
    }

    private List<String> parseJsonArray(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String toJsonArray(List<String> list) {
        try {
            return mapper.writeValueAsString(list == null ? List.of() : list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<CommunityCommentDto> buildCommentTree(List<CommunityCommentEntity> comments,
                                                       Map<String, UserEntity> userMap) {
        if (comments == null || comments.isEmpty()) return List.of();

        Map<String, List<CommunityCommentEntity>> children = new HashMap<>();
        for (CommunityCommentEntity c : comments) {
            children.computeIfAbsent(c.getParentCommentId(), k -> new ArrayList<>()).add(c);
        }

        List<CommunityCommentEntity> roots = children.getOrDefault(null, List.of());
        List<CommunityCommentDto> rootDtos = new ArrayList<>();
        for (CommunityCommentEntity r : roots) {
            rootDtos.add(toCommentDtoRecursive(r, children, userMap));
        }
        return rootDtos;
    }

    private CommunityCommentDto toCommentDtoRecursive(CommunityCommentEntity c,
                                                      Map<String, List<CommunityCommentEntity>> children,
                                                      Map<String, UserEntity> userMap) {
        List<CommunityCommentEntity> subs = children.getOrDefault(c.getId(), List.of());
        List<CommunityCommentDto> replies = new ArrayList<>();
        for (CommunityCommentEntity s : subs) {
            replies.add(toCommentDtoRecursive(s, children, userMap));
        }

        return new CommunityCommentDto(
                c.getId(),
                toCommentUserDto(userMap.get(c.getUserId()), c),
                c.getCreatedAt().atOffset(offset),
                c.getContent(),
                replies
        );
    }
}