package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Message;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.graph.messages.MessageQuery;
import cricket.merstham.website.graph.messages.SaveMessageMutation;
import cricket.merstham.website.graph.type.MessageInput;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);
    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public MessageService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public Message getMessage(String key) throws IOException {
        var query = MessageQuery.builder().key(key).build();
        Response<MessageQuery.Data> result = graphService.executeQuery(query);
        if (result.hasErrors()) {
            return Message.builder().key(key).build();
        }
        return modelMapper.map(result.getData().getMessage(), Message.class);
    }

    public Message saveMessage(OAuth2AccessToken accessToken, Message message) throws IOException {
        var input =
                MessageInput.builder()
                        .key(message.getKey())
                        .messageClass(message.getMessageClass())
                        .messageText(message.getMessageText())
                        .enabled(message.isEnabled())
                        .startDate(message.getStartDate())
                        .endDate(message.getEndDate())
                        .build();
        var mutation = SaveMessageMutation.builder().message(input).build();

        Response<SaveMessageMutation.Data> result =
                graphService.executeMutation(mutation, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving message",
                    result.getErrors().stream().map(Error::getMessage).toList());
        }
        return modelMapper.map(result.getData().getSaveMessage(), Message.class);
    }
}
