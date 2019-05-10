package fr.progilone.pgcn.exception.message;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Stream;

public class PgcnList<T> implements Iterable<T> {

    private final Set<T> elements = Collections.synchronizedSet(new HashSet<>());

    public Collection<T> get() {
        return Collections.unmodifiableCollection(elements);
    }

    public boolean add(final T element) {
        return elements.add(element);
    }

    public boolean addAll(final PgcnList<T> semList) {
        return semList != null && elements.addAll(semList.get());
    }

    public boolean addAll(final Collection<? extends T> c) {
        return elements.addAll(c);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public Stream<T> stream() {
        return elements.stream();
    }

    public int size() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        return StringUtils.join(elements, ", ");
    }
}
