package com.example.airlabproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "country")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    @Id
    @Column(length = 2)
    private String code;

    @Column(length = 3)
    private String code3;

    private String name;

    @ManyToOne
    @JoinColumn(name = "continent_id")
    private Continent continent;
}
