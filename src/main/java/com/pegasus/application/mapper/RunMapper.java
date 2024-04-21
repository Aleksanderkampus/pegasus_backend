package com.pegasus.application.mapper;

import com.pegasus.application.dto.RunDto;
import com.pegasus.application.mapper.common.EntityMapper;
import com.pegasus.application.models.Run;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RunMapper extends EntityMapper<Run, RunDto> {
}
