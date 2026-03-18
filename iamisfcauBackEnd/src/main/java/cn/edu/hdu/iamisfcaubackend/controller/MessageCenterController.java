package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.MessageConversationDto;
import cn.edu.hdu.iamisfcaubackend.dto.MessageDto;
import cn.edu.hdu.iamisfcaubackend.dto.MessageSendRequest;
import cn.edu.hdu.iamisfcaubackend.entity.MessageConversationEntity;
import cn.edu.hdu.iamisfcaubackend.entity.MessageMessageEntity;
import cn.edu.hdu.iamisfcaubackend.repo.MessageConversationRepository;
import cn.edu.hdu.iamisfcaubackend.repo.MessageMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageCenterController {

    private final MessageConversationRepository convRepo;
    private final MessageMessageRepository msgRepo;
    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public MessageCenterController(MessageConversationRepository convRepo,
                                   MessageMessageRepository msgRepo) {
        this.convRepo = convRepo;
        this.msgRepo = msgRepo;
    }

    @GetMapping("/conversations")
    public List<MessageConversationDto> conversations() {
        return convRepo.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(c -> {
                    MessageMessageEntity last = msgRepo.findTopByConversationIdOrderBySeqDesc(c.getId());
                    return new MessageConversationDto(
                            c.getId(),
                            c.getName(),
                            c.getAvatar(),
                            c.getUpdatedAt().atOffset(offset),
                            last != null ? last.getContent() : ""
                    );
                })
                .toList();
    }

    @GetMapping("/conversations/{id}/messages")
    public List<MessageDto> messages(@PathVariable String id) {
        return msgRepo.findByConversationIdOrderBySeqAsc(id)
                .stream()
                .map(m -> new MessageDto(
                        m.getRole(),
                        m.getContent(),
                        m.getSentAt().atOffset(offset)
                ))
                .toList();
    }

    @PostMapping("/conversations/{id}/messages")
    @Transactional
    public MessageDto sendMessage(@PathVariable String id,
                                  @RequestBody MessageSendRequest request) {
        MessageConversationEntity conversation = convRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        MessageMessageEntity last = msgRepo.findTopByConversationIdOrderBySeqDesc(id);
        int nextSeq = last == null ? 1 : last.getSeq() + 1;

        LocalDateTime now = LocalDateTime.now();

        MessageMessageEntity message = new MessageMessageEntity();
        message.setConversationId(id);
        message.setRole(request.role());
        message.setContent(request.content());
        message.setSentAt(now);
        message.setSeq(nextSeq);

        msgRepo.save(message);

        conversation.setUpdatedAt(now);
        convRepo.save(conversation);

        return new MessageDto(
                message.getRole(),
                message.getContent(),
                message.getSentAt().atOffset(offset)
        );
    }
}