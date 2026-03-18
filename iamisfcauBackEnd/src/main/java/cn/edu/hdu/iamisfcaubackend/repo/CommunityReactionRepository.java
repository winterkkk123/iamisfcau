package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.CommunityPostReactionEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityReactionRepository extends JpaRepository<CommunityPostReactionEntity, Long> {

    long countByPostIdAndReactionType(String postId, String reactionType);

    boolean existsByPostIdAndUserIdAndReactionType(String postId, String userId, String reactionType);

    void deleteByPostIdAndUserIdAndReactionType(String postId, String userId, String reactionType);

    void deleteByPostId(String postId);

    List<CommunityPostReactionEntity> findByUserIdAndPostIdIn(String userId, List<String> postIds);

    interface ReactionAgg {
        String getPostId();
        Long getLikes();
        Long getFavorites();
    }

    @Query(value = """
    select post_id as postId,
           sum(case when reaction_type = 'like' then 1 else 0 end) as likes,
           sum(case when reaction_type = 'favorite' then 1 else 0 end) as favorites
    from community_post_reactions
    where post_id in (:postIds)
    group by post_id
  """, nativeQuery = true)
    List<ReactionAgg> aggByPostIds(@Param("postIds") List<String> postIds);
}