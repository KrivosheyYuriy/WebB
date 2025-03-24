import jakarta.transaction.Transactional;
import org.example.webb.entity.Language;
import org.example.webb.entity.PollAnswer;
import org.example.webb.entity.PollAnswerLanguage;
import org.example.webb.entity.User;
import org.example.webb.repository.PollAnswersRepository;
import org.example.webb.repository.impl.PollAnswersRepositoryImpl;
import org.example.webb.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PollAnswerRepositoryTest {
    @Test
    @Transactional
    public void testPollAnswerRepository() {
        PollAnswersRepository pollAnswerRepository = new PollAnswersRepositoryImpl();
        String username = "TestUser";
        LocalDate birthday = LocalDate.of(1980, 1, 4);
        String gender = "мужской";
        String phoneNumber = "88005553535";
        String email = "test@test.com";
        String biography = "";
        LocalDateTime received = LocalDateTime.now();
        PollAnswer pollAnswer = new PollAnswer(username, birthday, gender, phoneNumber, email,
                biography, received, null);
        pollAnswerRepository.save(pollAnswer);
        assertTrue(pollAnswerRepository.findById(pollAnswer.getId()) != null);

        String newEmail = "newTest@test.com";
        pollAnswer.setEmail(newEmail);
        pollAnswerRepository.merge(pollAnswer);
        assertTrue(pollAnswerRepository.findById(pollAnswer.getId()).getEmail().equals(newEmail));

        pollAnswerRepository.deleteById(pollAnswer.getId());
        assertTrue(pollAnswerRepository.findById(pollAnswer.getId()) == null);

        PollAnswer answer = pollAnswerRepository.findById(70L);
        pollAnswerRepository.deleteById(70L);
        assertTrue(answer == null || pollAnswerRepository.findById(answer.getId()) == null);
    }
}
