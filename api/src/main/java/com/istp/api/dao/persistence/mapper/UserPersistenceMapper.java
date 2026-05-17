package com.istp.api.dao.persistence.mapper;

import com.istp.api.dao.persistence.entity.UserEntity;
import com.istp.api.service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    User toModel(UserEntity entity);
}
