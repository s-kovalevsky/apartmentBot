package ksamel.bot.core.yandex.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaDataProperty {

    @SerializedName("GeocoderMetaData")
    private GeocoderMetaData geocoderMetaData;
}
