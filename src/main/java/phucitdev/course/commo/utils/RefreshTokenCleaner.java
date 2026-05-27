package phucitdev.course.commo.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import phucitdev.course.modules.auth.entity.RefreshToken;
import phucitdev.course.modules.auth.repository.RefreshTokenRepository;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Component
public class RefreshTokenCleaner {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCleaner.class);
    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshTokenCleaner(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    @Scheduled(cron = "0 0 */2 * * *")
    public void cleanTokens() {
        int totalDeleted = 0;
        while (true) {
            List<RefreshToken> batch =
                    refreshTokenRepository.findTop1000ByExpiredAtBeforeOrRevokedTrue(new Date());
            if (batch.isEmpty()) {
                break;
            }
            List<UUID> ids = batch.stream()
                    .map(RefreshToken::getId)
                    .toList();
            totalDeleted += refreshTokenRepository.deleteByIds(ids);
        }
        if (totalDeleted > 0) {
            logger.info(
                    "Deleted {} expired/revoked refresh tokens",
                    totalDeleted
            );
        }
    }
}