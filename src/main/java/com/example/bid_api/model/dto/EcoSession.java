package com.example.bid_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EcoSession {
    private String cakePhp;
    private String csrfToken;
    private String awsalb;
    private String awsalbcors;
    private String browserId;
}
