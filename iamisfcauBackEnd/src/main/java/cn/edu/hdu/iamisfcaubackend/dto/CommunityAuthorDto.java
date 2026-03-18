package cn.edu.hdu.iamisfcaubackend.dto;

public record CommunityAuthorDto(
        String id,
        String name,
        String role,
        String department,
        String avatar
) {}