package ksamel.bot.core;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentFilter {

    private Integer priceUsdFrom;
    private Integer priceUsdTo;
    private Date updatedFrom;
}
