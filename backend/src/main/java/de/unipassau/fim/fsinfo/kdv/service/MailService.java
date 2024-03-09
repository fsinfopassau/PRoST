package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dto.InvoiceDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.HashMap;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  @Value("${MAIL_USER_NAME:Username}")
  private String mailUserName;

  @Value("${MAIL_USER_PASSWORD:password}")
  private String mailUserPassword;

  @Value("${MAIL_HOST_NAME:smtp.test.com}")
  private String mailHostName;

  @Value("${MAIL_HOST_PORT:587}")
  private int mailHostPort;

  @Value("${MAIL_COOLDOWN:60000}")
  private int mailCooldownTime;

  private final HashMap<String, Long> mailCooldown = new HashMap<>();

  @Autowired
  private UserRepository users;


  public boolean sendInvoice(@NonNull InvoiceDTO invoice) {
    Optional<KdvUser> user = users.findById(invoice.getUserId());
    if (user.isEmpty()) {
      return false;
    }

    String text = "Servus " + user.get().getDisplayName() + ",\n" +
        "dein aktueller Kontostand bei der Kaffeekasse beträgt " + KdvUser.formatMoney(
        invoice.getBalance()) + "\n"
        + "Bitte überweise den Betrag mittels PayPal [1] oder gib ihn mir bei der nächsten Gelegenheit persönlich in bar.\n"
        + "\n"
        + "Viele Grüße\n"
        + "Bierjam\n"
        + "\n"
        + "[1] https://paypal.me/fsinfokaffee/" + Math.abs(invoice.getBalance().doubleValue())
        + "\n";

    return sendMail(user.get().getEmail(), "Kaffeekasse - Neue Abrechnung", text);
  }


  /**
   * Sent Mail over predefined host and user.
   *
   * @param address The receiver.
   * @param subject The title-text shown in the mailclient.
   * @param text    The mail-Content.
   * @return true if mail could be sent out.
   */
  private boolean sendMail(String address, String subject, String text) {
    if (mailCooldown.containsKey(address)) {
      if (mailCooldown.get(address) < System.currentTimeMillis()) {
        mailCooldown.remove(address);
      } else {
        return false;
      }
    }

    System.out.println("[MS] :: send mail :: " + address + " |\n" + subject + " |\n" + text);

    MultiPartEmail email = new MultiPartEmail();
    try {
      email.setHostName(mailHostName);
      email.setSmtpPort(mailHostPort);
      email.setAuthentication(mailUserName, mailUserPassword);
      email.setFrom(mailUserName);
      email.addTo(address);
      email.setMsg(text);
      email.setSSLOnConnect(true);
      email.setStartTLSEnabled(true);
      email.setSubject(subject);

      email.send();
      mailCooldown.put(address, System.currentTimeMillis() + mailCooldownTime);
    } catch (EmailException e) {
      return false;
    }
    return true;
  }

}
