package com.istp.api.dao.persistence.mapper;

import com.istp.api.dao.persistence.entity.RepairRequestEntity;
import com.istp.api.service.model.RepairRequest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepairRequestPersistenceMapper {
    RepairRequest toModel(RepairRequestEntity entity);

    List<RepairRequest> toModels(List<RepairRequestEntity> entities);

    RepairRequestEntity toEntity(RepairRequest model);
}
