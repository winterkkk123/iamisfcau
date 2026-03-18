package cn.edu.hdu.iamisfcaubackend.dto;

public record MessageSendRequest(
        String role,
        String content
) {}