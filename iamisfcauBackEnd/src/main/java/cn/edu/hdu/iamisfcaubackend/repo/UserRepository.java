package cn.edu.hdu.iamisfcaubackend.repo;

import cn.edu.hdu.iamisfcaubackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query("""
        select u from UserEntity u
        where (:name is null or u.name like concat('%', :name, '%'))
          and (:role is null or u.role = :role)
        order by u.id
    """)
    List<UserEntity> searchUsers(
            @Param("name") String name,
            @Param("role") String role
    );
}