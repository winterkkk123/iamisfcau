package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.AiConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiConversationRepo extends JpaRepository<AiConversation, String> {

    List<AiConversation> findAllByOrderByCreatedAtDesc();
}