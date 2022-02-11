package com.workmotion.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@Slf4j
public class PingController
{
    @GetMapping("/ping")
    public ResponseEntity<String> ping()
    {
        try {
            return new ResponseEntity<>("pong", HttpStatus.OK);
        } catch (Exception e) {
            log.error(
                String.format(
                    "An error occurred while pinging the API. Message: %s. Trace: %s ",
                    e.getMessage(),
                    e.getStackTrace()
                )
            );
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
