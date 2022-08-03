package cwi.desafio.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cwi.desafio.backend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
  
}