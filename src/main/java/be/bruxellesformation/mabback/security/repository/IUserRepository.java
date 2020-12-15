package be.bruxellesformation.mabback.security.repository;

import be.bruxellesformation.mabback.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IUserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
