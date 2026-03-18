package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.AiMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AiMessageRepo extends JpaRepository<AiMessage, Long> {

    List<AiMessage> findByConversationIdOrderBySeqAsc(String conversationId);

    List<AiMessage> findTop20ByConversationIdOrderBySeqDesc(String conversationId);

    @Query("select coalesce(max(m.seq), 0) from AiMessage m where m.conversationId = :conversationId")
    Integer findMaxSeqByConversationId(@Param("conversationId") String conversationId);
}