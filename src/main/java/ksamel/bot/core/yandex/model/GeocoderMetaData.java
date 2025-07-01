package ksamel.bot.core.yandex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeocoderMetaData {

    @SerializedName("Address")
    private Address address;
}
