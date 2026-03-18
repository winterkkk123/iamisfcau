package cn.edu.hdu.iamisfcaubackend.controller;

import cn.edu.hdu.iamisfcaubackend.dto.CreateUserRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UpdateUserRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UserDto;
import cn.edu.hdu.iamisfcaubackend.dto.UserMeDto;
import cn.edu.hdu.iamisfcaubackend.entity.UserEntity;
import cn.edu.hdu.iamisfcaubackend.repo.UserMeQueryRepository;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepository;
import cn.edu.hdu.iamisfcaubackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepo;
    private final UserMeQueryRepository queryRepo;
    private final UserService userService;

    public UserController(
            UserRepository userRepo,
            UserMeQueryRepository queryRepo,
            UserService userService
    ) {
        this.userRepo = userRepo;
        this.queryRepo = queryRepo;
        this.userService = userService;
    }

    private String getCurrentUid(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new RuntimeException("未登录或缺少用户标识");
        }
        return uid;
    }

    @GetMapping("/me")
    public UserMeDto me(@RequestHeader("X-User-Id") String uid) {
        String currentUid = getCurrentUid(uid);
        UserEntity u = userRepo.findById(currentUid).orElseThrow(() -> new RuntimeException("当前用户不存在"));

        return new UserMeDto(
                true,
                u.getName(),
                u.getId(),
                u.getRole(),
                u.getCollege(),
                u.getAvatar(),
                queryRepo.findJoinedActivityIds(currentUid),
                queryRepo.findManagedActivityIds(currentUid),
                queryRepo.findMyFavoritePostIds(currentUid),
                queryRepo.findMyPostIds(currentUid)
        );
    }

    @GetMapping
    public List<UserDto> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role
    ) {
        return userService.listUsers(name, role);
    }

    @PostMapping
    public UserDto create(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userService.deleteUser(id);
    }
}