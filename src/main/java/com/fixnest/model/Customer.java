package com.fixnest.model;
import jakarta.persistence.*; import lombok.*; import java.util.*; import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name="customers") @Getter @Setter
public class Customer {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  @Column(nullable=false, unique=true) private String phone;
  private String email;
  @JsonIgnore private String passwordHash; // BCrypt hash, never sent to the browser
  @OneToMany(mappedBy="customer") @JsonIgnore private List<Booking> bookings = new ArrayList<>();
}
