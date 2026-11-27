package com.fixnest.service;
import com.fixnest.model.ChatbotFaq; import com.fixnest.repo.FaqRepository; import org.springframework.stereotype.Service; import java.util.*;

/** Chatbot: intent rules first (reschedule, cancel, agent, call back...), then FAQ keyword matching from the chatbot_faq table. */
@Service
public class ChatService {
  public record Reply(String answer, List<String> actions) {}
  private record Intent(List<String> keys, String answer, List<String> actions) {}

  private static final Set<String> STOP = Set.of("the","and","how","can","what","are","you","for","with","get","does","your","have","this");
  private final FaqRepository faqs;
  private final List<Intent> intents = List.of(
    new Intent(List.of("not available","unavailable","can't make","cannot make","cant make","not free","wrong time","change time","change the time","change slot","change date","another time","different time","other time","reschedule","postpone","busy","shift"),
      "No problem! If you are not available at the booked time, you can move your booking to a new date and time for free (up to 4 hours before the slot). Choose an option below.", List.of("RESCHEDULE","AGENT")),
    new Intent(List.of("cancel"), "Sorry to see you go. You can cancel an active booking below, or reschedule it if the time is the only problem.", List.of("CANCEL","RESCHEDULE","AGENT")),
    new Intent(List.of("call me","call back","callback","phone call","ring me","call"), "Sure, we can call you back. Tell us the best time and our team will ring you.", List.of("CALLBACK","AGENT")),
    new Intent(List.of("agent","human","representative","talk to","speak to","connect me","complaint","support","someone"), "Of course. I will connect you with a support agent. Share your details and they will get in touch.", List.of("AGENT","CALLBACK")),
    new Intent(List.of("money deducted","amount deducted","payment failed","failed payment","charged twice","double charged","refund"), "Sorry about that. Failed payments are reversed in 3 to 5 working days. If money was deducted but your booking is not confirmed, an agent can check it for you.", List.of("AGENT","CALLBACK")),
    new Intent(List.of("not arrived","did not come","didn't come","no show","technician late","late"), "Sorry for the trouble. Our team will look into it right away.", List.of("AGENT","CALLBACK")),
    new Intent(List.of("track","my booking","booking status","status"), "You can see all your bookings, with their payment status, in the My bookings section.", List.of("TRACK","RESCHEDULE")));

  public ChatService(FaqRepository f){ faqs = f; }

  public Reply answer(String msg) {
    String text = msg.toLowerCase().replace('\u2019', '\'').trim();
    Set<String> tokens = new HashSet<>(Arrays.asList(text.split("[^a-z']+")));
    if (text.split("\\s+").length <= 3 && (tokens.contains("hi") || tokens.contains("hello") || tokens.contains("hey") || tokens.contains("hii")))
      return new Reply("Hello! How can I help you today?", List.of("BOOK","TRACK","AGENT"));
    for (Intent i : intents) for (String k : i.keys()) if (matches(text, tokens, k)) return new Reply(i.answer(), i.actions());

    ChatbotFaq best = null; int top = 0;
    for (ChatbotFaq f : faqs.findAll()) {
      String key = (f.getQuestion() + " " + f.getCategory() + " " + f.getAnswer()).toLowerCase(); int score = 0;
      for (String w : tokens) if (w.length() > 2 && !STOP.contains(w) && key.contains(w)) score++;
      if (score > top) { top = score; best = f; }
    }
    if (best != null) return new Reply(best.getAnswer(), List.of("AGENT"));
    return new Reply("I am not sure I understood that. I can connect you with a support agent, or request a call back.", List.of("AGENT","CALLBACK","BOOK"));
  }
  private boolean matches(String text, Set<String> tokens, String key) {
    if (key.contains(" ") || key.contains("'")) return text.contains(key);
    for (String t : tokens) if (t.equals(key) || (key.length() >= 6 && t.startsWith(key))) return true;
    return false;
  }
}
