package edu.java.repository.jpa;

import edu.java.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaLinkRepository extends JpaRepository <Link, Long> {
    Optional<Link> findByUrl(String url);
    @Query(nativeQuery = true,
           value = """
                   SELECT * FROM Link WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - checked_at)) >= :interval
                   ORDER BY checked_at LIMIT :count
                   """)
    List<Link> findAllOutdatedLinks(Long linksLimit, Long timeInterval);
}
