package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.dao.ShopItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

  @GetMapping("/item")
  public ResponseEntity<String> list() {
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/item/consume")
  public ResponseEntity<String> consume(@RequestBody ShopItem item) {
    return ResponseEntity.noContent().build();
  }

}
