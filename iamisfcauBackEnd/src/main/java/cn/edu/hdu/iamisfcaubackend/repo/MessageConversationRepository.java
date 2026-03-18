package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.MessageConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageConversationRepository extends JpaRepository<MessageConversationEntity, String> {
    List<MessageConversationEntity> findAllByOrderByUpdatedAtDesc();
}