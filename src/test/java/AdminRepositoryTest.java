import org.example.webb.entity.Admin;
import org.example.webb.repository.AdminRepository;
import org.example.webb.repository.impl.AdminRepositoryImpl;
import org.example.webb.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminRepositoryTest {
    @Test
    public void testAdmin(){
        AdminRepository adminRepository =  new AdminRepositoryImpl();
        String password = "123456";
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        Admin admin = new Admin("admin1", hash, salt);
        adminRepository.save(admin);
        assertTrue(adminRepository.findByUsername(admin.getUsername()) != null);
        assertTrue(adminRepository.findById(admin.getId()) != null);
        assertTrue(PasswordUtil.verifyPassword(password, hash, salt));
    }
}
