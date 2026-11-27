package com.fixnest.model;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
/** Call-back / agent requests raised from the chatbot. */
@Entity @Table(name="support_requests") @Getter @Setter
public class SupportRequest {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long customerId;
  private String name;
  private String phone;
  private String type;          // CALLBACK or AGENT
  private String preferredTime;
  @Column(length=500) private String message;
  private String status = "OPEN"; // OPEN, IN_PROGRESS, RESOLVED
  private LocalDateTime createdAt = LocalDateTime.now();
}
