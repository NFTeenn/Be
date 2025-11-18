package nfteen.dondon.dondon.home.repository;

import nfteen.dondon.dondon.home.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

}