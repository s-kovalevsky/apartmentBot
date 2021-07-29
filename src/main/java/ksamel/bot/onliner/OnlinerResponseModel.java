package ksamel.bot.onliner;

import java.util.List;


public class OnlinerResponseModel {
    private List<OnlinerApartmentModel> apartments;
    private Integer total;
    private Page page;

    public List<OnlinerApartmentModel> getApartments() {
        return apartments;
    }

    public void setApartments(List<OnlinerApartmentModel> apartments) {
        this.apartments = apartments;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public static class Page {
        private Integer limit;
        private Integer items;
        private Integer current;
        private Integer last;

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Integer getItems() {
            return items;
        }

        public void setItems(Integer items) {
            this.items = items;
        }

        public Integer getCurrent() {
            return current;
        }

        public void setCurrent(Integer current) {
            this.current = current;
        }

        public Integer getLast() {
            return last;
        }

        public void setLast(Integer last) {
            this.last = last;
        }
    }
}
