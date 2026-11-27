package com.fixnest.service;
import com.fixnest.model.*; import com.fixnest.repo.*; import org.springframework.boot.CommandLineRunner; import org.springframework.stereotype.Component; import java.math.BigDecimal;
@Component
public class DataSeeder implements CommandLineRunner {
  private final ServiceRepository services; private final FaqRepository faqs;
  public DataSeeder(ServiceRepository s, FaqRepository f){ services=s; faqs=f; }
  public void run(String... args) {
    if (services.count() == 0) {
      add("Home Cleaning","🧹","Deep cleaning for kitchen, bath and rooms.",899); add("Plumbing","🚰","Leaks, taps and pipes fixed fast.",399);
      add("Electrical Repair","💡","Wiring, switches and fans fixed safely.",449); add("AC Servicing","❄️","Cleaning, gas check and tune-up.",599); add("Painting","🎨","Interior walls with a clean finish.",1999);
    }
    if (faqs.count() == 0) {
      faqs.save(new ChatbotFaq("How do I book a service?","Pick a service, choose date and time, then pay. You get an instant confirmation.","Booking"));
      faqs.save(new ChatbotFaq("Can I cancel or reschedule a booking?","Yes. Contact us 4 hours before the slot for a free reschedule or cancellation.","Booking"));
      faqs.save(new ChatbotFaq("What payment methods are accepted?","UPI, cards and net banking. In this demo, payment status is simulated.","Payment"));
      faqs.save(new ChatbotFaq("When do I get a refund?","Failed payments are reversed in 3 to 5 working days.","Payment"));
      faqs.save(new ChatbotFaq("What are the service prices?","Prices start at 399 for plumbing. Each service card shows its rate.","Services"));
      faqs.save(new ChatbotFaq("Are the professionals verified?","Every pro is background-checked, trained and rated by customers.","Services"));
      faqs.save(new ChatbotFaq("How do I track my booking?","Use My bookings and enter your phone number.","Booking"));
      faqs.save(new ChatbotFaq("What are your working hours?","We serve daily from 8 AM to 8 PM, including weekends.","General"));
    }
  }
  private void add(String n,String e,String d,int p){ ServiceItem s = new ServiceItem(); s.setName(n); s.setEmoji(e); s.setDescription(d); s.setPrice(BigDecimal.valueOf(p)); services.save(s); }
}
