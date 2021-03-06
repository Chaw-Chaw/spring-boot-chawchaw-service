package okky.team.chawchaw.block;

import lombok.RequiredArgsConstructor;
import okky.team.chawchaw.block.dto.BlockUserDto;
import okky.team.chawchaw.block.dto.CreateBlockDto;
import okky.team.chawchaw.block.dto.DeleteBlockDto;
import okky.team.chawchaw.block.exception.ExistBlockException;
import okky.team.chawchaw.block.exception.NotExistBlockException;
import okky.team.chawchaw.config.auth.PrincipalDetails;
import okky.team.chawchaw.user.UserService;
import okky.team.chawchaw.utils.dto.DefaultResponseVo;
import okky.team.chawchaw.utils.message.ResponseBlockMessage;
import okky.team.chawchaw.utils.message.ResponseGlobalMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;
    private final UserService userService;

    @PostMapping("/users/block")
    public ResponseEntity<?> createBlock(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      @Valid @RequestBody CreateBlockDto createBlockDto) {

        userService.validMyself(principalDetails.getId(), createBlockDto.getUserId());

        createBlockDto.setUserFromId(principalDetails.getId());
        createBlockDto.setUserFromEmail(principalDetails.getUsername());

        try {
            blockService.createBlock(createBlockDto);
        } catch (ExistBlockException e) {
            return new ResponseEntity<>(DefaultResponseVo.res(ResponseBlockMessage.B400), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(DefaultResponseVo.res(ResponseGlobalMessage.G200), HttpStatus.OK);
    }

    @DeleteMapping("/users/block")
    public ResponseEntity<?> deleteBlock(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      @Valid @RequestBody DeleteBlockDto deleteBlockDto) {

        deleteBlockDto.setUserFromId(principalDetails.getId());
        deleteBlockDto.setUserFromEmail(principalDetails.getUsername());

        userService.validMyself(deleteBlockDto.getUserFromId(), deleteBlockDto.getUserId());

        try {
            blockService.deleteBlock(deleteBlockDto);
        } catch (NotExistBlockException e) {
            return new ResponseEntity<>(DefaultResponseVo.res(ResponseBlockMessage.B401), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(DefaultResponseVo.res(ResponseGlobalMessage.G200), HttpStatus.OK);
    }

    @GetMapping("/users/block")
    public ResponseEntity<?> findBlockUsers(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<BlockUserDto> result = blockService.findAllByUserFromId(principalDetails.getId());

        return new ResponseEntity<>(DefaultResponseVo.res(ResponseGlobalMessage.G200, result), HttpStatus.OK);
    }

}
