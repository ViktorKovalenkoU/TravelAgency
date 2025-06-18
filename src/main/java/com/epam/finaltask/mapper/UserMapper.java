package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.SignUpRequestDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = Role.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "vouchers", ignore = true)
    @Mapping(target = "role", expression = "java(Role.valueOf(dto.getRole()))")
    @Mapping(target = "name", source = "firstName")
    @Mapping(target = "surname", source = "lastName")
    User toUser(SignUpRequestDTO dto);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserDTO toUserDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "vouchers", ignore = true)
    @Mapping(target = "role", expression = "java(Role.valueOf(dto.getRole()))")
    User toUser(UserDTO dto);
}
