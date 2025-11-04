package htw.webtech.rest.controller;

import htw.webtech.business.service.FatSecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fatsecret")
@RequiredArgsConstructor
public class FatSecretController {

    private final FatSecretService service;

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam("q") String q,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.searchFoods(q, page, size);
    }

    // Mini-Healthcheck, um 404 zu vermeiden
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok");
    }
}