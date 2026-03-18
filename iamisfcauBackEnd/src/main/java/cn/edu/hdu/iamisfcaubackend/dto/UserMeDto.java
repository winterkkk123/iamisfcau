package cn.edu.hdu.iamisfcaubackend.dto;

import java.util.List;

public record UserMeDto(
        boolean isLogin,
        String name,
        String id,
        String role,
        String college,
        String avatar,
        List<Integer> joinedActivityIds,
        List<Integer> managedActivityIds,
        List<String> communityMyFavorites,
        List<String> communityMyPosts
) {}