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
@Table(name = "quiz")
public class Quiz {

    @Id
    @Column(nullable = false)
    private int code;

    @Column(length = 50, nullable = false)
    private String type;

    @Column(length = 500, nullable = false)
    private String quiz;

    @Column(length = 500)
    private String a1;

    @Column(length = 500)
    private String a2;

    @Column(length = 500)
    private String a3;

    @Column(length = 500)
    private String a4;

    @Column(length = 10)
    private String result;

    @Column(length = 700)
    private String content;
}