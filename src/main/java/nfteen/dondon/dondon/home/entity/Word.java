package nfteen.dondon.dondon.home.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "word")
public class Word {

    @Id
    @Column(nullable = false)
    private int num;

    @Column(length = 50, nullable = false)
    private String subject;

    @Column(length = 100, nullable = false)
    private String word;

    @Column(length = 1000, nullable = false)
    private String description;
}