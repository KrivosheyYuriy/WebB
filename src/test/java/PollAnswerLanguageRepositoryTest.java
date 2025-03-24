import org.example.webb.entity.Language;
import org.example.webb.entity.PollAnswer;
import org.example.webb.entity.PollAnswerLanguage;
import org.example.webb.repository.LanguageRepository;
import org.example.webb.repository.PollAnswerLanguageRepository;
import org.example.webb.repository.PollAnswersRepository;
import org.example.webb.repository.impl.LanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswerLanguageRepositoryImpl;
import org.example.webb.repository.impl.PollAnswersRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PollAnswerLanguageRepositoryTest {
    @Test
    public void testPollAnswerLanguageRepository() {
        PollAnswersRepository pollAnswersRepository = new PollAnswersRepositoryImpl();
        PollAnswerLanguageRepository repository = new PollAnswerLanguageRepositoryImpl();
        LanguageRepository languageRepository = new LanguageRepositoryImpl();

        Language python = languageRepository.findById(6L);
        long pythonBefore = repository.countByLanguage(python);

        String username = "TestUser";
        LocalDate birthday = LocalDate.of(1980, 1, 4);
        String gender = "мужской";
        String phoneNumber = "88005553535";
        String email = "test@test.com";
        String biography = "";
        LocalDateTime received = LocalDateTime.now();
        PollAnswer pollAnswer = new PollAnswer(username, birthday, gender, phoneNumber, email,
                biography, received, null);
        pollAnswer.addPollAnswerLanguage(new PollAnswerLanguage(pollAnswer,  python));
        pollAnswersRepository.merge(pollAnswer);

        assertTrue(repository.countByLanguage(python) == pythonBefore + 1);
    }
}
