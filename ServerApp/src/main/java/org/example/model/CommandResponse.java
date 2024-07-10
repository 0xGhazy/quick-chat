package org.example.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CommandResponse {
    private CommandStatus status;
    private String message;
    private String payload;
    private String metadata;
}
