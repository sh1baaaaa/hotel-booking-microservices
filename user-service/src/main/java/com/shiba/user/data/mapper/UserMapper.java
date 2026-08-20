package com.shiba.user.data.mapper;

import com.shiba.user.data.dto.UserResponseDTO;
import com.shiba.user.data.dto.UserUpdateRequestDTO;
import com.shiba.user.data.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponseDTO toResponseDTO(UserEntity userEntity);

    UserEntity updateEntityFromRequestDTO(UserUpdateRequestDTO userUpdateRequestDTO,
                                          @MappingTarget UserEntity userEntity);
}
