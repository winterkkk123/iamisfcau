package cn.edu.hdu.iamisfcaubackend.service.impl;

import cn.edu.hdu.iamisfcaubackend.dto.CreateUserRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UpdateUserRequest;
import cn.edu.hdu.iamisfcaubackend.dto.UserDto;
import cn.edu.hdu.iamisfcaubackend.entity.UserEntity;
import cn.edu.hdu.iamisfcaubackend.repo.UserRepository;
import cn.edu.hdu.iamisfcaubackend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> listUsers(String name, String role) {
        String nameParam = (name == null || name.isBlank()) ? null : name;
        String roleParam = (role == null || role.isBlank()) ? null : role;

        return userRepository.searchUsers(nameParam, roleParam)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsById(request.getId())) {
            throw new RuntimeException("用户ID已存在");
        }

        UserEntity entity = new UserEntity();
        entity.setId(request.getId());
        entity.setName(request.getName());
        entity.setRole(request.getRole());
        entity.setPassword(request.getPassword());
        entity.setGender(request.getGender());
        entity.setAvatar(request.getAvatar());
        entity.setDepartment(request.getDepartment());
        entity.setPhone(request.getPhone());

        return toDto(userRepository.save(entity));
    }

    @Override
    public UserDto updateUser(String id, UpdateUserRequest request) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        entity.setName(request.getName());
        entity.setRole(request.getRole());
        entity.setPassword(request.getPassword());
        entity.setGender(request.getGender());
        entity.setAvatar(request.getAvatar());
        entity.setDepartment(request.getDepartment());
        entity.setPhone(request.getPhone());

        return toDto(userRepository.save(entity));
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    private UserDto toDto(UserEntity e) {
        return new UserDto(
                e.getId(),
                e.getName(),
                e.getRole(),
                e.getPassword(),
                e.getGender(),
                e.getAvatar(),
                e.getDepartment(),
                e.getCollege(),
                e.getPhone()
        );
    }
}