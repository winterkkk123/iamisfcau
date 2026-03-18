package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.CommunityPostEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, String> {

    List<CommunityPostEntity> findAllByOrderByCreatedAtDesc();

    @Modifying
    @Query(value = "update community_posts set views_count = views_count + 1 where id = :id", nativeQuery = true)
    int incViews(@Param("id") String id);
}