package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationWithRank;
import roomescape.domain.RoomTheme;
import roomescape.domain.Status;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    Optional<List<Reservation>> findByThemeIdAndStatusIn(Long themeId, List<Status> statuses); //TODO 상태값 관리 고민

    Optional<Reservation> findByDateAndTimeAndThemeAndMemberAndStatusIn(
            LocalDate date,
            ReservationTime time,
            RoomTheme theme,
            Member member,
            List<Status> statuses);

    @Query("""
        SELECT new roomescape.domain.ReservationWithRank(
            r1, CAST((SELECT COUNT(r2) FROM Reservation r2
                     where r2.theme = r1.theme
                     AND r2.date = r1.date
                     AND r2.time = r1.time
                     AND r2.createdAt < r1.createdAt
                     AND r2.status IN :statuses) AS Long))
        FROM Reservation r1
        WHERE r1.member.id = :memberId and r1.status IN :statuses
        order by r1.date
        """)
    List<ReservationWithRank> findMyReservationsAndStatusIn(Long memberId, List<Status> statuses);

    Optional<Reservation> findByIdAndStatus(Long id, Status status);

    List<Reservation> findAllAndStatusIn(List<Status> statuses);
}
