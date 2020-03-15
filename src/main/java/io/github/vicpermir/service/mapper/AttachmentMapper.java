package io.github.vicpermir.service.mapper;


import io.github.vicpermir.domain.*;
import io.github.vicpermir.service.dto.AttachmentDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Attachment} and its DTO {@link AttachmentDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {


    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "removeReports", ignore = true)
    @Mapping(target = "uploadDate", ignore = true)
    Attachment toEntity(AttachmentDTO attachmentDTO);

    default Attachment fromId(Long id) {
        if (id == null) {
            return null;
        }
        Attachment attachment = new Attachment();
        attachment.setId(id);
        return attachment;
    }
}
