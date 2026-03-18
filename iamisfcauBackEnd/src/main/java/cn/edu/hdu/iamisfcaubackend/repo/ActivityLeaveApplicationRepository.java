package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.ActivityLeaveApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLeaveApplicationRepository extends JpaRepository<ActivityLeaveApplicationEntity, String> {
    List<ActivityLeaveApplicationEntity> findAllByOrderBySubmittedAtDesc();

    boolean existsByActivityIdAndApplicantIdAndStatus(Integer activityId, String applicantId, String status);
}