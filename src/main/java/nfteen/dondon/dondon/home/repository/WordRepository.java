package nfteen.dondon.dondon.home.repository;

import nfteen.dondon.dondon.home.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {
}
