package yuriy.dev.carfixbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yuriy.dev.carfixbackend.model.Work;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {

    @Query("select w from Work w join WorkPrice wp on w.id = wp.work.id " +
            "where w.id = :id and wp.date = (SELECT MAX(wp2.date) from WorkPrice wp2 where wp2.work.id = :id) ")
    Optional<Work> findByIdWithLatestPrice(UUID id);
}
