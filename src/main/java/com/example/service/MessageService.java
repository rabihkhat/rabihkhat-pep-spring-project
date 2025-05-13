package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    
    @Autowired
    MessageRepository messageRepository;

    public boolean isValid(Message message) {
        return message.getMessageText() != null && !message.getMessageText().isBlank() && message.getMessageText().length() <= 255;
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }    

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public Message findById(int id) {
        return messageRepository.findById(id).orElse(null);
    }

    public int deleteById(int id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return 1;
        }
        return 0;
    }

    public boolean existsById(int id) {
        return messageRepository.existsById(id);
    }

    public int updateById (int id, String text) {
        if (messageRepository.existsById(id)) {
            Message message = findById(id);
            message.setMessageText(text);
            messageRepository.save(message);
            return 1;
        }
        return 0;
    }

    public List<Message> findByPostedBy(Integer postedBy) {
        return messageRepository.findByPostedBy(postedBy);
    };
}
