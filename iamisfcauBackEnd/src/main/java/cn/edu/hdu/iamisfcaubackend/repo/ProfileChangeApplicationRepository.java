package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.ProfileChangeApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileChangeApplicationRepository extends JpaRepository<ProfileChangeApplicationEntity, String> {
    List<ProfileChangeApplicationEntity> findAllByOrderBySubmittedAtDesc();
}