package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.UserSessionDto;
import com.app.cloudfilestorage.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityToUserSessionDtoMapper extends BaseMapper<UserEntity, UserSessionDto> {

    @Override
    UserSessionDto map(UserEntity from);
}
