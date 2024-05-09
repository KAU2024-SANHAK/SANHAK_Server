package org.kau.kkoolbeeServer.domain.advice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Advice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "LONGTEXT")
    String kind_advice;

    @Column(columnDefinition = "LONGTEXT")
    String spicy_advice;

    public Advice(String kind_advice, String spicy_advice) {
        this.kind_advice = kind_advice;
        this.spicy_advice = spicy_advice;
    }
}
