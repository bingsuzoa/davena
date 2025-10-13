package com.davena.notify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotifyController {

    @PostMapping("/notify")
    public ResponseEntity<String> notify(@RequestBody String body) {
        return ResponseEntity.ok("received");
    }
}
