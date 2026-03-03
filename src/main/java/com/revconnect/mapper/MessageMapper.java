//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.revconnect.mapper;

import com.revconnect.dto.MessageDto;
import com.revconnect.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageDto toDto(Message message) {
        if (message == null) {
            return null;
        } else {
            MessageDto dto = new MessageDto();
            dto.setId(message.getId());
            dto.setContent(message.getContent());
            dto.setRead(message.isRead());
            dto.setSentAt(message.getSentAt());
            if (message.getSender() != null) {
                dto.setSenderUsername(message.getSender().getUsername());
            }

            if (message.getReceiver() != null) {
                dto.setReceiverUsername(message.getReceiver().getUsername());
            }

            return dto;
        }
    }
}
