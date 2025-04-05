package yuriy.dev.carfixbackend.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "gov_number",unique = true)
    private String govNumber;

    @Column(name = "vin_number",unique = true)
    private String vinNumber;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;


}



