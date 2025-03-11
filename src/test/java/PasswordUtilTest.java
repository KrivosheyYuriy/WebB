import static org.junit.jupiter.api.Assertions.assertTrue;
import org.example.webb.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class PasswordUtilTest {
    @Test
    public void testPasswordUtil() {
        Set<String> passwordSet = new HashSet<>();
        for (int i = 0; i < 1000000; i++) { // проверка на минимальное совпадение одинаковых паролей
            String password = PasswordUtil.generateStrongPassword();
            assertTrue(!passwordSet.contains(password));
            passwordSet.add(password);
            assertTrue(password.length() == 16);

            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(password, salt);
            assertTrue(hashedPassword != null);
            assertTrue(!hashedPassword.contains(password));
            assertTrue(!hashedPassword.contains(salt));
            assertTrue(PasswordUtil.verifyPassword(password, hashedPassword, salt));
        }
    }
}
