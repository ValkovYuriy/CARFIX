package yuriy.dev.carfixbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuriy.dev.carfixbackend.model.Work;

import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {
}
