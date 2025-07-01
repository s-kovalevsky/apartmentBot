package ksamel.bot.domovita.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Apartment {

    private String url;
    private Address address;
    private Geo geo;
}
