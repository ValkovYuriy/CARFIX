package yuriy.dev.carfixbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yuriy.dev.carfixbackend.model.Mark;

import java.util.UUID;

@Repository
public interface MarkRepository extends JpaRepository<Mark, UUID> {

}
