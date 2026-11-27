package com.fixnest.model;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.*;
@Entity @Table(name="bookings") @Getter @Setter
public class Booking {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) private Customer customer;
  @ManyToOne(optional=false) private ServiceItem service;
  @Column(nullable=false) private LocalDate bookingDate;
  @Column(nullable=false) private String timeSlot;
  @Column(nullable=false) private String address;
  @Enumerated(EnumType.STRING) private BookingStatus status = BookingStatus.PENDING;
  private BigDecimal amount;
  private LocalDateTime createdAt = LocalDateTime.now();
  @OneToOne(mappedBy="booking", cascade=CascadeType.ALL) private Payment payment; // 1 booking -> 1 payment
}
