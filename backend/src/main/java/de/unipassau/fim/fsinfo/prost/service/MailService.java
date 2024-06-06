package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.DataFilter;
import de.unipassau.fim.fsinfo.prost.data.dao.InvoiceEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dto.InvoiceAmountMapping;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  @Value("${MAIL_SENDER_ADDR:addr@test.com}")
  private String mailSenderAddr;

  @Value("${MAIL_USER_NAME:}")
  private String mailUserName;

  @Value("${MAIL_USER_PASSWORD:}")
  private String mailUserPassword;

  @Value("${MAIL_HOST_NAME:smtp.test.com}")
  private String mailHostName;

  @Value("${MAIL_USE_SSL:true}")
  private boolean useSsl;

  @Value("${MAIL_HOST_PORT:587}")
  private int mailHostPort;

  @Value("${MAIL_COOLDOWN:60000}")
  private int mailCooldownTime;

  private final HashMap<String, Long> mailCooldown = new HashMap<>();

  private final UserRepository users;
  private final ShopItemRepository items;

  @Autowired
  public MailService(UserRepository users, ShopItemRepository items) {
    this.users = users;
    this.items = items;
  }

  public boolean sendInvoice(@NonNull InvoiceEntry invoice, List<InvoiceAmountMapping> amounts) {
    Optional<ProstUser> user = users.findById(invoice.getUserId());
    if (user.isEmpty()) {
      System.err.println(
          "[MS] :: invoice=" + invoice.getId() + " :: no user \"" + invoice.getUserId() + "\"!");
      return false;
    }

    if (invoice.isMailed()) {
      System.err.println("[MS] :: invoice=" + invoice.getId() + " :: already sent!");
      return false;
    }

    String text = "Servus " + user.get().getDisplayName() + ",\n\n" +
        "dein aktueller Kontostand bei der Kaffeekasse beträgt " + DataFilter.formatMoney(
        invoice.getBalance()) + ".\n"
        + formattedAmounts(amounts);

    if (invoice.getBalance().compareTo(BigDecimal.ZERO) < 0) {
      text +=
          "\nBitte überweise den Betrag mittels PayPal [1] oder gib ihn mir bei der nächsten Gelegenheit persönlich in bar.\n"
              + "\n"
              + "Viele Grüße\n"
              + "Bierjam\n"
              + "\n"
              + "[1] https://paypal.me/fsinfokaffee/" + Math.abs(invoice.getBalance().doubleValue())
              + "\n";
    } else {
      text += "\nViele Grüße\nBierjam\n\n";
    }

    if (sendMail(user.get().getEmail(), "Kaffeekasse - Neue Abrechnung", text)) {
      System.out.println("[MS] :: invoice=" + invoice.getId() + " :: sent new invoice!");
      return true;
    } else {
      System.err.println("[MS] :: invoice=" + invoice.getId() + " :: did not arrive!");
      return false;
    }
  }

  /**
   * Sent Mail over predefined host and user.
   *
   * @param address The receiver.
   * @param subject The title-text shown in the mailclient.
   * @param text The mail-Content.
   * @return true if mail could be sent out.
   */
  private boolean sendMail(String address, String subject, String text) {
    if (address == null) {
      System.err.println("[MS] :: no address!");
      return false;
    }
    if (subject == null) {
      System.err.println("[MS] :: no subject!");
      return false;
    }
    if (text == null) {
      System.err.println("[MS] :: no text!");
      return false;
    }

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
      if (mailUserName != null && !mailUserName.isEmpty()) {
        email.setAuthentication(mailUserName, mailUserPassword == null ? "" : mailUserPassword);
      }
      email.setFrom(mailSenderAddr);
      email.addTo(address);
      email.setMsg(text);
      if (useSsl) {
        email.setSSLOnConnect(true);
        email.setStartTLSEnabled(true);
      }
      email.setSubject(subject);

      email.send();
      mailCooldown.put(address, System.currentTimeMillis() + mailCooldownTime);
    } catch (EmailException e) {
      return false;
    }
    return true;
  }

  public String formattedAmounts(List<InvoiceAmountMapping> amounts) {

    StringBuilder b = new StringBuilder();

    if (amounts != null && !amounts.isEmpty()) {
      b.append("\n");
      b.append("Deine Einkäufe seit letzter Abrechnung:");
      b.append("\n");

      for (InvoiceAmountMapping mapping : amounts) {

        Optional<ShopItem> item = items.findById(mapping.getItemId());
        String itemName = mapping.getItemId();

        if (item.isPresent()) {
          itemName = item.get().getDisplayName();
        }

        int amount = mapping.getAmount();

        if (amount < 10) {
          b.append(" ");
        }
        b.append(itemName);
        b.append(" >> ");
        b.append(amount);
        b.append(" x ");
        b.append(DataFilter.formatMoney(mapping.getSingeItemPrice()));
        b.append("\n");
      }
    }

    return b.toString();
  }

}
