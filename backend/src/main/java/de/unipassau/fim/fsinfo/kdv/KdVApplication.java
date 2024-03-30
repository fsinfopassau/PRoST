package de.unipassau.fim.fsinfo.kdv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class KdVApplication {

  public static void main(String[] args) {
    SpringApplication.run(KdVApplication.class, args);
  }

}
