package com.fixnest.model;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import com.fasterxml.jackson.annotation.JsonIgnore; import java.util.*;
@Entity @Table(name="services") @Getter @Setter
public class ServiceItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  private String emoji;
  private String description;
  @Column(nullable=false) private BigDecimal price;
  private boolean active = true;
  @OneToMany(mappedBy="service") @JsonIgnore private List<Booking> bookings = new ArrayList<>(); // 1 service -> many bookings
}
