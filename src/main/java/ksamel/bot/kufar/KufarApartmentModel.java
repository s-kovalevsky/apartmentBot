package ksamel.bot.kufar;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class KufarApartmentModel {
    @SerializedName("account_parameters")
    private List<AccountParameters> accountParameters;
    @SerializedName("ad_link")
    private String link;
    @SerializedName("ad_id")
    private Integer id;
    @SerializedName("list_time")
    private Date listTime;
    @SerializedName("company_ad")
    private Boolean companyAd;

    @SerializedName("price_usd")
    private Double priceUsd ;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getListTime() {
        return listTime;
    }

    public void setListTime(Date listTime) {
        this.listTime = listTime;
    }

    public Boolean getCompanyAd() {
        return companyAd;
    }

    public void setCompanyAd(Boolean companyAd) {
        this.companyAd = companyAd;
    }

    public Double getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(Double priceUsd) {
        this.priceUsd = priceUsd;
    }
    //
    public static class AccountParameters {
        private String pl;
        private String vl;
        private String p;
        private String v;
        private String pu;

        public String getPl() {
            return pl;
        }

        public void setPl(String pl) {
            this.pl = pl;
        }

        public String getVl() {
            return vl;
        }

        public void setVl(String vl) {
            this.vl = vl;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public String getPu() {
            return pu;
        }

        public void setPu(String pu) {
            this.pu = pu;
        }
    }

    public List<AccountParameters> getAccountParameters() {
        return accountParameters;
    }

    public void setAccountParameters(List<AccountParameters> accountParameters) {
        this.accountParameters = accountParameters;
    }
}
