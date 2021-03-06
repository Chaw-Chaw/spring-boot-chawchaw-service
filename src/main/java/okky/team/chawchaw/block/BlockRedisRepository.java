package okky.team.chawchaw.block;

import lombok.RequiredArgsConstructor;
import okky.team.chawchaw.block.dto.BlockSessionDto;
import okky.team.chawchaw.config.properties.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class BlockRedisRepository {

    private final RedisTemplate<String, List<BlockSessionDto>> redisTemplate;
    private final RedisProperties redisProperties;

    public void save(List<BlockSessionDto> blockUserIds, String email) {
        String key = redisProperties.getBlock().getPrefix() + email;
        redisTemplate.opsForValue().set(key, blockUserIds);
    }

    public void update(List<BlockSessionDto> blockUserIds, String email) {
        String key = redisProperties.getBlock().getPrefix() + email;
        redisTemplate.opsForValue().setIfPresent(key, blockUserIds);
    }

    public void delete(String email) {
        String key = redisProperties.getBlock().getPrefix() + email;
        redisTemplate.opsForValue().set(key, null, 1, TimeUnit.MILLISECONDS);
    }

    public List<BlockSessionDto> findAllByEmail(String email) {
        String key = redisProperties.getBlock().getPrefix() + email;
        return redisTemplate.opsForValue().get(key);
    }

    public boolean isBlock(String email) {
        String key = redisProperties.getBlock().getPrefix() + email;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null;
    }

}

