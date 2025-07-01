package ksamel.bot.core.yandex.model;

import com.google.gson.annotations.SerializedName;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoObjectCollection {

    @SerializedName("featureMember")
    private Collection<FeatureMember> featureMembers;
}
