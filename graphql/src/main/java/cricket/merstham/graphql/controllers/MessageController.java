package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.MessageService;
import cricket.merstham.shared.dto.Message;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @QueryMapping
    public List<Message> messages() {
        return service.getMessages();
    }

    @QueryMapping
    public Message message(@Argument("key") String key) {
        return service.getMessage(key);
    }

    @MutationMapping
    public Message saveMessage(@Argument("message") Message message) {
        return service.save(message);
    }
}
