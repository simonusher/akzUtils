package ml.szymonwozniak.akzutils.model;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MyPredicate {
    public enum PredicateType {
        SEARCH, COURSETYPE, TIMETABLE, NOT, FREE, STATIONARY, GRADE
    }
    PredicateType type;
    Predicate predicate;

    public MyPredicate(PredicateType type, Predicate predicate) {
        this.type = type;
        this.predicate = predicate;
    }

    public static List<Predicate> flatten(List<MyPredicate> myPredicateList){
        return myPredicateList.stream().map(t -> t.predicate).collect(Collectors.toList());
    }
}