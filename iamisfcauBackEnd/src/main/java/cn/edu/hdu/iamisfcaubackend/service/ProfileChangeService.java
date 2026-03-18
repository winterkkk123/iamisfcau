package cn.edu.hdu.iamisfcaubackend.service;

import cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeCreateRequest;
import cn.edu.hdu.iamisfcaubackend.dto.ProfileChangeDto;
import cn.edu.hdu.iamisfcaubackend.entity.ProfileChangeApplicationEntity;
import cn.edu.hdu.iamisfcaubackend.entity.UserEntity;
import cn.edu.hdu.iamisfcaubackend.repo.ProfileChangeApplicationRepository;
import cn.edu.hdu.iamisfcaubackend.repo.ActivityRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileChangeService {

    private final ProfileChangeApplicationRepository repo;
    private final UserRepository userRepository;
    private final ZoneOffset offset = ZoneOffset.ofHours(8);

    public ProfileChangeService(ProfileChangeApplicationRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public List<ProfileChangeDto> list() {
        return repo.findAllByOrderBySubmittedAtDesc().stream().map(this::toDto).toList();
    }

    public ProfileChangeDto create(ProfileChangeCreateRequest request) {
        UserEntity targetUser = userRepository.findById(request.getTargetUserId()).orElseThrow();
        UserEntity applicantUser = userRepository.findById(request.getApplicantId()).orElseThrow();

        ProfileChangeApplicationEntity entity = new ProfileChangeApplicationEntity();
        entity.setApplyId(generateApplyId());
        entity.setTargetUserId(targetUser.getId());
        entity.setTargetUserName(targetUser.getName());
        entity.setApplicantId(applicantUser.getId());
        entity.setApplicantName(applicantUser.getName());
        entity.setChangeType(request.getChangeType());
        entity.setBeforeValue(request.getBeforeValue());
        entity.setAfterValue(request.getAfterValue());
        entity.setSubmittedAt(LocalDateTime.now());
        entity.setStatus("待审核");
        entity.setApprovalComment(null);

        return toDto(repo.save(entity));
    }

    private ProfileChangeDto toDto(ProfileChangeApplicationEntity e) {
        OffsetDateTime submittedAt = e.getSubmittedAt() == null ? null : e.getSubmittedAt().atOffset(offset);
        return new ProfileChangeDto(
                e.getApplyId(),
                e.getTargetUserName(),
                e.getTargetUserId(),
                e.getApplicantName(),
                e.getApplicantId(),
                e.getChangeType(),
                e.getBeforeValue(),
                e.getAfterValue(),
                submittedAt,
                e.getStatus(),
                e.getApprovalComment()
        );
    }

    private String generateApplyId() {
        LocalDateTime now = LocalDateTime.now();
        String date = String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        return "AR-" + date + "-" + suffix;
    }
}