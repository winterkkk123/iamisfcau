package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.AiConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiConversationRepository extends JpaRepository<AiConversationEntity, String> {
    List<AiConversationEntity> findAllByOrderByCreatedAtDesc();
}