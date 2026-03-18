package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.ActivityCreateApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityCreateApplicationRepository extends JpaRepository<ActivityCreateApplicationEntity, String> {
    List<ActivityCreateApplicationEntity> findAllByOrderBySubmittedAtDesc();
}