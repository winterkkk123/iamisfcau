package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {
}