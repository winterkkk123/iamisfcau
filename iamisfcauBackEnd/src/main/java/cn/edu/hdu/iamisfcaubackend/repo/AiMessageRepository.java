package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.AiMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiMessageRepository extends JpaRepository<AiMessageEntity, Long> {
    List<AiMessageEntity> findByConversationIdOrderBySeqAsc(String conversationId);
}