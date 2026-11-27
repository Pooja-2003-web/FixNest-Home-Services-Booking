package com.fixnest.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="chatbot_faq") @Getter @Setter @NoArgsConstructor
public class ChatbotFaq {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String question;
  @Column(nullable=false, length=500) private String answer;
  private String category;
  public ChatbotFaq(String q,String a,String c){ this.question=q; this.answer=a; this.category=c; }
}
