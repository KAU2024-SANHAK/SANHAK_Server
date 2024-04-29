package org.kau.kkoolbeeServer.domain.advice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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


    String kind_advice;

    String spicy_advice;

    public Advice(String kind_advice, String spicy_advice) {
        this.kind_advice = kind_advice;
        this.spicy_advice = spicy_advice;
    }
}
