package yuriy.dev.carfixbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yuriy.dev.carfixbackend.model.Work;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {

    @Override
    @Query("select new Work(w.id,w.name,w.description,wp.price,w.imageUrl) from Work w join WorkPrice wp on w.id = wp.work.id " +
            "where wp.date = (SELECT MAX(wp2.date) from WorkPrice wp2 where wp2.work.id = w.id)")
    List<Work> findAll();

    @Query("select new Work(w.id,w.name,w.description,wp.price,w.imageUrl) from Work w join WorkPrice wp on w.id = wp.work.id " +
            "where w.id = :id and wp.date = (select MAX(wp2.date) from WorkPrice wp2 where wp2.work.id = :id) ")
    Optional<Work> findByIdWithLatestPrice(UUID id);
}
