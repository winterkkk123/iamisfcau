package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMeQueryRepository extends Repository<UserEntity, String> {

    @Query(value = """
    select activity_id from user_activity_rel
    where user_id = :uid and relation_type = 'join'
    order by activity_id
  """, nativeQuery = true)
    List<Integer> findJoinedActivityIds(@Param("uid") String uid);

    @Query(value = """
    select activity_id from user_activity_rel
    where user_id = :uid and relation_type = 'manage'
    order by activity_id
  """, nativeQuery = true)
    List<Integer> findManagedActivityIds(@Param("uid") String uid);

    @Query(value = """
    select post_id from community_post_reactions
    where user_id = :uid and reaction_type = 'favorite'
    order by created_at desc
  """, nativeQuery = true)
    List<String> findMyFavoritePostIds(@Param("uid") String uid);

    @Query(value = """
    select id from community_posts
    where created_by_user_id = :uid
    order by created_at desc
  """, nativeQuery = true)
    List<String> findMyPostIds(@Param("uid") String uid);
}