package com.tanfed.user.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<List<UserRole>, String> {

	private static final String SEPARATOR = ",";

	@Override
	public String convertToDatabaseColumn(List<UserRole> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return null;
		}
		return attribute.stream().map(Enum::name).collect(Collectors.joining(SEPARATOR));
	}

	@Override
	public List<UserRole> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}
		return Arrays.stream(dbData.split(SEPARATOR)).map(UserRole::valueOf).collect(Collectors.toList());
	}

}
