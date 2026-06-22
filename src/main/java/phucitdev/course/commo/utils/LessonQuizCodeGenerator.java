package phucitdev.course.commo.utils;
import org.springframework.stereotype.Service;
import phucitdev.course.modules.lesson_quiz.repository.LessonQuizRepository;

import java.security.SecureRandom;

@Service
public class LessonQuizCodeGenerator {

    private static final String CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    private final LessonQuizRepository lessonQuizRepository;

    public LessonQuizCodeGenerator(
            LessonQuizRepository lessonQuizRepository
    ) {
        this.lessonQuizRepository = lessonQuizRepository;
    }

    public String generateUniqueCode() {
        int maxAttempts = 20;

        for (int i = 0; i < maxAttempts; i++) {
            String code = randomCode();

            if (!lessonQuizRepository.existsByLessonQuizCode(code)) {
                return code;
            }
        }

        throw new RuntimeException(
                "Không thể generate unique lesson quiz code"
        );
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder("LQ-");

        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(
                    CHARS.charAt(random.nextInt(CHARS.length()))
            );
        }

        return sb.toString();
    }
}