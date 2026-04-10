package es.urjc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import es.urjc.model.Lists;
import es.urjc.model.User;
import java.util.List;

public interface ListsRepository extends JpaRepository<Lists, Long> {
    
    List<Lists> findByOwner(User owner);
    
}
