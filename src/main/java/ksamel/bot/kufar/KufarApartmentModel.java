package ksamel.bot.kufar;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KufarApartmentModel {

    @SerializedName("account_parameters")
    private List<Parameters> accountParameters;
    @SerializedName("ad_link")
    private String link;
    @SerializedName("ad_id")
    private Integer id;
    @SerializedName("list_time")
    private Date listTime;
    @SerializedName("company_ad")
    private Boolean companyAd;

    @SerializedName("price_usd")
    private Double priceUsd;
    @SerializedName("ad_parameters")
    private List<Parameters> adParameters;

    @Getter
    @Setter
    public static class Parameters {

        private Object pl;
        private Object vl;
        private Object p;
        private Object v;
        private Object pu;
    }
}
