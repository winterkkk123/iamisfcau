package cn.edu.hdu.iamisfcaubackend.service;

import cn.edu.hdu.iamisfcaubackend.dto.AiConversationDto;
import cn.edu.hdu.iamisfcaubackend.dto.AiConversationSummaryDto;
import cn.edu.hdu.iamisfcaubackend.dto.AiMessageDto;
import cn.edu.hdu.iamisfcaubackend.entity.AiConversation;
import cn.edu.hdu.iamisfcaubackend.entity.AiMessage;
import cn.edu.hdu.iamisfcaubackend.repo.AiConversationRepo;
import cn.edu.hdu.iamisfcaubackend.repo.AiMessageRepo;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AiConversationService {

    private final AiConversationRepo conversationRepo;
    private final AiMessageRepo messageRepo;

    public AiConversationService(AiConversationRepo conversationRepo, AiMessageRepo messageRepo) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
    }

    @Transactional(readOnly = true)
    public List<AiConversationSummaryDto> listConversations() {
        return conversationRepo.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(c -> new AiConversationSummaryDto(
                        c.getId(),
                        c.getTitle(),
                        c.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AiMessageDto> listMessages(String conversationId) {
        ensureConversationExists(conversationId);

        return messageRepo.findByConversationIdOrderBySeqAsc(conversationId)
                .stream()
                .map(m -> new AiMessageDto(
                        m.getRole(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public AiConversationDto createConversation() {
        AiConversation conversation = new AiConversation();
        conversation.setId("conv-" + UUID.randomUUID());
        conversation.setTitle("新的对话");
        conversation.setCreatedAt(LocalDateTime.now());
        conversationRepo.save(conversation);

        saveAssistantMessage(conversation.getId(), "你好！我是高校智能助手，有什么可以帮你的吗？");

        return new AiConversationDto(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                listMessages(conversation.getId())
        );
    }

    @Transactional(readOnly = true)
    public AiConversation ensureConversationExists(String conversationId) {
        return conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "会话不存在"));
    }

    @Transactional
    public void saveUserMessage(String conversationId, String content) {
        saveMessage(conversationId, "user", content);
    }

    @Transactional
    public void saveAssistantMessage(String conversationId, String content) {
        saveMessage(conversationId, "assistant", content);
    }

    @Transactional
    public void updateTitleIfNeeded(String conversationId, String firstUserMessage) {
        AiConversation conversation = ensureConversationExists(conversationId);

        if (!"新的对话".equals(conversation.getTitle())) {
            return;
        }

        String text = firstUserMessage == null ? "" : firstUserMessage.trim();
        if (text.isBlank()) {
            return;
        }

        String newTitle = text.length() > 18 ? text.substring(0, 18) + "..." : text;
        conversation.setTitle(newTitle);
        conversationRepo.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<Message> buildRecentPromptMessages(String conversationId) {
        ensureConversationExists(conversationId);

        List<AiMessage> recent = messageRepo.findTop20ByConversationIdOrderBySeqDesc(conversationId);
        Collections.reverse(recent);

        return recent.stream().map(this::toSpringAiMessage).toList();
    }

    @Transactional(readOnly = true)
    public AiConversation getConversation(String conversationId) {
        return ensureConversationExists(conversationId);
    }

    private Message toSpringAiMessage(AiMessage message) {
        if ("assistant".equals(message.getRole())) {
            return new AssistantMessage(message.getContent());
        }
        return new UserMessage(message.getContent());
    }

    private void saveMessage(String conversationId, String role, String content) {
        ensureConversationExists(conversationId);

        Integer maxSeq = messageRepo.findMaxSeqByConversationId(conversationId);
        int nextSeq = (maxSeq == null ? 1 : maxSeq + 1);

        AiMessage message = new AiMessage();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setSeq(nextSeq);

        messageRepo.save(message);
    }
}