package org.example.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Command {
    private CommandFlag flag;
    private String payload;

    @Override
    public String toString() {
        return "Command{" +
                "flag=" + flag +
                ", payload='" + payload + '\'' +
                '}';
    }
}
