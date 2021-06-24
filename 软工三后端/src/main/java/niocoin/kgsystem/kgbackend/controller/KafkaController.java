package niocoin.kgsystem.kgbackend.controller;

import niocoin.kgsystem.kgbackend.service.NREService;
import niocoin.kgsystem.kgbackend.util.Res;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/kafka")
public class KafkaController {

    private final KafkaTemplate<String, String> template;
    private final NREService nreService;

    public KafkaController(KafkaTemplate<String, String> template, NREService nreService) {
        this.template = template;
        this.nreService = nreService;
    }

    @GetMapping("/")
    public Res kafkaSending() {
        template.send("java2py", "Hello, Python");
        return Res.ok();
    }

    @KafkaListener(id = "ID", topics = "py2java")
    public void listenKafka(String in) {
        System.out.println(in);
        nreService.transformJson(in);
    }

}
