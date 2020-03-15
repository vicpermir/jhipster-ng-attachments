package io.github.vicpermir.service.mapper;


import io.github.vicpermir.domain.*;
import io.github.vicpermir.service.dto.ReportDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Report} and its DTO {@link ReportDTO}.
 */
@Mapper(componentModel = "spring", uses = {AttachmentMapper.class})
public interface ReportMapper extends EntityMapper<ReportDTO, Report> {


    @Mapping(target = "removeAttachments", ignore = true)

    default Report fromId(Long id) {
        if (id == null) {
            return null;
        }
        Report report = new Report();
        report.setId(id);
        return report;
    }
}
