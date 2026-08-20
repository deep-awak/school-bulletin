package api.poja.app.service.event;

import api.poja.app.endpoint.event.model.TranscriptEmailRequested;
import api.poja.app.mail.Email;
import api.poja.app.mail.Mailer;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * Consumer for {@link TranscriptEmailRequested}, invoked by the existing POJA worker mechanism
 * (see EventServiceInvoker). Reuses the pre-configured {@link Mailer} instead of building a
 * parallel email system.
 */
@Service
@AllArgsConstructor
public class TranscriptEmailRequestedService implements Consumer<TranscriptEmailRequested> {
  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(TranscriptEmailRequested event) {
    InternetAddress to = parse(event.getTo());
    String subject = "Votre relevé de notes - " + event.getAcademicYear();
    String body =
        "Bonjour "
            + event.getStudentFullName()
            + ",\n\nVotre relevé de notes pour l'année "
            + event.getAcademicYear()
            + " est disponible ici :\n"
            + event.getTranscriptUrl()
            + "\n\nCordialement,\nHEI";
    mailer.accept(new Email(to, List.of(), List.of(), subject, body, List.of()));
  }

  private InternetAddress parse(String address) throws AddressException {
    return new InternetAddress(address);
  }
}
