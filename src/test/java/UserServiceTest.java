import org.example.webb.entity.User;
import org.example.webb.repository.UserRepository;
import org.example.webb.repository.impl.UserRepositoryImpl;
import org.example.webb.service.UserService;
import org.example.webb.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest {
    @Test
    public void testUserService() {
        UserRepository userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);
        String password = PasswordUtil.generateStrongPassword();
        User saved = userService.addUser(password);

        assertTrue(saved != null);
        assertTrue(saved.getId() != null);
        assertTrue(saved.getUsername().equals(String.format("user%d", userRepository.count())));
        assertTrue(saved.getPasswordHash().length() != 16);

        userRepository.deleteById(saved.getId());
    }
}
