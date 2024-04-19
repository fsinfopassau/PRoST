package de.unipassau.fim.fsinfo.prost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class PRoSTApplication {

  public static void main(String[] args) {
    SpringApplication.run(PRoSTApplication.class, args);
  }

}
