package cn.edu.hdu.iamisfcaubackend.dto;

public record CommunityUserStateDto(
        boolean liked,
        boolean favorited
) {}