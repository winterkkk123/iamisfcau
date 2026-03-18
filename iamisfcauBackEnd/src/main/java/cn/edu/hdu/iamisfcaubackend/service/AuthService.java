package cn.edu.hdu.iamisfcaubackend.service;

import cn.edu.hdu.iamisfcaubackend.dto.LoginRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UserMeDto;
import cn.edu.hdu.iamisfcaubackend.entity.User;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepo userRepo;

    public AuthService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserMeDto login(LoginRequest request) {
        User user = userRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("账号不存在"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String college = user.getCollege() != null && !user.getCollege().isBlank()
                ? user.getCollege()
                : user.getDepartment();

        return new UserMeDto(
                true,                  // isLogin
                user.getName(),        // name
                user.getId(),          // id
                user.getRole(),        // role
                college,               // college
                user.getAvatar(),      // avatar
                List.of(),             // joinedActivityIds
                List.of(),             // managedActivityIds
                List.of(),             // communityMyFavorites
                List.of()              // communityMyPosts
        );
    }
}