package yuriy.dev.carfixbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuriy.dev.carfixbackend.model.WorkPrice;

import java.util.UUID;

@Repository
public interface WorkPriceRepository extends JpaRepository<WorkPrice, UUID> {

    void deleteAllByWorkId(UUID workId);
}
