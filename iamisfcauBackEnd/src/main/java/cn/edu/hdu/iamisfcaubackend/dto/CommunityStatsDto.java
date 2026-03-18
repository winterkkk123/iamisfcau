package cn.edu.hdu.iamisfcaubackend.dto;

public record CommunityStatsDto(
        long likes,
        long comments,
        long favorites,
        long views
) {}