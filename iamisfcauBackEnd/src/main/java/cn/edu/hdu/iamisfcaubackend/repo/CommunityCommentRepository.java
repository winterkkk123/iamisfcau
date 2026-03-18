package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.CommunityCommentEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityCommentEntity, String> {

    List<CommunityCommentEntity> findByPostIdOrderByCreatedAtAsc(String postId);

    List<CommunityCommentEntity> findByPostIdInOrderByCreatedAtAsc(List<String> postIds);

    void deleteByPostId(String postId);

    interface CommentAgg {
        String getPostId();
        Long getCnt();
    }

    @Query(value = """
              select post_id as postId, count(*) as cnt
              from community_comments
              where post_id in (:postIds)
              group by post_id
            """, nativeQuery = true)
    List<CommentAgg> countByPostIds(@Param("postIds") List<String> postIds);
}