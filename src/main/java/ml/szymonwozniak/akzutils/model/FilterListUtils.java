package ml.szymonwozniak.akzutils.model;

import javafx.collections.transformation.FilteredList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FilterListUtils {
    List<MyPredicate> predicates;
    FilteredList<Course> filteredList;

    public FilterListUtils(){
        predicates = new ArrayList<>();
    }

    public FilterListUtils(FilteredList filteredList){
        predicates = new ArrayList<>();
        this.filteredList = filteredList;
    }

    public void setFilteredList(FilteredList filteredList){
        this.filteredList = filteredList;
        updateListPredicates();
    }

    public void addPredicate(MyPredicate.PredicateType predicateType, Predicate predicate){
        removePredicate(predicateType);
        predicates.add(new MyPredicate(predicateType, predicate));
        updateListPredicates();
    }

    public void removePredicate(MyPredicate.PredicateType predicateType){
        predicates.removeIf(t -> t.type == predicateType);
        updateListPredicates();
    }

    public void updateListPredicates(){
        filteredList.setPredicate(MyPredicate.flatten(predicates).stream().reduce(Predicate::and).orElse(x->true));
    }

    public FilteredList<Course> getFilteredList() {
        return filteredList;
    }
}
