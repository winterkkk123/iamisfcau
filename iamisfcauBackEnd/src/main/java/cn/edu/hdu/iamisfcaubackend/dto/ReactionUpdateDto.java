package cn.edu.hdu.iamisfcaubackend.dto;

public record ReactionUpdateDto(
        boolean liked,
        boolean favorited,
        long likes,
        long favorites
) {}