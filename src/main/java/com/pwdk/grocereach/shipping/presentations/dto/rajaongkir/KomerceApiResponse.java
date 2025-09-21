package com.pwdk.grocereach.shipping.presentations.dto.rajaongkir;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KomerceApiResponse {
    private KomerceMetaData meta;
    private List<KomerceShippingData> data;
}

