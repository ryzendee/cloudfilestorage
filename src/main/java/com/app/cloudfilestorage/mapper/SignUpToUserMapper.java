package com.app.cloudfilestorage.mapper;

import com.app.cloudfilestorage.dto.request.SignupRequest;
import com.app.cloudfilestorage.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SignUpToUserMapper extends BaseMapper<SignupRequest, UserEntity> {
}
