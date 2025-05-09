package yuriy.dev.carfixbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yuriy.dev.carfixbackend.dto.PopularWork;
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


    @Query("SELECT new Work(w.id,w.name,w.description,wp.price,w.imageUrl)" +
            "FROM Work w " +
            "JOIN WorkPrice wp ON w.id = wp.work.id " +
            "WHERE wp.date = (SELECT MAX(wp2.date) FROM WorkPrice wp2 WHERE wp2.work.id = w.id) " +
            "AND w.id IN (SELECT ow.id FROM Order o JOIN o.works ow )")
    List<Work> findAllWorksWithPricesForEveryOrder();

    @Query("SELECT new yuriy.dev.carfixbackend.dto.PopularWork(w.name,w.imageUrl, COUNT(*)) " +
            "FROM Order o JOIN o.works w " +
            "WHERE EXTRACT(YEAR FROM o.orderDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "GROUP BY w.name,w.imageUrl " +
            "ORDER BY COUNT(*) DESC " +
            "LIMIT 5")
    List<PopularWork> findMostPopularWorksOfTheYear();

    @Query("SELECT new Work(w.id,w.name,w.description,wp.price,w.imageUrl) from Work w join WorkPrice wp on w.id = wp.work.id " +
                      "where w.name = :name and wp.date = (select MAX(wp2.date) from WorkPrice wp2 where wp2.work.id = w.id) ")
    Work findByWorkName(String name);
}
