package com.fixnest.model;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime; import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name="payments") @Getter @Setter
public class Payment {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @OneToOne(optional=false) @JoinColumn(unique=true) @JsonIgnore private Booking booking;
  private BigDecimal amount;
  private String method;
  @Enumerated(EnumType.STRING) private PaymentStatus status = PaymentStatus.PENDING; // simulated
  private LocalDateTime createdAt = LocalDateTime.now();
}
