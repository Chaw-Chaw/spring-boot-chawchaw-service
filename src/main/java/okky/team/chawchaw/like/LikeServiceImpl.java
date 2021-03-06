package okky.team.chawchaw.like;

import lombok.RequiredArgsConstructor;
import okky.team.chawchaw.like.dto.CreateLikeDto;
import okky.team.chawchaw.like.dto.DeleteLikeDto;
import okky.team.chawchaw.like.dto.LikeMessageDto;
import okky.team.chawchaw.user.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final LikeRedisRepository likeRedisRepository;

    @Override
    @Transactional(readOnly = false)
    public LikeMessageDto addLike(CreateLikeDto createLikeDto) {

        if (!likeRepository.isLike(createLikeDto.getUserFromId(), createLikeDto.getUserId())) {
            likeRepository.save(new LikeEntity(new UserEntity(createLikeDto.getUserFromId()), new UserEntity(createLikeDto.getUserId())));
            LikeMessageDto result = new LikeMessageDto(LikeType.LIKE, createLikeDto.getUserFromName(), LocalDateTime.now().withNano(0));
            likeRedisRepository.save(result, createLikeDto.getUserId());
            return result;
        }
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public LikeMessageDto deleteLike(DeleteLikeDto deleteLikeDto) {

        if (likeRepository.isLike(deleteLikeDto.getUserFromId(), deleteLikeDto.getUserId())) {
            likeRepository.removeByUserFromIdAndUserToId(deleteLikeDto.getUserFromId(), deleteLikeDto.getUserId());
            LikeMessageDto result = new LikeMessageDto(LikeType.UNLIKE, deleteLikeDto.getUserFromName(), LocalDateTime.now().withNano(0));
            likeRedisRepository.save(result, deleteLikeDto.getUserId());
            return result;
        }
        return null;
    }

    @Override
    public List<LikeMessageDto> findMessagesByUserFromId(Long userFromId) {

        List<LikeMessageDto> result = likeRedisRepository.findMessagesByUserFromId(userFromId);
        likeRedisRepository.deleteMessagesByUserFromId(userFromId);
        return result;
    }

    @Override
    public Boolean isLike(Long userFromId, Long userToId) {

        return likeRepository.isLike(userFromId, userToId);
    }
}
