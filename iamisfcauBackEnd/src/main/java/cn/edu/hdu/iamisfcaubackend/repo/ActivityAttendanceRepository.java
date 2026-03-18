package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.ActivityAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityAttendanceRepository extends JpaRepository<ActivityAttendanceEntity, Long> {

    List<ActivityAttendanceEntity> findByActivityIdOrderByIdAsc(Integer activityId);

    Optional<ActivityAttendanceEntity> findByActivityIdAndUserId(Integer activityId, String userId);

    boolean existsByActivityIdAndUserId(Integer activityId, String userId);

    void deleteByActivityIdAndUserId(Integer activityId, String userId);
}