package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.UserActivityRelEntity;
import cn.edu.hdu.iamisfcaubackend.entity.UserActivityRelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRelRepository extends JpaRepository<UserActivityRelEntity, UserActivityRelId> {

    boolean existsById(UserActivityRelId id);

    List<UserActivityRelEntity> findByIdUserIdAndIdRelationType(String userId, String relationType);

    void deleteByIdUserIdAndIdActivityIdAndIdRelationType(String userId, Integer activityId, String relationType);
}