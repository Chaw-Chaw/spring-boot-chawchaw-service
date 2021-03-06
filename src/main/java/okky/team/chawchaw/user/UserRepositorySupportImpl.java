package okky.team.chawchaw.user;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import okky.team.chawchaw.user.dto.FindUserVo;
import okky.team.chawchaw.user.dto.UserCardDto;
import okky.team.chawchaw.user.language.QLanguageEntity;
import okky.team.chawchaw.user.language.QUserHopeLanguageEntity;
import okky.team.chawchaw.user.language.QUserLanguageEntity;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositorySupportImpl implements UserRepositorySupport{

    private final JPAQueryFactory jpaQueryFactory;

    QUserEntity user = QUserEntity.userEntity;
    QUserLanguageEntity userLanguage = QUserLanguageEntity.userLanguageEntity;
    QUserHopeLanguageEntity userHopeLanguage = QUserHopeLanguageEntity.userHopeLanguageEntity;
    QLanguageEntity language = QLanguageEntity.languageEntity;

    @Override
    public List<UserCardDto> findAllByElement(FindUserVo findUserVo) {

        int limit = 0;

        if (findUserVo.getIsFirst())
            limit = 6;
        else
            limit = 3;

        return jpaQueryFactory
                .select(Projections.constructor(
                        UserCardDto.class,
                        user.id,
                        user.name,
                        user.imageUrl,
                        user.content,
                        user.repCountry,
                        user.repLanguage,
                        user.repHopeLanguage,
                        user.regDate,
                        user.likeTo.size()
                ))
                .from(user)
                .where(
                        /* 구사할 수 있는 언어 */
                        StringUtils.hasText(findUserVo.getLanguage()) ?
                                user.in(
                                        JPAExpressions
                                                .select(userLanguage.user)
                                                .from(userLanguage)
                                                .join(userLanguage.user, user)
                                                .join(userLanguage.language, language)
                                                .where(userLanguage.language.abbr.eq(findUserVo.getLanguage()))
                                ) : null,
                        /* 배우길 희망하는 언어 */
                        StringUtils.hasText(findUserVo.getHopeLanguage()) ?
                                user.in(
                                        JPAExpressions
                                                .select(userHopeLanguage.user)
                                                .from(userHopeLanguage)
                                                .join(userHopeLanguage.user, user)
                                                .join(userHopeLanguage.hopeLanguage, language)
                                                .where(userHopeLanguage.hopeLanguage.abbr.eq(findUserVo.getHopeLanguage()))
                                ) : null,
                        /* 학교 */
                        StringUtils.hasText(findUserVo.getSchool()) ? user.school.eq(findUserVo.getSchool()) : null,
                        /* 이름 */
                        StringUtils.hasText(findUserVo.getName()) ? user.name.contains(findUserVo.getName()) : null,
                        /* 제외 목록 */
                        findUserVo.getExclude() != null && !findUserVo.getExclude().isEmpty() ?
                                user.id.notIn(findUserVo.getExclude()) : null,
                        /* 유저 */
                        user.role.eq(Role.USER)
                )
                .orderBy(getSortedColumn(findUserVo.getSort()))
                .limit(limit)
                .fetch();
    }

    private OrderSpecifier<?> getSortedColumn(String sort) {
        if (StringUtils.hasText(sort)) {
            if (sort.equals("like")) {
                return user.likeTo.size().desc();
            }
            else if (sort.equals("view")) {
                return user.views.desc();
            }
            else if (sort.equals("date")) {
                return user.regDate.desc();
            }
        }
        return Expressions.numberTemplate(Double.class, "function('rand')").asc();
    }

}
