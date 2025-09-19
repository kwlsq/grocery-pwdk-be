package com.pwdk.grocereach.geocoding.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacesAutocompleteResponse {
    private String status;
    private List<Prediction> predictions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prediction {
        private String description;
        private String placeId;
        private List<MatchedSubstring> matchedSubstrings;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MatchedSubstring {
            private Integer length;
            private Integer offset;
        }
    }
}