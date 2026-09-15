package org.example.filecloud.dao.book;

public enum BookGenre {

    FANTASY("Фентезі"),
    SCIENCE_FICTION("Наукова фантастика"),
    ADVENTURE("Пригоди"),
    DETECTIVE("Детектив"),
    THRILLER("Трилер"),
    HORROR("Жахи"),
    ROMANCE("Романтика"),
    DRAMA("Драма"),
    COMEDY("Комедія"),
    HISTORICAL("Історичний"),
    CRIME("Кримінал"),
    WESTERN("Вестерн"),
    DYSTOPIA("Антиутопія"),
    POST_APOCALYPTIC("Постапокаліптика"),
    MYSTERY("Містика"),

    SCIENCE("Наука"),
    MATHEMATICS("Математика"),
    PHYSICS("Фізика"),
    CHEMISTRY("Хімія"),
    BIOLOGY("Біологія"),
    ASTRONOMY("Астрономія"),
    PSYCHOLOGY("Психологія"),
    PHILOSOPHY("Філософія"),
    HISTORY("Історія"),
    GEOGRAPHY("Географія"),

    ECONOMICS("Економіка"),
    PROGRAMMING("Програмування"),
    COMPUTER_SCIENCE("Комп'ютерні науки"),
    BUSINESS("Бізнес"),
    FINANCE("Фінанси"),
    MANAGEMENT("Менеджмент"),
    MARKETING("Маркетинг"),

    SELF_DEVELOPMENT("Саморозвиток"),
    EDUCATION("Освіта"),

    BIOGRAPHY("Біографія"),
    AUTOBIOGRAPHY("Автобіографія"),
    MEMOIR("Мемуари"),

    POETRY("Поезія"),
    JOURNALISM("Журналістика"),
    ART("Мистецтво"),
    COOKING("Кулінарія"),
    TRAVEL("Подорожі"),
    SPORT("Спорт"),

    CHILDREN("Дитяча література"),
    COMICS("Комікси"),
    MANGA("Манґа"),
    RELIGION("Релігія"),
    ENCYCLOPEDIA("Енциклопедія"),
    REFERENCE("Довідкова література");

    private final String ukrainianName;

    BookGenre(String ukrainianName) {
        this.ukrainianName = ukrainianName;
    }

    public String getUkrainianName() {
        return ukrainianName;
    }

    @Override
    public String toString() {
        return ukrainianName;
    }
}
