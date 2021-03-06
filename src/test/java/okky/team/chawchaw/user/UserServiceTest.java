package okky.team.chawchaw.user;

import okky.team.chawchaw.like.LikeService;
import okky.team.chawchaw.like.dto.CreateLikeDto;
import okky.team.chawchaw.user.country.UserCountryRepository;
import okky.team.chawchaw.user.dto.*;
import okky.team.chawchaw.user.language.UserHopeLanguageRepository;
import okky.team.chawchaw.user.language.UserLanguageRepository;
import okky.team.chawchaw.user.exception.DuplicationUserEmailException;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserCountryRepository userCountryRepository;
    @Autowired
    private UserLanguageRepository userLanguageRepository;
    @Autowired
    private UserHopeLanguageRepository userHopeLanguageRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 회원가입() throws Exception {
        //given
        CreateUserDto createUserDto = CreateUserDto.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build();
        //when
        userService.createUser(createUserDto);
        //then
        List<UserEntity> result = userRepository.findAll();

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getEmail()).isEqualTo("mangchhe@naver.com");
        Assertions.assertThat(result.get(0).getRole()).isEqualTo(Role.GUEST);
        Assertions.assertThat(result.get(0).getImageUrl()).isEqualTo("default.png");
        Assertions.assertThat(result.get(0).getViews()).isEqualTo(0);
    }

    @Test
    public void 회원중복() throws Exception {
        //given
        userRepository.save(UserEntity.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build());
        //when, then
        Assertions.assertThatExceptionOfType(DuplicationUserEmailException.class).isThrownBy(() -> {
            userService.duplicateEmail("mangchhe@naver.com");
        });
        userService.duplicateEmail("mangchhe2@naver.com");
    }

    @Test
    public void 회원삭제() throws Exception {
        // given
        UserEntity user = userRepository.save(UserEntity.builder()
                .email("mangchhe@naver.com")
                .password(passwordEncoder.encode("1234"))
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build());
        //when
        userService.deleteUser(user.getId());
        //then
        List<UserEntity> users = userRepository.findAll();

        Assertions.assertThat(users.size()).isEqualTo(0);
    }

    @Test
    public void 프로필_수정() throws Exception {
        //given
        UserEntity user = userRepository.save(UserEntity.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build());
        //when
        userService.updateProfile(UpdateUserDto.builder()
                .id(user.getId())
                .content("내용2")
                .facebookUrl("페이스북주소2")
                .instagramUrl("인스타그램주소2")
                .repCountry("Samoa")
                .repLanguage("cy")
                .repHopeLanguage("am")
                .country(Sets.newHashSet(Arrays.asList(
                        "United States",
                        "South Korea",
                        "Samoa",
                        "Kosovo"
                )))
                .language(Sets.newHashSet(Arrays.asList(
                        "fy",
                        "xh",
                        "wo",
                        "cy"
                )))
                .hopeLanguage(Sets.newHashSet(Arrays.asList(
                        "ab",
                        "aa",
                        "sq",
                        "am"
                )))
                .build());

        //then
        List<String> countrys = userCountryRepository.findAll().stream().map(x -> x.getCountry().getName()).collect(Collectors.toList());
        List<String> languages = userLanguageRepository.findAll().stream().map(x -> x.getLanguage().getAbbr()).collect(Collectors.toList());
        List<String> hopeLanguages = userHopeLanguageRepository.findAll().stream().map(x -> x.getHopeLanguage().getAbbr()).collect(Collectors.toList());

        Assertions.assertThat(countrys)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("South Korea", "United States", "Samoa", "Kosovo"));
        Assertions.assertThat(languages)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("fy", "xh", "wo", "cy"));
        Assertions.assertThat(hopeLanguages)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("ab", "aa", "sq", "am"));
        Assertions.assertThat(user.getFacebookUrl()).isEqualTo("페이스북주소2");
        Assertions.assertThat(user.getInstagramUrl()).isEqualTo("인스타그램주소2");
        Assertions.assertThat(user.getContent()).isEqualTo("내용2");
        Assertions.assertThat(user.getRepCountry()).isEqualTo("Samoa");
        Assertions.assertThat(user.getRepLanguage()).isEqualTo("cy");
        Assertions.assertThat(user.getRepHopeLanguage()).isEqualTo("am");
        Assertions.assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @Test
    public void 카드조회() throws Exception {
        //given
        for (int i = 0; i < 3; i++) {
            UserEntity user = userRepository.save(UserEntity.builder()
                    .email("mangchhe" + String.valueOf(i) + "@naver.com")
                    .password("1234")
                    .name("이름")
                    .web_email("웹메일")
                    .school("학교")
                    .build());
            userService.updateProfile(UpdateUserDto.builder()
                    .id(user.getId())
                    .content("내용" + String.valueOf(i))
                    .facebookUrl("페이스북주소")
                    .instagramUrl("인스타그램주소")
                    .language(Sets.newHashSet(Arrays.asList(
                            "fy",
                            "xh",
                            "yi",
                            "yo"
                    )))
                    .hopeLanguage(Sets.newHashSet(Arrays.asList(
                            "ab",
                            "aa",
                            "af",
                            "ak"
                    )))
                    .country(Sets.newHashSet(Arrays.asList(
                            "United States",
                            "South Korea",
                            "Zambia",
                            "Zimbabwe"
                    )))
                    .build());
        }
        List<UserEntity> users = userRepository.findAll();
        for (int i = 0; i < 2; i++) {
            for (int j = i + 1; j < 3; j++) {
                likeService.addLike(new CreateLikeDto(users.get(i).getId(), users.get(i).getName(), users.get(j).getId()));
            }
        }
        //when
        List<UserCardDto> result = userRepository.findAllByElement(FindUserVo.builder()
                .language("yi")
                .hopeLanguage("ab")
                .build());
        //then
        Assertions.assertThat(result).extracting("content")
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("내용0", "내용1", "내용2"));
        Assertions.assertThat(result).extracting("follows")
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList(0, 1, 2));
    }

    @Test
    public void 카드상세보기() throws Exception {
        //given
        UserEntity user = userRepository.save(UserEntity.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build());
        userService.updateProfile(UpdateUserDto.builder()
                .id(user.getId())
                .content("내용")
                .facebookUrl("페이스북주소")
                .instagramUrl("인스타그램주소")
                .language(Sets.newHashSet(Arrays.asList(
                        "fy",
                        "xh",
                        "yi",
                        "yo"
                )))
                .hopeLanguage(Sets.newHashSet(Arrays.asList(
                        "ab",
                        "aa",
                        "af",
                        "ak"
                )))
                .country(Sets.newHashSet(Arrays.asList(
                        "United States",
                        "South Korea",
                        "Zambia",
                        "Zimbabwe"
                )))
                .build());
        //when
        UserDetailsDto result = userService.findUserDetails(user.getId());
        //then
        Assertions.assertThat(result.getName()).isEqualTo("이름");
        Assertions.assertThat(result.getContent()).isEqualTo("내용");
        Assertions.assertThat(result.getFacebookUrl()).isEqualTo("페이스북주소");
        Assertions.assertThat(result.getInstagramUrl()).isEqualTo("인스타그램주소");
        Assertions.assertThat(result.getLikes()).isEqualTo(0);
        Assertions.assertThat(result.getCountry())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("United States", "South Korea", "Zambia", "Zimbabwe"));
        Assertions.assertThat(result.getLanguage())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("fy", "xh", "yi", "yo"));
        Assertions.assertThat(result.getHopeLanguage())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(Arrays.asList("ab", "aa", "af", "ak"));

    }

    @Test
    public void 조회수_조회() throws Exception {
        //given
        UserEntity user = userRepository.save(UserEntity.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build());
        long cache = 0;
        long noneCache = 0;
        //when
        long beforeTime = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            userRepository.findViewsByUserId(1L);
        }
        noneCache = (System.currentTimeMillis() - beforeTime) / 1000;
        beforeTime = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            userService.getViews(1L);
        }
        cache = (System.currentTimeMillis() - beforeTime) / 1000;
        // then
        Assertions.assertThat(noneCache > cache).isTrue();
    }

    @Test
    public void 중복_조회() throws Exception {
        //given
        UserEntity user = userRepository.save(UserEntity.builder()
                .email("mangchhe@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build());
        UserEntity user2 = userRepository.save(UserEntity.builder()
                .email("mangchhe2@naver.com")
                .password("1234")
                .name("이름")
                .web_email("웹메일")
                .school("학교")
                .build());
        //when
        userService.checkView(user.getId(), user2.getId());
        userService.checkView(user.getId(), user2.getId());
        userService.checkView(user.getId(), user2.getId());
        //then
        Assertions.assertThat(userService.getViews(user2.getId())).isEqualTo(1L);
        Assertions.assertThat(redisTemplate.opsForValue().get("viewDuplex::" + user.getId() + "_" + user2.getId())).isEqualTo(1);
        Assertions.assertThat(redisTemplate.opsForValue().get("views::" + user2.getId())).isEqualTo(1L);
    }

    @Test
    public void 조회수_업데이트() throws Exception {
        //given
        UserEntity[] users = new UserEntity[3];
        for (int i = 0; i < 3; i++) {
            users[i] = userRepository.save(UserEntity.builder()
                    .email("mangchhe" + i +"@naver.com")
                    .password("1234")
                    .name("이름")
                    .web_email("웹메일")
                    .school("학교")
                    .build());
        }
        userService.checkView(users[1].getId(), users[0].getId());
        userService.checkView(users[0].getId(), users[1].getId());
        userService.checkView(users[2].getId(), users[1].getId());
        //when
        userService.updateViews();
        //then
        Assertions.assertThat(userRepository.findById(users[0].getId()).get().getViews()).isEqualTo(1);
        Assertions.assertThat(userRepository.findById(users[1].getId()).get().getViews()).isEqualTo(2);
        Assertions.assertThat(userRepository.findById(users[2].getId()).get().getViews()).isEqualTo(0);
    }
}