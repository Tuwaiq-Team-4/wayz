package com.example.wayz.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_trips")
public class UserTrips {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userTrips")
    private Set<StudentTrips> studentTrips;


    @ManyToOne
    @JsonIgnore
    private Driver driver;

    @Column(nullable = false)
    private String status;

}
