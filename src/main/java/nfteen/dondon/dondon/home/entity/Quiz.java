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

    @Column(updatable = false, insertable = false, nullable = false)
    private int code; // 번호

    @Column(updatable = false, insertable = false, nullable = false, length = 50)
    private String type; // 구분

    @Column(updatable = false, insertable = false, nullable = false, length = 500)
    private String quiz; // 문제내용

    @Column(updatable = false, insertable = false, length = 500)
    private String a1; // 보기1

    @Column(updatable = false, insertable = false, length = 500)
    private String a2; // 보기2

    @Column(updatable = false, insertable = false, length = 500)
    private String a3; // 보기3

    @Column(updatable = false, insertable = false, length = 500)
    private String a4; // 보기4

    @Column(updatable = false, insertable = false, length = 10)
    private String result; // 정답

    @Column(updatable = false, insertable = false, length = 700)
    private String content; // 해설
}