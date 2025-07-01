package ksamel.bot.core;

import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.Date;
import java.util.Set;
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
    private Set<String> locations;

    @Override
    public String toString() {
        return format("""
                      Фильтр
                      Цена: от %s до %s
                      Локации: %s
                      """,
                      priceUsdFrom,
                      priceUsdTo,
                      isEmpty(locations) ? "Любые" : locations);
    }
}
