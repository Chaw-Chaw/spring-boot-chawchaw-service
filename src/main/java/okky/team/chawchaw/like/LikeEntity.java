package okky.team.chawchaw.like;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import okky.team.chawchaw.user.UserEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicInsert
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;
    @Column(nullable = false, insertable = false, updatable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_from")
    private UserEntity userFrom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_to")
    private UserEntity userTo;

    public LikeEntity(UserEntity userFrom, UserEntity userTo) {
        this.userFrom = userFrom;
        this.userTo = userTo;
    }
}
