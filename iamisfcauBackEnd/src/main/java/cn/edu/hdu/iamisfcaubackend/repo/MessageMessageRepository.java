package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.MessageMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageMessageRepository extends JpaRepository<MessageMessageEntity, Long> {
    List<MessageMessageEntity> findByConversationIdOrderBySeqAsc(String conversationId);

    MessageMessageEntity findTopByConversationIdOrderBySeqDesc(String conversationId);
}