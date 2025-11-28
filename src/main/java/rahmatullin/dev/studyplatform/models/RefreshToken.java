package rahmatullin.dev.studyplatform.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@RedisHash("refresh_token")
public class RefreshToken {
    @Id
    private String username;
    private String token;
    @TimeToLive(unit = TimeUnit.MICROSECONDS)
    private Long timeToLiveSeconds;
}
