package ksamel.bot.realt;

import static ksamel.bot.core.Utils.doPost;

import java.util.List;
import ksamel.bot.core.Apartment;
import ksamel.bot.core.ApartmentFetchService;
import ksamel.bot.core.ApartmentFilter;
import ksamel.bot.realt.model.RealtResponse;
import ksamel.bot.realt.model.Result;
import ksamel.bot.realt.model.SearchObject;

public class RealtApartmentFeatchService implements ApartmentFetchService {

    private static final String NAME = "realt";
    private static final String URL = "https://realt.by/bff/graphql";
    private static final String POST_BODY = """
            {
                "operationName": "searchObjects",
                "variables": {
                  "data": {
                    "where": {
                      "rooms": [
                        "1",
                        "2"
                      ],
                      "seller": "true",
                      "priceFrom": "%s",
                      "priceTo": "%s",
                      "priceType": "840",
                      "addressV2": [
                        {
                          "townUuid": "4cb07174-7b00-11eb-8943-0cc47adabd66"
                        }
                      ],
                      "category": 2
                    },
                    "pagination": {
                      "page": 1,
                      "pageSize": 2
                    },
                    "sort": [
                      {
                        "by": "createdAt",
                        "order": "DESC"
                      }
                    ],
                    "extraFields": null,
                    "isReactAdaptiveUA": false
                  }
                },
                "query": "query searchObjects($data: GetObjectsByAddressInput!) {searchObjects(data: $data) {body {results {uuid createdAt updatedAt price priceCurrency pricePerM2 pricePerM2Max pricePerPerson code address location }}...StatusAndErrors}}fragment StatusAndErrors on INullResponse {success errors {code message}}"
            }
            """;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Apartment> getApartments(ApartmentFilter apartmentFilter) {
        String postBody = String.format(POST_BODY, apartmentFilter.getPriceUsdFrom(), apartmentFilter.getPriceUsdTo());
        RealtResponse response = doPost(URL, postBody, RealtResponse.class);
        SearchObject searchObject = response.getData().getSearchObjects();
        if (!searchObject.getSuccess()) {
            throw new RuntimeException("Unexpected error, while do post to realt");
        }
        return searchObject.getBody()
                           .getResults()
                           .stream()
                           .map(this::buildApartment)
                           .toList();
    }

    private Apartment buildApartment(Result result) {
        return Apartment.builder()
                        .price(result.getPrice())
                        .apartmentId(result.getCode().intValue())
                        .address(result.getAddress())
                        .source(getName())
                        .updateDate(result.getUpdatedAt())
                        .link(buildLink(result.getCode()))
                        .longitude(result.getLocation().get(0))
                        .latitude(result.getLocation().get(1))
                        .build();
    }

    private String buildLink(Long code) {
        return String.format("https://realt.by/rent-flat-for-long/object/%s/", code);
    }
}
