package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.MessageEntity;
import cricket.merstham.graphql.repository.MessageEntityRepository;
import cricket.merstham.shared.dto.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static cricket.merstham.graphql.configuration.CacheConfiguration.MESSAGE_BY_KEY_CACHE;

@Service
public class MessageService {

    private final MessageEntityRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public MessageService(MessageEntityRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public List<Message> getMessages() {
        return repository.findAll().stream().map(m -> modelMapper.map(m, Message.class)).toList();
    }

    @Cacheable(value = MESSAGE_BY_KEY_CACHE, key = "#key")
    public Message getMessage(String key) {
        return repository.findById(key).map(m -> modelMapper.map(m, Message.class)).orElseThrow();
    }

    @Caching(evict = {@CacheEvict(value = MESSAGE_BY_KEY_CACHE, key = "#message.key")})
    public Message save(Message message) {
        var entity = repository.findById(message.getKey()).orElseGet(MessageEntity::new);
        modelMapper.map(message, entity);
        return modelMapper.map(repository.saveAndFlush(entity), Message.class);
    }
}
