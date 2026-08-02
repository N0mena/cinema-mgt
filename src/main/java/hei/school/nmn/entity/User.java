package hei.school.nmn.entity;

import lombok.Builder;

import java.util.UUID;

@Builder
public record User(UUID id, String firstName, String lastName, String userName, String email) {
}
