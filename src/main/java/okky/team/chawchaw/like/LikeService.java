package okky.team.chawchaw.like;

import okky.team.chawchaw.like.dto.CreateLikeDto;
import okky.team.chawchaw.like.dto.DeleteLikeDto;
import okky.team.chawchaw.like.dto.LikeMessageDto;

import java.util.List;

public interface LikeService {

    LikeMessageDto addLike(CreateLikeDto createLikeDto);
    LikeMessageDto deleteLike(DeleteLikeDto deleteLikeDto);

    List<LikeMessageDto> findMessagesByUserFromId(Long userFromId);

    Boolean isLike(Long userFromId, Long userToId);

}
