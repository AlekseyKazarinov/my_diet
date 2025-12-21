package com.mydiet.mydiet.config;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    private String status;
    private String message;
    private String details;

}
